/*******************************************************************************
 * Copyright (c) 2014 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.bund.bfr.knime.flink.scala;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import org.apache.maven.cli.MavenCli;
import org.knime.core.util.crypto.HexUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.google.common.collect.Lists;

/**
 * Creates and caches the jars build from the Scala fragment. A hashing function identifies the same program to avoid
 * unnecessary compilations.
 */
public class JarBuilder {
	private static Path cacheDir;

	private final static Charset UTF8 = Charset.forName("UTF-8");

	static {
		try {
			cacheDir =
				Files.createDirectories(FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"),
					"flink_snippets"));
		} catch (IOException e) {
			throw new IllegalStateException("Cannot create temporary directory.", e);
		}
	}

	private MavenCli cli = new MavenCli();

	private final MessageDigest hash;

	{
		try {
			this.hash = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public void removeJar(String partialScript, Path[] additionalJars) throws IOException {
		Path cachedJar = this.getCachedJar(partialScript, additionalJars);
		if (Files.exists(cachedJar))
			Files.delete(cachedJar);
	}

	public void buildJar(String partialScript, Path targetJar, Path[] additionalJars, String javaHome)
			throws IOException {
		Path tempProject = this.createEmptyMavenProject(targetJar);
		Path srcTarget = tempProject.resolve("src/main/scala/");
		Files.createDirectories(srcTarget);
		Files.write(srcTarget.resolve("Job.scala"), partialScript.getBytes(UTF8));
		List<String> options = Lists.newArrayList("package", "-q");
		if (javaHome != null && !javaHome.isEmpty()) {
			options.add("-PCustomJDK");
			options.add("-Djdk.home=\"" + new File(javaHome).getAbsolutePath() + "\"");
		}
		this.invokeMaven(options.toArray(new String[0]), tempProject.toAbsolutePath().toString(),
			"Cannot compile project (error code %s).");
		Files.move(this.findGeneratedJar(tempProject), targetJar);
		this.deleteAll(tempProject);
		if (additionalJars != null) {
			// TODO: append files in jar
		}
	}

	public Path getScriptJar(ScalaSnippetSettings settings) throws IOException {
		Path cachedJar = this.getCachedJar(settings.getScript(), settings.getJarPaths());
		if (!Files.exists(cachedJar))
			this.buildJar(settings.getScript(), cachedJar, settings.getJarPaths(), settings.getJavaHome());
		return cachedJar;
	}

	private Path createEmptyMavenProject(Path targetJar) throws IOException {
		Bundle currentBundle = FrameworkUtil.getBundle(getClass());
		URL bundleBase = currentBundle.getEntry("/");
		Enumeration<String> resources = currentBundle.getEntryPaths("maven/");
		// Bundle bundle = Platform.getBundle("de.vogella.example.readfile");
		// URL fileURL = bundle.getEntry("files/test.txt");

		Path tempDir = Files.createTempDirectory(targetJar.getFileName().toString());
		while (resources.hasMoreElements()) {
			String resource = resources.nextElement();
			URL resourceURL = new URL(bundleBase, resource);
			try (InputStream in = resourceURL.openStream()) {
				Files.copy(in, tempDir.resolve(Paths.get(resource).getFileName()));
			}
		}

		// Files.delete(tempDir);
		// Files.createDirectories(tempDir);
		//
		// // Create a minimal project (usually word count).
		// // Here, we could also take a pre-configured pom.xml and save one Maven execution.
		// // However, this approach is more flexible, because the quickstart example may be fixed by the Flink
		// developers
		// // in case of bugs.
		// String[] mvnCommand = { "archetype:generate",
		// "-DarchetypeArtifactId=flink-quickstart-scala",
		// "-DarchetypeGroupId=org.apache.flink",
		// "-DarchetypeVersion=0.7.0-incubating",
		// "-DgroupId=org.apache.flink",
		// "-DartifactId=generated",
		// "-Dversion=0.1",
		// "-Dpackage=org.apache.flink",
		// "-DinteractiveMode=false" };
		//
		// this.invokeMaven(mvnCommand, tempDir.toAbsolutePath().toString(),
		// "Cannot create archetype project (error code %s).\nPlease make sure you have Internet access.");
		//
		// // Delete the given scala sources. We retain only the pom.xml and the assembly configurations.
		// this.deleteInDir(tempDir.resolve("generated/src"), ".*\\.scala");
		// return tempDir.resolve("generated");
		return tempDir;
	}

	private void deleteAll(Path dir) throws IOException {
		Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return super.postVisitDirectory(dir, exc);
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return super.visitFile(file, attrs);
			}
		});
	}

	// private void deleteInDir(Path dir, String filePattern) throws IOException {
	// final Pattern pattern = Pattern.compile(filePattern);
	// Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
	// /*
	// * (non-Javadoc)
	// * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object,
	// * java.nio.file.attribute.BasicFileAttributes)
	// */
	// @Override
	// public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	// if (pattern.matcher(file.getFileName().toString()).matches())
	// Files.delete(file);
	// return super.visitFile(file, attrs);
	// }
	// });
	// }

	private Path findGeneratedJar(Path emptyMavenProject) throws IOException {
		try (DirectoryStream<Path> files = Files.newDirectoryStream(emptyMavenProject.resolve("target"))) {
			for (Path file : files) {
				String fileName = file.getName(file.getNameCount() - 1).toString();
				if (fileName.endsWith(".jar"))
					return file;
			}
		}
		throw new IllegalStateException("Cannot find generated jar in " + emptyMavenProject);
	}

	public Path getCachedJar(String partialScript, Path[] jars) throws IOException, FileNotFoundException {
		this.hash.update(partialScript.getBytes(UTF8));
		Arrays.sort(jars, new Comparator<Path>() {
			@Override
			public int compare(Path path1, Path path2) {
				return path1.toString().compareTo(path2.toString());
			}
		});
		for (Path jarPath : jars) {
			byte[] buffer = new byte[65536];
			try (FileInputStream fos = new FileInputStream(jarPath.toFile())) {
				int read;
				while ((read = fos.read(buffer)) != -1)
					this.hash.update(buffer, 0, read);
			}
		}

		byte[] digest = this.hash.digest();
		Path cachedJar = cacheDir.resolve(HexUtils.bytesToHex(digest) + ".jar");
		return cachedJar;
	}

	private void invokeMaven(String[] cmd, String path, String errorMessage) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(baos, true);
		int returnCode = this.cli.doMain(cmd, path, stream, stream);
		baos.close();
		if (returnCode != 0)
			throw new IOException(String.format(errorMessage + "\n" + baos.toString("utf-8"),
				returnCode));
	}

}

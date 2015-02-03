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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import de.bund.bfr.knime.flink.Parameter;
import de.bund.bfr.knime.flink.Parameter.Type;
import de.bund.bfr.knime.flink.scala.ScalaSnippetDocument.Section;

/**
 * Collects the information about the Scala snippet and updates the settings and documents appropriately.<br/>
 * <br/>
 * This class is heavily inspired by JSnippet plugin from Heiko Hofer.
 */
public class ScalaSnippet {

	/** The version 1.x of the java snippet. */
	public static final String VERSION_1_X = "version 1.x";

	private static Map<Type, String> TypeParserPattern = new EnumMap<>(Type.class);

	private static Map<Type, String> TypeToScalaType = new EnumMap<>(Type.class);

	static {
		TypeToScalaType.put(Type.STRING, "String");
		TypeToScalaType.put(Type.DOUBLE, "Double");
		TypeToScalaType.put(Type.INTEGER, "Integer");
		TypeParserPattern.put(Type.STRING, "%s");
		TypeParserPattern.put(Type.DOUBLE, "%s.toDouble");
		TypeParserPattern.put(Type.INTEGER, "%s.toInt");
	}

	protected boolean m_dirty;

	private GuardedDocument<Section> m_document;

	private ScalaSnippetSettings m_settings;

	/**
	 * Create a new snippet.
	 */
	public ScalaSnippet() {
	}

	/**
	 * Get the document with the code of the snippet.
	 * 
	 * @return the document
	 */
	public GuardedDocument<Section> getDocument() {
		// Lazy initialization of the document
		if (this.m_document == null) {
			this.m_document = this.createDocument();
			this.m_document.addDocumentListener(new DocumentListener() {

				@Override
				public void changedUpdate(final DocumentEvent e) {
					ScalaSnippet.this.m_dirty = true;
				}

				@Override
				public void insertUpdate(final DocumentEvent e) {
					ScalaSnippet.this.m_dirty = true;
				}

				@Override
				public void removeUpdate(final DocumentEvent e) {
					ScalaSnippet.this.m_dirty = true;
				}
			});
			this.initDocument(this.m_document);
		}
		return this.m_document;
	}

	/**
	 * @return
	 * @see de.bund.bfr.knime.flink.scala.ScalaSnippetSettings#getInVars()
	 */
	public List<Parameter> getParameters() {
		return this.m_settings.getParameters();
	}

	/**
	 * Get the updated settings java snippet.
	 * 
	 * @return the settings
	 */
	public ScalaSnippetSettings getSettings() {
		this.updateSettings();
		return this.m_settings;
	}

	/**
	 * Set the list of additional jar files to be added to the class path
	 * of the snippet.
	 * 
	 * @param jarFiles
	 *        the jar files
	 */
	public void setJarFiles(final String[] jarFiles) {
		this.m_settings.setJarFiles(jarFiles);
	}

	/**
	 * @param inVars
	 * @see de.bund.bfr.knime.flink.scala.ScalaSnippetSettings#setInVars(de.bund.bfr.knime.flink.scala.List<Field>)
	 */
	public void setParameters(List<Parameter> parameters) {
		this.m_settings.setParameters(parameters);
		if (null != this.m_document)
			this.initGuardedSection(this.m_document);
	}

	/**
	 * Create a new snippet with the given settings.
	 * 
	 * @param settings
	 *        the settings
	 */
	public void setSettings(final ScalaSnippetSettings settings) {
		this.m_settings = settings;
		this.setJarFiles(settings.getJarFiles());
		this.setParameters(this.getParameters());
		this.init();
	}

	/**
	 * Get the list of default imports. Override this method to append or
	 * modify this list.
	 * 
	 * @return the list of default imports
	 */
	protected String[] getSystemImports() {
		return new String[] { "org.apache.flink.api.scala._" };
	}

	/** Create the document with the default skeleton. */
	private GuardedDocument<Section> createDocument() {
		return new ScalaSnippetDocument();
	}

	/**
	 * Create the system variable (input and output) section of the snippet.
	 */
	private String createFieldsSection() {
		StringBuilder out = new StringBuilder();
		out.append("  private val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment\n");
		if (this.m_settings != null && this.m_settings.getParameters().size() > 0) {
			out.append("  // Parsed input parameters; all parameters have been initialized\n");
			for (Parameter parameter : this.m_settings.getParameters()) {
				String scalaType = TypeToScalaType.get(parameter.getType());
				if (parameter.isOptional())
					out.append(String.format("  private var %s: Option[%s] = None\n", parameter.getName(),
						scalaType));
				else
					out.append(String.format("  private var %s: %s = null.asInstanceOf[%2$s]\n", parameter.getName(), scalaType));
			}
		}
		return out.toString();
	}

	/**
	 * Create the imports section for the snippet's document.
	 */
	private String createImportsSection() {
		StringBuilder imports = new StringBuilder();
		imports.append("// Imports\n");
		for (String s : this.getSystemImports()) {
			imports.append("import ");
			imports.append(s);
			imports.append(";\n");
		}
		return imports.toString();
	}

	/**
	 * Create the system variable (input and output) section of the snippet.
	 */
	private String createMainSection() {
		StringBuilder out = new StringBuilder();
		out.append("  def main(args: Array[String]) {\n");
		out.append("    // Generated parameter extraction\n");
		if (this.m_settings != null && this.m_settings.getParameters().size() > 0) {
			List<Parameter> parameters = this.m_settings.getParameters();
			for (int index = 0; index < parameters.size(); index++) {
				String argSelector = String.format("args(%d)", index);
				Parameter parameter = parameters.get(index);
				String parser = String.format(TypeParserPattern.get(parameter.getType()), argSelector);
				if (parameter.isOptional())
					out.append(String.format("    if(args.length > %d && args(%1$d) != \"\")\n      %s = Option(%s)\n",
						index, parameter.getName(), parser));
				else
					out.append(String.format("    %s = %s\n", parameter.getName(), parser));
			}
		}
		out.append("    performTask\n");
		out.append("  }");
		return out.toString();
	}

	private void init() {
		if (null != this.m_document)
			this.initDocument(this.m_document);
	}

	/** Initialize document with information from the settings. */
	private void initDocument(final GuardedDocument<Section> doc) {
		try {
			if (this.m_settings != null) {
				String script = this.m_settings.getScript();
				if (script != null && !script.isEmpty()) {
					doc.setBreakGuarded(true);
					doc.replace(0, doc.getLength(), this.m_settings.getScript(), null);
					doc.setBreakGuarded(false);
					Map<Section, int[]> scriptParts = this.m_settings.getScriptParts();
					for (Map.Entry<Section, int[]> scriptPart : scriptParts.entrySet()) {
						doc.getGuardedSection(scriptPart.getKey()).setStart(
							doc.createPosition(scriptPart.getValue()[0]));
						doc.getGuardedSection(scriptPart.getKey()).setEnd(doc.createPosition(scriptPart.getValue()[1]));
					}
				}
			} else
				this.initGuardedSection(doc);
			// doc.replaceBetween(Section.Imports, Section.JobStart, this.m_settings.getScriptImports());
			// doc.replaceBetween(GUARDED_FIELDS, Section.TaskStart, this.m_settings.getScriptFields());
			// doc.replaceBetween(Section.TaskStart, GUARDED_TASK_END, this.m_settings.getScriptBody());
			// }
		} catch (BadLocationException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * Initialize Section.Imports and GUARDED_FIELDS with information from
	 * the settings.
	 */
	private void initGuardedSection(final GuardedDocument<Section> doc) {
		try {
			GuardedSection imports = doc.getGuardedSection(Section.Imports);
			imports.setText(this.createImportsSection());
			GuardedSection fields = doc.getGuardedSection(Section.Fields);
			fields.setText(this.createFieldsSection());
			GuardedSection main = doc.getGuardedSection(Section.MainStart);
			main.setText(this.createMainSection());
		} catch (BadLocationException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private void updateSettings() {
		try {
			GuardedDocument<Section> doc = this.getDocument();
			this.m_settings.setScript(doc.getText(0, doc.getLength()));
			Set<Section> guardedSections = doc.getGuardedSections();
			Map<Section, int[]> sectionParts = new EnumMap<>(Section.class);
			for (Section section : guardedSections) {
				GuardedSection guardedSection = doc.getGuardedSection(section);
				sectionParts.put(section, new int[] { guardedSection.getStart().getOffset(),
					guardedSection.getEnd().getOffset() });
			}
			this.m_settings.setScriptParts(sectionParts);
		} catch (BadLocationException e) {
			// this should never happen
			throw new IllegalStateException(e);
		}
	}
}

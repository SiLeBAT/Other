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

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.config.base.ConfigBaseRO;
import org.knime.core.node.config.base.ConfigBaseWO;

import de.bund.bfr.knime.flink.Parameter;
import de.bund.bfr.knime.flink.Parameter.Type;
import de.bund.bfr.knime.flink.scala.ScalaSnippetDocument.Section;

/**
 * The settings of the Scala snippet node. <br/>
 * <br/>
 * This class is heavily inspired by JSnippet plugin from Heiko Hofer.
 */
public class ScalaSnippetSettings {
	private static final String JAR_FILES = "jarFiles";

	private static final String PARAMETERS = "parameters";

	private static final String SCRIPT = "script";

	private static final String VERSION = "version";

	/** Custom jar files. */
	private String[] jarFiles = new String[0];

	/** Input variables definitions. */
	private List<Parameter> parameters = new ArrayList<>();

	private String script = "";

	/** Custom imports. */
	private Map<Section, int[]> scriptParts = new EnumMap<>(Section.class);

	/** The version of the java snippet. */
	private String version = ScalaSnippet.VERSION_1_X;

	/**
	 * Create a new instance.
	 */
	public ScalaSnippetSettings() {
	}

	/**
	 * @return the jarFiles
	 */
	public String[] getJarFiles() {
		return this.jarFiles;
	}

	public Path[] getJarPaths() {
		Path[] paths = new Path[this.jarFiles.length];
		FileSystem fs = FileSystems.getDefault();
		for (int index = 0; index < paths.length; index++)
			paths[index] = fs.getPath(this.jarFiles[index]);
		return paths;
	}

	/**
	 * Returns the inVars.
	 * 
	 * @return the inVars
	 */
	public List<Parameter> getParameters() {
		return this.parameters;
	}

	/**
	 * Returns the script.
	 * 
	 * @return the script
	 */
	public String getScript() {
		return this.script;
	}

	/**
	 * Returns the scriptParts.
	 * 
	 * @return the scriptParts
	 */
	public Map<Section, int[]> getScriptParts() {
		return this.scriptParts;
	}

	/**
	 * Loads parameters in NodeModel.
	 * 
	 * @param settings
	 *        To load from.
	 * @throws InvalidSettingsException
	 *         If incomplete or wrong.
	 */
	public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		for (Section section : Section.values())
			if (settings.containsKey(section.name()))
				this.scriptParts.put(section, settings.getIntArray(section.name()));
		this.script = settings.getString(SCRIPT);
		this.jarFiles = settings.getStringArray(JAR_FILES);
		this.parameters = this.loadParameters(settings, PARAMETERS, false);
		this.version = settings.getString(VERSION);
	}

	/**
	 * Loads parameters in Dialog.
	 * 
	 * @param settings
	 *        To load from.
	 */
	public void loadSettingsForDialog(final NodeSettingsRO settings) {
		for (Section section : Section.values())
			if (settings.containsKey(section.name()))
				this.scriptParts.put(section, settings.getIntArray(section.name(), new int[] { 0, 0 }));
		this.script = settings.getString(SCRIPT, null);
		this.jarFiles = settings.getStringArray(JAR_FILES, new String[0]);
		try {
			this.parameters = this.loadParameters(settings, PARAMETERS, true);
		} catch (InvalidSettingsException e) {
			throw new IllegalStateException("Cannot happen in safe mode", e);
		}
		this.version = settings.getString(VERSION, ScalaSnippet.VERSION_1_X);
	}

	/**
	 * Saves current parameters to settings object.
	 * 
	 * @param settings
	 *        To save to.
	 */
	public void saveSettings(final NodeSettingsWO settings) {
		for (Entry<Section, int[]> section : this.scriptParts.entrySet())
			settings.addIntArray(section.getKey().name(), section.getValue());
		settings.addString(SCRIPT, this.script);
		settings.addStringArray(JAR_FILES, this.jarFiles);
		this.saveParameters(settings, PARAMETERS, this.parameters);
		settings.addString(VERSION, this.version);
	}

	/**
	 * Sets the inVars to the specified value.
	 * 
	 * @param inVars
	 *        the inVars to set
	 */
	public void setParameters(List<Parameter> inVars) {
		if (inVars == null)
			throw new NullPointerException("inVars must not be null");

		this.parameters = inVars;
	}

	/**
	 * Sets the script to the specified value.
	 * 
	 * @param script
	 *        the script to set
	 */
	public void setScript(String script) {
		if (script == null)
			throw new NullPointerException("script must not be null");

		this.script = script;
	}

	/**
	 * Sets the scriptParts to the specified value.
	 * 
	 * @param scriptParts
	 *        the scriptParts to set
	 */
	public void setScriptParts(Map<Section, int[]> scriptParts) {
		if (scriptParts == null)
			throw new NullPointerException("scriptParts must not be null");

		this.scriptParts = scriptParts;
	}

	/**
	 * @return the version
	 */
	String getVersion() {
		return this.version;
	}

	/**
	 * @param jarFiles
	 *        the jarFiles to set
	 */
	void setJarFiles(final String[] jarFiles) {
		this.jarFiles = jarFiles;
	}

	private List<Parameter> loadParameters(NodeSettingsRO settings, String key, boolean safe)
			throws InvalidSettingsException {
		List<Parameter> fields = new ArrayList<>();
		if (!settings.containsKey(key))
			return fields;
		ConfigBaseRO config = settings.getConfigBase(key);
		int count = safe ? config.getInt("count", 0) : config.getInt("count");
		for (int index = 0; index < count; index++)
			fields.add(new Parameter(config.getString(String.format("name%d", index)),
				Type.valueOf(config.getString(String.format("type%d", index))),
				config.getBoolean(String.format("optional%d", index))));
		return fields;
	}

	private void saveParameters(final NodeSettingsWO settings, String key, List<Parameter> vars) {
		int count = vars.size();
		ConfigBaseWO config = settings.addConfigBase(key);
		config.addInt("count", count);
		for (int index = 0; index < count; index++) {
			config.addString(String.format("name%d", index), vars.get(index).getName());
			config.addString(String.format("type%d", index), vars.get(index).getType().name());
			config.addBoolean(String.format("optional%d", index), vars.get(index).isOptional());
		}
	}
}

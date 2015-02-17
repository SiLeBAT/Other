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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import de.bund.bfr.knime.flink.port.FlinkProgramObject;
import de.bund.bfr.knime.flink.port.FlinkProgramObjectSpec;

/**
 * This is the model implementation of FlinkScalaBuilder.
 * Compiles a Scala snippet into a Flink jar.
 * 
 * @author Arvid Heise
 */
public class FlinkScalaBuilderNodeModel extends NodeModel {
	private JarBuilder jarBuilder = new JarBuilder();

	private ScalaSnippetSettings m_settings = new ScalaSnippetSettings();

	/**
	 * Constructor for the node model.
	 */
	protected FlinkScalaBuilderNodeModel() {
		super(new PortType[] {}, new PortType[] { FlinkProgramObject.TYPE });
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#configure(org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		try {
			Path executableJar =
				this.jarBuilder.getScriptJar(this.m_settings);
			FlinkProgramObjectSpec flinkProgramObject = new FlinkProgramObjectSpec();
			flinkProgramObject.getProgram().setJarPath(executableJar.toAbsolutePath().toString());
			flinkProgramObject.getProgram().setParameters(this.m_settings.getParameters());
			return new PortObjectSpec[] { flinkProgramObject };
		} catch (Exception e) {
			throw new InvalidSettingsException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#execute(org.knime.core.node.port.PortObject[],
	 * org.knime.core.node.ExecutionContext)
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec) throws Exception {
		Path executableJar = this.jarBuilder.getScriptJar(this.m_settings);

		FlinkProgramObject flinkProgramObject = new FlinkProgramObject();
		flinkProgramObject.getProgram().setJarPath(executableJar.toAbsolutePath().toString());
		flinkProgramObject.getProgram().setParameters(this.m_settings.getParameters());
		return new PortObject[] { flinkProgramObject };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		Path executableJar = this.jarBuilder.getCachedJar(this.m_settings.getScript(), this.m_settings.getJarPaths());
		if (executableJar != null && !Files.exists(executableJar)) {
			Path internalJar = internDir.toPath().resolve(executableJar.getFileName());
			if (Files.exists(internalJar))
				Files.copy(internalJar, executableJar, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		this.m_settings.loadSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// try {
		// this.jarBuilder.removeJar(m_settings.getScript(), m_settings.getJarPaths());
		// } catch (IOException e) {
		// logger.warn("Could not remove old jar", e);
		// }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		Path executableJar = this.jarBuilder.getCachedJar(this.m_settings.getScript(), this.m_settings.getJarPaths());
		if (executableJar != null && Files.exists(executableJar))
			Files.copy(executableJar, internDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		this.m_settings.saveSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {

		ScalaSnippetSettings snippetSettings = new ScalaSnippetSettings();
		snippetSettings.loadSettings(settings);

		try {
			// test compile
			this.jarBuilder.getScriptJar(snippetSettings);
		} catch (Exception e) {
			throw new InvalidSettingsException(e.getMessage());
		}
	}

}

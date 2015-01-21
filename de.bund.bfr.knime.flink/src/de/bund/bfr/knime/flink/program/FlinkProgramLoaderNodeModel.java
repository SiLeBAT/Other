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
package de.bund.bfr.knime.flink.program;

import java.io.File;
import java.io.IOException;

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

import de.bund.bfr.knime.flink.FlinkProgramWithUsage;
import de.bund.bfr.knime.flink.SerializationHelper;
import de.bund.bfr.knime.flink.port.FlinkProgramObject;
import de.bund.bfr.knime.flink.port.FlinkProgramObjectSpec;

/**
 * This is the model implementation of FlinkProgramLoader.
 * Loads an existing Flink program.
 * 
 * @author Arvid Heise
 */
public class FlinkProgramLoaderNodeModel extends NodeModel {

	private FlinkProgramWithUsage program = new FlinkProgramWithUsage();

	/**
	 * Constructor for the node model.
	 */
	protected FlinkProgramLoaderNodeModel() {
		super(new PortType[0], new PortType[] { FlinkProgramObject.TYPE });
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#configure(org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		if (this.program.getJarPath() == null)
			throw new InvalidSettingsException("Path to jar not specified.");
		FlinkProgramObjectSpec flinkProgramObject = new FlinkProgramObjectSpec();
		flinkProgramObject.getProgram().setJarPath(this.program.getJarPath());
		flinkProgramObject.getProgram().setParameters(this.program.getParameters());
		return new PortObjectSpec[] { flinkProgramObject };
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#execute(org.knime.core.node.port.PortObject[],
	 * org.knime.core.node.ExecutionContext)
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec) throws Exception {
		FlinkProgramObject flinkProgramObject = new FlinkProgramObject();
		flinkProgramObject.getProgram().setJarPath(this.program.getJarPath());
		flinkProgramObject.getProgram().setParameters(this.program.getParameters());
		return new PortObject[] { flinkProgramObject };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		this.program = SerializationHelper.readObject(settings, "program");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		SerializationHelper.writeObject(settings, "program", this.program);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		FlinkProgramWithUsage program = SerializationHelper.readObject(settings, "program");
		if (program.getJarPath() == null)
			throw new InvalidSettingsException("Path to jar not specified.");
	}

}

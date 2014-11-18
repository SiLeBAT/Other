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
package de.bund.bfr.knime.flink.jm;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import de.bund.bfr.knime.flink.FlinkJobManagerSettings;
import de.bund.bfr.knime.flink.port.FlinkJobmanagerConnectionObject;
import de.bund.bfr.knime.flink.port.FlinkJobmanagerConnectionObjectSpec;

/**
 * This is the model implementation of FlinkJobManagerConnection.
 * Connects to the Flink job manager.
 * 
 * @author Arvid Heise
 */
public class FlinkJobManagerConnectionNodeModel extends NodeModel {

	/**
	 * the settings key which is used to retrieve and store the settings (from
	 * the dialog or from a settings file) (package visibility to be usable from
	 * the dialog).
	 */
	static final String CFGKEY_JM_PORT = "Jobmanager port",
			CFGKEY_JM_ADDRESS = "Jobmanager address";

	static final String DEFAULT_JM_ADDRESS = "localhost";

	/** initial default count value. */
	static final int DEFAULT_JM_PORT = 6123;

	// the logger instance
	private static final NodeLogger logger = NodeLogger
		.getLogger(FlinkJobManagerConnectionNodeModel.class);

	private final SettingsModelString jobManagerAddress = createAddressModel();

	private final SettingsModelIntegerBounded jobManagerPort = createPortModel();

	/**
	 * Constructor for the node model.
	 */
	protected FlinkJobManagerConnectionNodeModel() {
		super(new PortType[0], new PortType[] { FlinkJobmanagerConnectionObject.TYPE });
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#configure(org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		if (this.jobManagerAddress.getStringValue().isEmpty())
			throw new InvalidSettingsException("No address provided");

		FlinkJobmanagerConnectionObjectSpec connection = new FlinkJobmanagerConnectionObjectSpec();
		FlinkJobManagerSettings settings = connection.getSettings();
		settings.setAddress(new InetSocketAddress(this.jobManagerAddress.getStringValue(),
			this.jobManagerPort.getIntValue()));
		return new PortObjectSpec[] { connection };
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#execute(org.knime.core.node.port.PortObject[],
	 * org.knime.core.node.ExecutionContext)
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec) throws Exception {
		FlinkJobmanagerConnectionObject connection = new FlinkJobmanagerConnectionObject();
		FlinkJobManagerSettings settings = connection.getSettings();
		settings.setAddress(new InetSocketAddress(this.jobManagerAddress.getStringValue(),
			this.jobManagerPort.getIntValue()));
		return new PortObject[] { connection };
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
		this.jobManagerAddress.loadSettingsFrom(settings);
		this.jobManagerPort.loadSettingsFrom(settings);
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
		this.jobManagerAddress.saveSettingsTo(settings);
		this.jobManagerPort.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		this.jobManagerAddress.validateSettings(settings);
		this.jobManagerPort.validateSettings(settings);
	}

	static SettingsModelString createAddressModel() {
		return new SettingsModelString(CFGKEY_JM_ADDRESS, DEFAULT_JM_ADDRESS);
	}

	static SettingsModelIntegerBounded createPortModel() {
		return new SettingsModelIntegerBounded(CFGKEY_JM_PORT, DEFAULT_JM_PORT, 0, 65536);
	}

}

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
package de.bund.bfr.knime.hdfs.connection;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import de.bund.bfr.knime.hdfs.HDFSSettings;
import de.bund.bfr.knime.hdfs.port.HDFSConnectionObject;
import de.bund.bfr.knime.hdfs.port.HDFSConnectionObjectSpec;

/**
 * This is the model implementation of HDFSConnection.
 * Connects to a local or remote HDFS server.
 * 
 * @author Arvid Heise
 */
public class HDFSConnectionNodeModel extends NodeModel {
	/**
	 * the settings key which is used to retrieve and store the settings (from
	 * the dialog or from a settings file) (package visibility to be usable from
	 * the dialog).
	 */
	private static final String CFGKEY_JM_PORT = "Namenode port",
			CFGKEY_JM_ADDRESS = "Namenode address",
			CFGKEY_JM_PROTOCOL = "Namenode protocol";

	private static final String DEFAULT_JM_ADDRESS = "localhost", DEFAULT_JM_PROTOCOL = "webhdfs";

	/** initial default count value. */
	private static final int DEFAULT_JM_PORT = 50070;

	private final SettingsModelString namenodeAddress = createAddressModel();

	private final SettingsModelIntegerBounded namenodePort = createPortModel();

	private final SettingsModelString namenodeProtocol = createProtocolModel();

	/**
	 * Constructor for the node model.
	 */
	protected HDFSConnectionNodeModel() {
		super(new PortType[0], new PortType[] { HDFSConnectionObject.TYPE });
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#configure(org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		if (this.namenodeAddress.getStringValue().isEmpty())
			throw new InvalidSettingsException("No address provided");

		HDFSConnectionObjectSpec connection = new HDFSConnectionObjectSpec();
		HDFSSettings settings = connection.getSettings();
		settings.getConfiguration().set("fs.default.name", String.format("%s://%s:%d",
			this.namenodeProtocol.getStringValue(),
			this.namenodeAddress.getStringValue(),
			this.namenodePort.getIntValue()));
		return new PortObjectSpec[] { connection };
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#execute(org.knime.core.node.port.PortObject[],
	 * org.knime.core.node.ExecutionContext)
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec) throws Exception {
		HDFSConnectionObject connection = new HDFSConnectionObject();
		HDFSSettings settings = connection.getSettings();
		settings.getConfiguration().set("fs.default.name", String.format("%s://%s:%d",
			this.namenodeProtocol.getStringValue(),
			this.namenodeAddress.getStringValue(),
			this.namenodePort.getIntValue()));
		// test if settings are correct
		FileSystem.get(settings.getConfiguration()).listFiles(new Path("/"), false);
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
		this.namenodeAddress.loadSettingsFrom(settings);
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
		this.namenodeAddress.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		this.namenodeAddress.validateSettings(settings);
	}

	static SettingsModelString createAddressModel() {
		return new SettingsModelString(CFGKEY_JM_ADDRESS, DEFAULT_JM_ADDRESS);
	}

	static SettingsModelIntegerBounded createPortModel() {
		return new SettingsModelIntegerBounded(CFGKEY_JM_PORT, DEFAULT_JM_PORT, 0, 65536);
	}

	/**
	 * @return
	 */
	public static SettingsModelString createProtocolModel() {
		return new SettingsModelString(CFGKEY_JM_PROTOCOL, DEFAULT_JM_PROTOCOL);
	}
}

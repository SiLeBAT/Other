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
package de.bund.bfr.knime.hdfs.file;

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
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;

import de.bund.bfr.knime.hdfs.port.HDFSConnectionObject;
import de.bund.bfr.knime.hdfs.port.HDFSConnectionObjectSpec;

/**
 * This is the model implementation of HDFSUpload.
 * Transfers a file to HDFS.
 * 
 * @author Arvid Heise
 */
public class HDFSUploadNodeModel extends NodeModel {
	private SettingsModelString source = createSourceModel(), target = createTargetModel(),
			targetVar = createTargetVariableModel(true);

	private SettingsModelBoolean override = createOverrideModel();

	private static NameGenerator nameGenerator = NameGenerator.getInstance("hdfsFile");

	static SettingsModelString createSourceModel() {
		return new SettingsModelString("source", "");
	}

	static SettingsModelString createTargetModel() {
		return new SettingsModelString("target", "");
	}

	static SettingsModelBoolean createOverrideModel() {
		return new SettingsModelBoolean("override", true);
	}

	static SettingsModelString createTargetVariableModel(boolean generate) {
		return new SettingsModelString("targetVar", generate ? nameGenerator.generate() : "");
	}

	/**
	 * Constructor for the node model.
	 */
	protected HDFSUploadNodeModel() {
		super(new PortType[] { HDFSConnectionObject.TYPE }, new PortType[] { FlowVariablePortObject.TYPE });
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#execute(org.knime.core.node.port.PortObject[],
	 * org.knime.core.node.ExecutionContext)
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec) throws Exception {
		HDFSConnectionObject connection = (HDFSConnectionObject) inObjects[0];
		FileSystem hdfs = FileSystem.get(connection.getSettings().getConfiguration());
		hdfs.copyFromLocalFile(new Path(this.source.getStringValue()), new Path(this.target.getStringValue()));

		// HDFSFile file = new HDFSFile();
		// file.setHdfsSettings(connection.getSettings());
		// file.setLocation(hdfs.resolvePath(new Path(this.target.getStringValue())).toUri());
		//
		// HDFSFilesObject hdfsFileObject = new HDFSFilesObject();
		// hdfsFileObject.setFiles(Sets.newHashSet(file));
		return new PortObject[] { FlowVariablePortObject.INSTANCE };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
	}

	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		try {
			if (this.source.getStringValue().isEmpty())
				throw new InvalidSettingsException("Source may not be empty");

			if (this.target.getStringValue().isEmpty())
				throw new InvalidSettingsException("Target may not be empty");

			HDFSConnectionObjectSpec connection = (HDFSConnectionObjectSpec) inSpecs[0];
			FileSystem hdfs = FileSystem.get(connection.getSettings().getConfiguration());
			final Path targetPath = new Path(this.target.getStringValue());
			if (!this.override.getBooleanValue() && hdfs.exists(targetPath))
				throw new InvalidSettingsException("File already exists");

			// HDFSFile file = new HDFSFile();
			// file.setHdfsSettings(connection.getSettings());
			// test if file can be created
			try {
				hdfs.create(targetPath).close();
				// file.setLocation(hdfs.resolvePath(targetPath).toUri());
				pushFlowVariableString(this.targetVar.getStringValue(), hdfs.resolvePath(targetPath).toUri().toString());
			} finally {
				hdfs.delete(targetPath, true);
			}
			//
			// HDFSFilesObjectSpec hdfsFileObject = new HDFSFilesObjectSpec();
			// hdfsFileObject.setFiles(Sets.newHashSet(file));
			return new PortObjectSpec[] { FlowVariablePortObjectSpec.INSTANCE };
		} catch (IOException e) {
			throw new InvalidSettingsException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		this.source.saveSettingsTo(settings);
		this.target.saveSettingsTo(settings);
		this.targetVar.saveSettingsTo(settings);
		this.override.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		this.source.loadSettingsFrom(settings);
		this.target.loadSettingsFrom(settings);
		this.targetVar.loadSettingsFrom(settings);
		this.override.loadSettingsFrom(settings);
		nameGenerator.addExistingName(this.targetVar.getStringValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		this.source.validateSettings(settings);
		this.target.validateSettings(settings);
		this.targetVar.validateSettings(settings);
		this.override.validateSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

}

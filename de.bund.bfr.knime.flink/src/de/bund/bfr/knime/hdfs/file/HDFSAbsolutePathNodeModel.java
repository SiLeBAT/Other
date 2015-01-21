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
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;

import de.bund.bfr.knime.hdfs.port.HDFSConnectionObject;
import de.bund.bfr.knime.hdfs.port.HDFSConnectionObjectSpec;

/**
 * This is the model implementation of HDFSAbsolutePath.
 * Transforms a relative HDFS path to an absolute path with an HDFS connection.
 * 
 * @author Arvid Heise
 */
public class HDFSAbsolutePathNodeModel extends NodeModel {

	private SettingsModelString relativePath = createRelativePathModel(),
			variable = createVariableModel(true);

	private static NameGenerator nameGenerator = NameGenerator.getInstance("hdfsFile");

	static SettingsModelString createRelativePathModel() {
		return new SettingsModelString("relativePath", "");
	}

	static SettingsModelString createVariableModel(boolean generate) {
		return new SettingsModelString("variable", generate ? nameGenerator.generate() : "");
	}

	/**
	 * Constructor for the node model.
	 */
	protected HDFSAbsolutePathNodeModel() {
		super(new PortType[] { HDFSConnectionObject.TYPE }, new PortType[] { FlowVariablePortObject.TYPE });
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#configure(org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs) throws InvalidSettingsException {

		try {
			if (this.relativePath.getStringValue().isEmpty())
				throw new InvalidSettingsException("Relative path may not be empty");

			if (this.variable.getStringValue().isEmpty())
				throw new InvalidSettingsException("Variable may not be empty");

			HDFSConnectionObjectSpec connection = (HDFSConnectionObjectSpec) inSpecs[0];
			FileSystem hdfs = FileSystem.get(connection.getSettings().getConfiguration());

			Path path = new Path(this.relativePath.getStringValue());
			boolean created = true;
			try {
				hdfs.create(path, false).close();
			} catch (IOException e) {
				created = false;
			}
			Path resolvedPath = hdfs.resolvePath(path);
			if(created)
				hdfs.delete(path, false);
			pushFlowVariableString(this.variable.getStringValue(), resolvedPath.toUri().toString());
		} catch (IOException e) {
			throw new InvalidSettingsException(e);
		}
		return new PortObjectSpec[] { FlowVariablePortObjectSpec.INSTANCE };
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#execute(org.knime.core.node.port.PortObject[],
	 * org.knime.core.node.ExecutionContext)
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec) throws Exception {
		return new PortObject[] { FlowVariablePortObject.INSTANCE };
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#loadValidatedSettingsFrom(org.knime.core.node.NodeSettingsRO)
	 */
	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		this.relativePath.loadSettingsFrom(settings);
		this.variable.loadSettingsFrom(settings);
		nameGenerator.addExistingName(this.variable.getStringValue());
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#validateSettings(org.knime.core.node.NodeSettingsRO)
	 */
	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		this.relativePath.validateSettings(settings);
		this.variable.validateSettings(settings);
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#saveSettingsTo(org.knime.core.node.NodeSettingsWO)
	 */
	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		this.relativePath.saveSettingsTo(settings);
		this.variable.saveSettingsTo(settings);
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#loadInternals(java.io.File, org.knime.core.node.ExecutionMonitor)
	 */
	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#saveInternals(java.io.File, org.knime.core.node.ExecutionMonitor)
	 */
	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#reset()
	 */
	@Override
	protected void reset() {
	}

}

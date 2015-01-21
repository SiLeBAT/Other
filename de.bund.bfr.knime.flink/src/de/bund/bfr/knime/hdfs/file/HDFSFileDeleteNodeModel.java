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

/**
 * This is the model implementation of HDFSFileDelete.
 * Deletes a file or directory in HDFS.
 * 
 * @author Arvid Heise
 */
public class HDFSFileDeleteNodeModel extends NodeModel {
	private SettingsModelString path = createPathModel();

	static SettingsModelString createPathModel() {
		return new SettingsModelString("path", "");
	}

	/**
	 * Constructor for the node model.
	 */
	protected HDFSFileDeleteNodeModel() {
		super(new PortType[] { HDFSConnectionObject.TYPE }, new PortType[] { FlowVariablePortObject.TYPE });
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#configure(org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		if (this.path.getStringValue().isEmpty())
			throw new InvalidSettingsException("Path may not be empty");

		return new PortObjectSpec[] { FlowVariablePortObjectSpec.INSTANCE };
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

		Path path = new Path(this.path.getStringValue());
		if (hdfs.exists(path))
			hdfs.delete(path, true);

		return new PortObject[] { FlowVariablePortObject.INSTANCE };
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#loadValidatedSettingsFrom(org.knime.core.node.NodeSettingsRO)
	 */
	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		this.path.loadSettingsFrom(settings);
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#validateSettings(org.knime.core.node.NodeSettingsRO)
	 */
	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		this.path.validateSettings(settings);
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#saveSettingsTo(org.knime.core.node.NodeSettingsWO)
	 */
	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		this.path.saveSettingsTo(settings);
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

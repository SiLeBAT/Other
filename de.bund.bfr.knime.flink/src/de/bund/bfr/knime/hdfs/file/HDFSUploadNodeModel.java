package de.bund.bfr.knime.hdfs.file;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.knime.core.data.DataTableSpec;
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

import com.google.common.collect.Sets;

import de.bund.bfr.knime.hdfs.HDFSFile;
import de.bund.bfr.knime.hdfs.port.HDFSConnectionObject;
import de.bund.bfr.knime.hdfs.port.HDFSConnectionObjectSpec;
import de.bund.bfr.knime.hdfs.port.HDFSFilesObject;
import de.bund.bfr.knime.hdfs.port.HDFSFilesObjectSpec;

/**
 * This is the model implementation of HDFSUpload.
 * Transfers a file to HDFS.
 * 
 * @author Arvid Heise
 */
public class HDFSUploadNodeModel extends NodeModel {
	private SettingsModelString source = createSourceModel(), target = createTargetModel();

	private SettingsModelBoolean override = createOverrideModel();

	static SettingsModelString createSourceModel() {
		return new SettingsModelString("source", "");
	}

	static SettingsModelString createTargetModel() {
		return new SettingsModelString("target", "");
	}

	static SettingsModelBoolean createOverrideModel() {
		return new SettingsModelBoolean("override", true);
	}

	/**
	 * Constructor for the node model.
	 */
	protected HDFSUploadNodeModel() {
		super(new PortType[] { HDFSConnectionObject.TYPE }, new PortType[] { HDFSFilesObject.TYPE });
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
		
		HDFSFile file = new HDFSFile();
		file.setHdfsSettings(connection.getSettings());
		file.setLocation(new URL(this.target.getStringValue()));

		HDFSFilesObject hdfsFileObject = new HDFSFilesObject();
		hdfsFileObject.setFiles(Sets.newHashSet(file));
		return new PortObject[] { hdfsFileObject };
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
			HDFSConnectionObjectSpec connection = (HDFSConnectionObjectSpec) inSpecs[0];
			FileSystem hdfs = FileSystem.get(connection.getSettings().getConfiguration());
			hdfs.copyFromLocalFile(new Path(this.source.getStringValue()), new Path(this.target.getStringValue()));
			
			HDFSFile file = new HDFSFile();
			file.setHdfsSettings(connection.getSettings());
			file.setLocation(new URL(this.target.getStringValue()));

			HDFSFilesObjectSpec hdfsFileObject = new HDFSFilesObjectSpec();
			hdfsFileObject.setFiles(Sets.newHashSet(file));
			return new PortObjectSpec[] { hdfsFileObject };
		}  catch (IOException e) {
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
		this.override.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		this.source.loadSettingsFrom(settings);
		this.target.loadSettingsFrom(settings);
		this.override.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		this.source.validateSettings(settings);
		this.target.validateSettings(settings);
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

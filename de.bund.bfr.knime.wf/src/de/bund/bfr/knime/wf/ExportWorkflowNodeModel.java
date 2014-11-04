package de.bund.bfr.knime.wf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.WorkflowManager;

/**
 * This is the model implementation of ExportWorkflow.
 * 
 *
 * @author BfR
 */
public class ExportWorkflowNodeModel extends NodeModel {
    
	static final String ZIP_FILE = "zipfile";
	
    private final SettingsModelString zipFile = new SettingsModelString(ZIP_FILE, "");

    /**
     * Constructor for the node model.
     */
    protected ExportWorkflowNodeModel() {
        super(1, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	saveWF(exec);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        return null;
    }

	private Integer saveWF(final ExecutionContext exec) throws Exception {
		Integer result = null;
		for (NodeContainer nc : WorkflowManager.ROOT.getNodeContainers()) {
			if (nc instanceof WorkflowManager) {
				WorkflowManager wfm = (WorkflowManager) nc;
				for (ExportWorkflowNodeModel m : wfm.findNodes(ExportWorkflowNodeModel.class, true).values()) {
					if (m == this) {
						File wfdir = wfm.getWorkingDir().getFile();
						wfm.save(wfdir, exec, true);
						zipDirectory(wfdir, zipFile.getStringValue());
					}
				}
			}
		}
		return result;
	}
	private void zipDirectory(File dir, String zipDirName) {
		try {
			List<String> filesListInDir = populateFilesList(null, dir);
			//now zip files one by one
			//create ZipOutputStream to write to the zip file
			FileOutputStream fos = new FileOutputStream(zipDirName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (String filePath : filesListInDir) {
				//for ZipEntry we need to keep only relative file path, so we used substring on absolute path
				ZipEntry ze = new ZipEntry(filePath.substring(dir.getParentFile().getAbsolutePath().length() + 1, filePath.length()));
				zos.putNextEntry(ze);
				//read the file and write to ZipOutputStream
				FileInputStream fis = new FileInputStream(filePath);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private List<String> populateFilesList(List<String> filesListInDir, File dir) throws IOException {
		if (filesListInDir == null) filesListInDir = new ArrayList<String>();
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				if (!file.getName().equals(".knimeLock")) filesListInDir.add(file.getAbsolutePath());
			} else {
				filesListInDir = populateFilesList(filesListInDir, file);
			}
		}
		return filesListInDir;
	}

	/**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	zipFile.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	zipFile.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	zipFile.validateSettings(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

}


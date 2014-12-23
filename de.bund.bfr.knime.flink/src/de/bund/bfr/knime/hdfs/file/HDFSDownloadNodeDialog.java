package de.bund.bfr.knime.hdfs.file;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentString;

/**
 * <code>NodeDialog</code> for the "HDFSDownload" Node.
 * Downloads an HDFS file from a remote HDFS namenode to the local filesystem.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Arvid Heise
 */
public class HDFSDownloadNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the HDFSUpload node.
	 */
	protected HDFSDownloadNodeDialog() {
		this.addDialogComponent(new DialogComponentString(HDFSDownloadNodeModel.createSourceModel(), "Remote path"));
		this.addDialogComponent(new DialogComponentFileChooser(HDFSDownloadNodeModel.createTargetModel(),
			HDFSUploadNodeDialog.class.getName(), JFileChooser.SAVE_DIALOG, false));
		this.addDialogComponent(new DialogComponentBoolean(HDFSDownloadNodeModel.createOverrideModel(), "Override: "));
	}
}


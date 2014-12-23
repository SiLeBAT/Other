package de.bund.bfr.knime.hdfs.file;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentString;

/**
 * <code>NodeDialog</code> for the "HDFSUpload" Node.
 * Transfers a file to HDFS.
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Arvid Heise
 */
public class HDFSUploadNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the HDFSUpload node.
	 */
	protected HDFSUploadNodeDialog() {
		this.addDialogComponent(new DialogComponentFileChooser(HDFSUploadNodeModel.createSourceModel(),
			HDFSUploadNodeDialog.class.getName(), JFileChooser.OPEN_DIALOG, false));
		this.addDialogComponent(new DialogComponentString(HDFSDownloadNodeModel.createTargetModel(), "Remote path"));
		this.addDialogComponent(new DialogComponentBoolean(HDFSUploadNodeModel.createOverrideModel(), "Override: "));
	}
}

package de.bund.bfr.knime.wf;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "ExportWorkflow" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author BfR
 */
public class ExportWorkflowNodeDialog extends DefaultNodeSettingsPane {

	private final DialogComponentFileChooser zipfileDialog;
    private final SettingsModelString zipFile = new SettingsModelString(ExportWorkflowNodeModel.ZIP_FILE, "");

    /**
     * New pane for configuring the ExportWorkflow node.
     */
    protected ExportWorkflowNodeDialog() {
    	zipfileDialog = new DialogComponentFileChooser(zipFile, ExportWorkflowNodeModel.ZIP_FILE, JFileChooser.OPEN_DIALOG, ".zip");
    	
    	zipfileDialog.setBorderTitle("Zip File");
    	
    	addDialogComponent(zipfileDialog);
    }
}


package de.bund.bfr.knime.aaw.hartung;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;


/**
 * <code>NodeDialog</code> for the "MyImporter" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Armin Weiser
 */
public class MyImporterNodeDialog extends DefaultNodeSettingsPane {

	private final DialogComponentFileChooser xlsDialog;
    private final SettingsModelString xlsFile = new SettingsModelString(MyImporterNodeModel.XLS_FILE, "");

    /**
     * New pane for configuring the MyImporter node.
     */
    protected MyImporterNodeDialog() {
    	xlsDialog = new DialogComponentFileChooser(xlsFile, MyImporterNodeModel.XLS_FILE, JFileChooser.OPEN_DIALOG, ".xls", ".XLS");
    	
    	xlsDialog.setBorderTitle("XLS File");
    	
    	addDialogComponent(xlsDialog);
    }
}


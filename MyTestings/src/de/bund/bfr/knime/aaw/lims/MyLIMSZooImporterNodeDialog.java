package de.bund.bfr.knime.aaw.lims;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import javax.swing.JFileChooser;
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
public class MyLIMSZooImporterNodeDialog extends DefaultNodeSettingsPane {

	private final DialogComponentFileChooser xlsDialog;
    private final SettingsModelString xlsFile = new SettingsModelString(MyLIMSZooImporterNodeModel.XLS_FILE, "");

    /**
     * New pane for configuring the MyImporter node.
     */
    protected MyLIMSZooImporterNodeDialog() {
    	xlsDialog = new DialogComponentFileChooser(xlsFile, MyLIMSZooImporterNodeModel.XLS_FILE, JFileChooser.OPEN_DIALOG, ".xls", ".XLS");
    	
    	xlsDialog.setBorderTitle("XLS File");
    	
    	addDialogComponent(xlsDialog);
    }
}


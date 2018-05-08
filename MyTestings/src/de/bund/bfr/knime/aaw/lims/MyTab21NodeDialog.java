package de.bund.bfr.knime.aaw.lims;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentNumberEdit;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "MyTab21" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author aaw
 */
public class MyTab21NodeDialog extends DefaultNodeSettingsPane {

	private final DialogComponentFileChooser folderDialog;
	private final DialogComponentString erregerDialog, bfrProgrammDialog, serovarDialog, blSubFolderDialog, pruefPlanIdDialog;
	private final DialogComponentNumberEdit jahrDialog, kriterienJahrDialog, esblDialog, enteroDialog;
    private final SettingsModelString baseFolder = new SettingsModelString(MyTab21NodeModel.BASE_FOLDER, "C:/Dokumente und Einstellungen/Weiser/Desktop/tawak/");
    private final SettingsModelString blSubFolder = new SettingsModelString(MyTab21NodeModel.BLSUBFOLDER, "");
    private final SettingsModelString erreger = new SettingsModelString(MyTab21NodeModel.ERREGER, "SA");
    private final SettingsModelString bfrProgramm = new SettingsModelString(MyTab21NodeModel.BFR_PROGRAMM, "Dia");
    private final SettingsModelString serovar = new SettingsModelString(MyTab21NodeModel.SEROVAR, "");
    private final SettingsModelInteger jahr = new SettingsModelInteger(MyTab21NodeModel.JAHR, 2013);
    private final SettingsModelInteger kriterienJahr = new SettingsModelInteger(MyTab21NodeModel.KRITERIEN_JAHR, 2013);
    private final SettingsModelString pruefPlanId = new SettingsModelString(MyTab21NodeModel.PPID, "");
    private final SettingsModelInteger esbl = new SettingsModelInteger(MyTab21NodeModel.ESBL, 0);
    private final SettingsModelInteger entero = new SettingsModelInteger(MyTab21NodeModel.ENTERO, 0);

    /**
     * New pane for configuring the MyTab21 node.
     */
    protected MyTab21NodeDialog() {
    	folderDialog = new DialogComponentFileChooser(baseFolder, MyTab21NodeModel.BASE_FOLDER, JFileChooser.OPEN_DIALOG, true);
    	
    	erregerDialog = new DialogComponentString(erreger, MyTab21NodeModel.ERREGER);
    	bfrProgrammDialog = new DialogComponentString(bfrProgramm, MyTab21NodeModel.BFR_PROGRAMM);
    	serovarDialog = new DialogComponentString(serovar, MyTab21NodeModel.SEROVAR);
    	blSubFolderDialog = new DialogComponentString(blSubFolder, MyTab21NodeModel.BLSUBFOLDER);
    	pruefPlanIdDialog = new DialogComponentString(pruefPlanId, MyTab21NodeModel.PPID);
    	
    	jahrDialog = new DialogComponentNumberEdit(jahr, MyTab21NodeModel.JAHR);
    	kriterienJahrDialog = new DialogComponentNumberEdit(kriterienJahr, MyTab21NodeModel.KRITERIEN_JAHR);

    	esblDialog = new DialogComponentNumberEdit(esbl, MyTab21NodeModel.ESBL);

    	enteroDialog = new DialogComponentNumberEdit(entero, MyTab21NodeModel.ENTERO);

    	folderDialog.setBorderTitle("Base Folder");   	
    	addDialogComponent(folderDialog);
    	
    	addDialogComponent(erregerDialog);
    	addDialogComponent(bfrProgrammDialog);
    	addDialogComponent(serovarDialog);
    	addDialogComponent(blSubFolderDialog);
    	addDialogComponent(jahrDialog);
    	addDialogComponent(kriterienJahrDialog);
    	addDialogComponent(pruefPlanIdDialog);
    	addDialogComponent(esblDialog);
    	addDialogComponent(enteroDialog);
    }
}


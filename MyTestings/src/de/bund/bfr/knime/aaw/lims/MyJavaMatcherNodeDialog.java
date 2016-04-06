package de.bund.bfr.knime.aaw.lims;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "MyJavaJoiner" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author BfR
 */
public class MyJavaMatcherNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the MyJavaJoiner node.
     */
	protected MyJavaMatcherNodeDialog() {
    	createNewGroup("BVL:"); 
    	SettingsModelString sms = new SettingsModelString(MyJavaMatcherNodeModel.COLSEL, "");
    	SettingsModelString bvlSample = new SettingsModelString(MyJavaMatcherNodeModel.BVL_SAMPLE, "");
    	//DialogComponentColumnNameSelection d = new DialogComponentColumnNameSelection(sms, "Select a columnname", 0, true);
    	//addDialogComponent(d);
    	addDialogComponent(new DialogComponentString(sms, "Enter a columnname -> LabNumber:"));
    	addDialogComponent(new DialogComponentString(bvlSample, "Columnname for Sample result:"));

    	createNewGroup("LIMS:"); 
    	SettingsModelString smsLims = new SettingsModelString(MyJavaMatcherNodeModel.COLSELLIMS, "");
    	SettingsModelString limsSample = new SettingsModelString(MyJavaMatcherNodeModel.LIMS_SAMPLE, "");
    	//d = new DialogComponentColumnNameSelection(smsLims, "Select a columnname", 1, true);
    	//addDialogComponent(d);
    	addDialogComponent(new DialogComponentString(smsLims, "Enter a columnname -> Kundennummer:"));
    	addDialogComponent(new DialogComponentString(limsSample, "Columnname for Sample result:"));

    	createNewGroup("Remove Non-Numbers:"); 
    	SettingsModelBoolean smsNO = new SettingsModelBoolean(MyJavaMatcherNodeModel.NUMBERSONLY, false);
    	addDialogComponent(new DialogComponentBoolean(smsNO, "Remove all non-numbers from LIMS Strings:"));

    	createNewGroup("Wild Search:"); 
    	SettingsModelBoolean smsWild = new SettingsModelBoolean(MyJavaMatcherNodeModel.WILD, false);
    	addDialogComponent(new DialogComponentBoolean(smsWild, "Enable wild cards at the ends of Strings:"));
	}
}


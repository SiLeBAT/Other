package de.bund.bfr.knime.aaw.lims;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
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
    	SettingsModelString sms = new SettingsModelString(MyJavaMatcherNodeModel.BVL_PROBENNR, "");
    	SettingsModelString bvlSample = new SettingsModelString(MyJavaMatcherNodeModel.BVL_SAMPLE, "");
    	SettingsModelString bvlMatrixCode = new SettingsModelString(MyJavaMatcherNodeModel.BVL_MATRIX_CODE, "");
    	SettingsModelString bvlSamplingDate = new SettingsModelString(MyJavaMatcherNodeModel.BVL_SAMPLING_DATE, "");
     	addDialogComponent(new DialogComponentString(sms, "Enter a columnname -> LabNumber:"));
    	addDialogComponent(new DialogComponentString(bvlSample, "Columnname for Sample Vorbefund:"));
    	addDialogComponent(new DialogComponentString(bvlMatrixCode, "Columnname for ADV-Matrix-Code:"));
    	addDialogComponent(new DialogComponentString(bvlSamplingDate, "Columnname for sampling date:"));

    	createNewGroup("LIMS:"); 
    	SettingsModelString smsLims = new SettingsModelString(MyJavaMatcherNodeModel.LIMS_KUNDENPROBENNR, "");
    	SettingsModelString limsSample = new SettingsModelString(MyJavaMatcherNodeModel.LIMS_SAMPLE, "");
    	SettingsModelString limsMatrixCode = new SettingsModelString(MyJavaMatcherNodeModel.LIMS_MATRIX_CODE, "");
    	SettingsModelString limsSamplingDate = new SettingsModelString(MyJavaMatcherNodeModel.LIMS_SAMPLING_DATE, "");
    	SettingsModelString limsSampleResult = new SettingsModelString(MyJavaMatcherNodeModel.LIMS_SAMPLE_RESULT, "");
    	SettingsModelString limsSampleStatus = new SettingsModelString(MyJavaMatcherNodeModel.LIMS_SAMPLE_STATUS, "");
    	addDialogComponent(new DialogComponentString(smsLims, "Enter a columnname -> Kundennummer:"));
    	addDialogComponent(new DialogComponentString(limsSample, "Columnname for Sample Vorbefund:"));
    	addDialogComponent(new DialogComponentString(limsMatrixCode, "Columnname for ADV-Matrix-Code:"));
    	addDialogComponent(new DialogComponentString(limsSamplingDate, "Columnname for sampling date:"));
    	addDialogComponent(new DialogComponentString(limsSampleResult, "Columnname for Sample result:"));
    	addDialogComponent(new DialogComponentString(limsSampleStatus, "Columnname for result status:"));
/*
    	createNewGroup("Remove Non-Numbers:"); 
    	SettingsModelBoolean smsNO = new SettingsModelBoolean(MyJavaMatcherNodeModel.NUMBERSONLY, false);
    	addDialogComponent(new DialogComponentBoolean(smsNO, "Remove all non-numbers from LIMS Strings:"));

    	createNewGroup("Wild Search:"); 
    	SettingsModelBoolean smsWild = new SettingsModelBoolean(MyJavaMatcherNodeModel.WILD, false);
    	addDialogComponent(new DialogComponentBoolean(smsWild, "Enable wild cards at the ends of Strings:"));

    	createNewGroup("Loose Search:"); 
    	SettingsModelBoolean smsLoose = new SettingsModelBoolean(MyJavaMatcherNodeModel.LOOSE, false);
    	addDialogComponent(new DialogComponentBoolean(smsLoose, "Enable loose search, i.e. NULL allowed as Match:"));

    	createNewGroup("Optional Output:"); 
    	SettingsModelBoolean smsOptional = new SettingsModelBoolean(MyJavaMatcherNodeModel.OPTIONAL, false);
    	addDialogComponent(new DialogComponentBoolean(smsOptional, "Enable optional Output:"));

    	createNewGroup("Degree of Trust:"); 
    	SettingsModelInteger smiTrust = new SettingsModelInteger(MyJavaMatcherNodeModel.DEGREE_OF_TRUST, 0);
    	addDialogComponent(new DialogComponentNumber(smiTrust, "Match Quality:", 1));
    	*/
	}
}


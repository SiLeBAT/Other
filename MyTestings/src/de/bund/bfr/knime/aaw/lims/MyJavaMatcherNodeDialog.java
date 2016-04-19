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
    	SettingsModelString tpn = new SettingsModelString(MyJavaMatcherNodeModel.BVL_TEILPROBENNR, "");
    	SettingsModelString bvlSample = new SettingsModelString(MyJavaMatcherNodeModel.BVL_SAMPLE, "");
    	SettingsModelString bvlMatrixCode = new SettingsModelString(MyJavaMatcherNodeModel.BVL_MATRIX_CODE, "");
    	SettingsModelString bvlSamplingDate = new SettingsModelString(MyJavaMatcherNodeModel.BVL_SAMPLING_DATE, "");
     	addDialogComponent(new DialogComponentString(sms, "Enter a columnname -> Lab-ProbenNummer:"));
     	addDialogComponent(new DialogComponentString(tpn, "Enter a columnname for Teilprobe:"));
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
    	SettingsModelString limsProjectName = new SettingsModelString(MyJavaMatcherNodeModel.LIMS_PROJECT_NAME, "");
    	addDialogComponent(new DialogComponentString(smsLims, "Enter a columnname -> Kundennummer:"));
    	addDialogComponent(new DialogComponentString(limsSample, "Columnname for Sample Vorbefund:"));
    	addDialogComponent(new DialogComponentString(limsMatrixCode, "Columnname for ADV-Matrix-Code:"));
    	addDialogComponent(new DialogComponentString(limsSamplingDate, "Columnname for sampling date:"));
    	addDialogComponent(new DialogComponentString(limsSampleResult, "Columnname for Sample result:"));
    	addDialogComponent(new DialogComponentString(limsSampleStatus, "Columnname for result status:"));
    	addDialogComponent(new DialogComponentString(limsProjectName, "Columnname for project name:"));
	}
}


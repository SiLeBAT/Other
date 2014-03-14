package de.bund.bfr.knime.fbd;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "FoodSensitivitySpecificity" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author
 */
public class FoodSensitivitySpecificityNodeDialog extends
		DefaultNodeSettingsPane {

	private static final String FOOD_HISTORY = "Food History";
	private static final String OUT_HISTORY = "Out History";

	/**
	 * New pane for configuring the FoodSensitivitySpecificity node.
	 */
	protected FoodSensitivitySpecificityNodeDialog() {
		DialogComponentFileChooser foodComp = new DialogComponentFileChooser(
				new SettingsModelString(
						FoodSensitivitySpecificityNodeModel.CFGKEY_FOOD_DISTRIBUTION_FILE,
						null), FOOD_HISTORY, ".csv");
		DialogComponentFileChooser outComp = new DialogComponentFileChooser(
				new SettingsModelString(
						FoodSensitivitySpecificityNodeModel.CFGKEY_OUTPUT_PATH,
						null), OUT_HISTORY, JFileChooser.SAVE_DIALOG, true);
		DialogComponentStringSelection methodComp = new DialogComponentStringSelection(
				new SettingsModelString(
						FoodSensitivitySpecificityNodeModel.CFGKEY_METHOD,
						FoodSensitivitySpecificityNodeModel.LIKELYHOOD_METHOD),
				"Method", FoodSensitivitySpecificityNodeModel.METHODS);

		foodComp.setBorderTitle("Food Distribution File");
		outComp.setBorderTitle("Output Path");

		addDialogComponent(foodComp);
		addDialogComponent(outComp);
		addDialogComponent(methodComp);
	}
}

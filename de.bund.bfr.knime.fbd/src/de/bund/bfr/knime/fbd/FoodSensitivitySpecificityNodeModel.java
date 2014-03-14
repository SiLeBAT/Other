package de.bund.bfr.knime.fbd;

import java.io.File;
import java.io.IOException;

import org.eclipse.stem.fbd.GenerateFoodSensitivitySpecificity;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is the model implementation of FoodSensitivitySpecificity.
 * 
 * 
 * @author
 */
public class FoodSensitivitySpecificityNodeModel extends NodeModel {

	static final String LIKELYHOOD_METHOD = "Likelyhood Method";
	static final String SPEARMAN_METHOD = "Spearman Method";
	static final String[] METHODS = { LIKELYHOOD_METHOD, SPEARMAN_METHOD };

	static final String CFGKEY_FOOD_DISTRIBUTION_FILE = "Food Distribution File";
	static final String CFGKEY_OUTPUT_PATH = "Output Path";
	static final String CFGKEY_METHOD = "Method";

	private final SettingsModelString foodFile = new SettingsModelString(
			CFGKEY_FOOD_DISTRIBUTION_FILE, null);
	private final SettingsModelString outPath = new SettingsModelString(
			CFGKEY_OUTPUT_PATH, null);
	private final SettingsModelString method = new SettingsModelString(
			CFGKEY_METHOD, LIKELYHOOD_METHOD);

	/**
	 * Constructor for the node model.
	 */
	protected FoodSensitivitySpecificityNodeModel() {
		super(0, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
			final ExecutionContext exec) throws Exception {
		GenerateFoodSensitivitySpecificity.exec(foodFile.getStringValue(),
				outPath.getStringValue(),
				method.getStringValue().equals(SPEARMAN_METHOD), exec);
		return new BufferedDataTable[] {};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException {
		return new DataTableSpec[] {};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		foodFile.saveSettingsTo(settings);
		outPath.saveSettingsTo(settings);
		method.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		foodFile.loadSettingsFrom(settings);
		outPath.loadSettingsFrom(settings);
		method.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		foodFile.validateSettings(settings);
		outPath.validateSettings(settings);
		method.validateSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

}

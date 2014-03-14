package de.bund.bfr.knime.pmm.io.formulareader;

import java.io.File;
import java.io.IOException;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.core.port.PmmPortObjectSpec;

/**
 * This is the model implementation of ModelReader.
 * 
 * 
 * @author Christian Thoens
 */
public class FormulaReaderNodeModel extends NodeModel {

	private FormulaReaderSettings set;

	/**
	 * Constructor for the node model.
	 */
	protected FormulaReaderNodeModel() {
		super(new PortType[] {}, new PortType[] { PmmPortObject.TYPE });
		set = new FormulaReaderSettings();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		if (set.getModelType().equals(FormulaReaderSettings.PRIMARY_TYPE)) {
			return new PortObject[] { new PmmPortObject(set.getPrimaryModels(),
					PmmPortObjectSpec.PRIMARY_MODEL_FORMULA_TYPE) };
		} else if (set.getModelType().equals(
				FormulaReaderSettings.SECONDARY_TYPE)) {
			return new PortObject[] { new PmmPortObject(
					set.getSecondaryModels(),
					PmmPortObjectSpec.SECONDARY_MODEL_FORMULA_TYPE) };
		}

		return null;
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
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		if (set.getModelType().equals(FormulaReaderSettings.PRIMARY_TYPE)) {
			return new PortObjectSpec[] { new PmmPortObjectSpec(
					PmmPortObjectSpec.PRIMARY_MODEL_FORMULA_TYPE) };
		} else if (set.getModelType().equals(
				FormulaReaderSettings.SECONDARY_TYPE)) {
			return new PortObjectSpec[] { new PmmPortObjectSpec(
					PmmPortObjectSpec.SECONDARY_MODEL_FORMULA_TYPE) };
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		set.save(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		set.load(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
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

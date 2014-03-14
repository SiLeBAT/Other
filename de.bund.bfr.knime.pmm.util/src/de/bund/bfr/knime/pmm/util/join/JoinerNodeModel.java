package de.bund.bfr.knime.pmm.util.join;

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
 * This is the model implementation of PmmJoiner.
 * 
 * 
 * @author Christian Thoens
 */
public class JoinerNodeModel extends NodeModel {

	private JoinerSettings set;

	/**
	 * Constructor for the node model.
	 */
	protected JoinerNodeModel() {
		super(new PortType[] { PmmPortObject.TYPE, PmmPortObject.TYPE },
				new PortType[] { PmmPortObject.TYPE });
		set = new JoinerSettings();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		PmmPortObject in1 = (PmmPortObject) inObjects[0];
		PmmPortObject in2 = (PmmPortObject) inObjects[1];
		Joiner joiner = null;

		if (JoinerSettings.PRIMARY_JOIN.equals(set.getJoinType())) {
			joiner = new PrimaryJoiner(in1, in2);
		} else if (JoinerSettings.SECONDARY_JOIN.equals(set.getJoinType())) {
			joiner = new SecondaryJoiner(in1, in2);
		}

		return new PortObject[] { joiner.getOutput(set.getAssignments()) };
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
		if (set.getJoinType() == null) {
			throw new InvalidSettingsException("Node has to be configured");
		}

		if (JoinerSettings.PRIMARY_JOIN.equals(set.getJoinType())) {
			return new PortObjectSpec[] { new PmmPortObjectSpec(
					PmmPortObjectSpec.PRIMARY_MODEL_TYPE) };
		} else if (JoinerSettings.SECONDARY_JOIN.equals(set.getJoinType())) {
			return new PortObjectSpec[] { new PmmPortObjectSpec(
					PmmPortObjectSpec.SECONDARY_MODEL_TYPE) };
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		set.saveSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		set.loadSettings(settings);
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

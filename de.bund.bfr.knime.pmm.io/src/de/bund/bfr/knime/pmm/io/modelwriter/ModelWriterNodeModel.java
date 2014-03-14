package de.bund.bfr.knime.pmm.io.modelwriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
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

import de.bund.bfr.knime.pmm.core.models.Model;
import de.bund.bfr.knime.pmm.core.models.PrimaryModel;
import de.bund.bfr.knime.pmm.core.models.SecondaryModel;
import de.bund.bfr.knime.pmm.core.models.TertiaryModel;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.core.port.PmmPortObjectSpec;
import de.bund.bfr.knime.pmm.io.DB;

/**
 * This is the model implementation of ModelWriter.
 * 
 * 
 * @author Christian Thoens
 */
public class ModelWriterNodeModel extends NodeModel {

	private ModelWriterSettings set;

	/**
	 * Constructor for the node model.
	 */
	protected ModelWriterNodeModel() {
		super(new PortType[] { PmmPortObject.TYPE }, new PortType[] {});
		set = new ModelWriterSettings();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		PmmPortObject in = (PmmPortObject) inObjects[0];
		PmmPortObjectSpec inSpec = (PmmPortObjectSpec) in.getSpec();
		List<? extends Model> models = null;

		if (inSpec.getType().equals(PmmPortObjectSpec.PRIMARY_MODEL_TYPE)) {
			models = in.getData(new ArrayList<PrimaryModel>());
		} else if (inSpec.getType().equals(
				PmmPortObjectSpec.SECONDARY_MODEL_TYPE)) {
			models = in.getData(new ArrayList<SecondaryModel>());
		} else if (inSpec.getType().equals(
				PmmPortObjectSpec.TERTIARY_MODEL_TYPE)) {
			models = in.getData(new ArrayList<TertiaryModel>());
		}

		Session session = DB.getDataStore().getSessionFactory().openSession();

		session.beginTransaction();

		for (Model model : models) {
			session.save(model);
		}

		session.getTransaction().commit();
		session.close();

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
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		PmmPortObjectSpec in = (PmmPortObjectSpec) inSpecs[0];

		if (!in.getType().equals(PmmPortObjectSpec.PRIMARY_MODEL_TYPE)
				&& !in.getType().equals(PmmPortObjectSpec.SECONDARY_MODEL_TYPE)
				&& !in.getType().equals(PmmPortObjectSpec.TERTIARY_MODEL_TYPE)) {
			throw new InvalidSettingsException("Wrong Input");
		}

		return new DataTableSpec[] {};
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

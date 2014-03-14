package de.bund.bfr.knime.pmm.util.fitting;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

import de.bund.bfr.knime.pmm.core.CombineUtilities;
import de.bund.bfr.knime.pmm.core.models.Model;
import de.bund.bfr.knime.pmm.core.models.ModelFormula;
import de.bund.bfr.knime.pmm.core.models.Parameter;
import de.bund.bfr.knime.pmm.core.models.PrimaryModel;
import de.bund.bfr.knime.pmm.core.models.SecondaryModel;
import de.bund.bfr.knime.pmm.core.models.TertiaryModel;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.core.port.PmmPortObjectSpec;

/**
 * This is the model implementation of ModelFitting.
 * 
 * 
 * @author Christian Thoens
 */
public class ModelFittingNodeModel extends NodeModel {

	private static final int MAX_THREADS = 8;

	private ModelFittingSettings set;

	/**
	 * Constructor for the node model.
	 */
	protected ModelFittingNodeModel() {
		super(new PortType[] { PmmPortObject.TYPE },
				new PortType[] { PmmPortObject.TYPE });
		set = new ModelFittingSettings();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		PmmPortObject in = (PmmPortObject) inObjects[0];
		List<? extends Model> data = null;
		String type = null;

		if (ModelFittingSettings.PRIMARY_FITTING.equals(set.getFittingType())) {
			data = in.getData(new ArrayList<PrimaryModel>());
			type = PmmPortObjectSpec.PRIMARY_MODEL_TYPE;
		} else if (ModelFittingSettings.SECONDARY_FITTING.equals(set
				.getFittingType())) {
			data = in.getData(new ArrayList<SecondaryModel>());
			type = PmmPortObjectSpec.SECONDARY_MODEL_TYPE;
		} else if (ModelFittingSettings.TERTIARY_FITTING.equals(set
				.getFittingType())) {
			data = CombineUtilities.combine(in
					.getData(new ArrayList<SecondaryModel>()));
			type = PmmPortObjectSpec.TERTIARY_MODEL_TYPE;
		}

		doEstimation(data, exec);

		return new PortObject[] { new PmmPortObject(data, type) };
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
		if (set.getFittingType() == null) {
			throw new InvalidSettingsException("Node has to be configured");
		}

		String type = ((PmmPortObjectSpec) inSpecs[0]).getType();

		if (ModelFittingSettings.PRIMARY_FITTING.equals(set.getFittingType())) {
			if (!type.equals(PmmPortObjectSpec.PRIMARY_MODEL_TYPE)) {
				throw new InvalidSettingsException("Wrong Input");
			}

			return new PortObjectSpec[] { new PmmPortObjectSpec(
					PmmPortObjectSpec.PRIMARY_MODEL_TYPE) };
		} else if (ModelFittingSettings.SECONDARY_FITTING.equals(set
				.getFittingType())) {
			if (!type.equals(PmmPortObjectSpec.SECONDARY_MODEL_TYPE)) {
				throw new InvalidSettingsException("Wrong Input");
			}

			return new PortObjectSpec[] { new PmmPortObjectSpec(
					PmmPortObjectSpec.SECONDARY_MODEL_TYPE) };
		} else if (ModelFittingSettings.TERTIARY_FITTING.equals(set
				.getFittingType())) {
			if (!type.equals(PmmPortObjectSpec.SECONDARY_MODEL_TYPE)) {
				throw new InvalidSettingsException("Wrong Input");
			}

			return new PortObjectSpec[] { new PmmPortObjectSpec(
					PmmPortObjectSpec.TERTIARY_MODEL_TYPE) };
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

	private Map<String, Point2D.Double> getLimits(ModelFormula model) {
		Map<String, Point2D.Double> limits = new LinkedHashMap<String, Point2D.Double>();

		for (Parameter param : model.getParams()) {
			double min = Double.NaN;
			double max = Double.NaN;

			if (param.getMin() != null) {
				min = param.getMin();
			}

			if (param.getMax() != null) {
				max = param.getMax();
			}

			limits.put(param.getName(), new Point2D.Double(min, max));
		}

		return limits;
	}

	private void doEstimation(List<? extends Model> dataModels,
			ExecutionContext exec) throws CanceledExecutionException,
			InterruptedException {
		int n = dataModels.size();
		AtomicInteger runningThreads = new AtomicInteger(0);
		AtomicInteger finishedThreads = new AtomicInteger(0);

		for (Model dataModel : dataModels) {
			while (true) {
				exec.checkCanceled();
				exec.setProgress((double) finishedThreads.get() / (double) n,
						"");

				if (runningThreads.get() < MAX_THREADS) {
					break;
				}

				Thread.sleep(100);
			}

			Map<String, Point2D.Double> parameterGuesses;
			int nParameterSpace;
			int nLevenberg;
			boolean stopWhenSuccessful;

			if (set.isExpertSettings()) {
				parameterGuesses = set.getParameterGuesses().get(
						dataModel.getModelFormula().getId());
				nParameterSpace = set.getnParameterSpace();
				nLevenberg = set.getnLevenberg();
				stopWhenSuccessful = set.isStopWhenSuccessful();
			} else {
				parameterGuesses = getLimits(dataModel.getModelFormula());
				nParameterSpace = ModelFittingSettings.DEFAULT_NPARAMETERSPACE;
				nLevenberg = ModelFittingSettings.DEFAULT_NLEVENBERG;
				stopWhenSuccessful = ModelFittingSettings.DEFAULT_STOPWHENSUCCESSFUL;
			}

			Thread thread = null;

			if (dataModel instanceof PrimaryModel) {
				thread = new Thread(new PrimaryEstimationThread(
						(PrimaryModel) dataModel, parameterGuesses,
						set.isEnforceLimits(), nParameterSpace, nLevenberg,
						stopWhenSuccessful, runningThreads, finishedThreads));
			} else if (dataModel instanceof SecondaryModel) {
				thread = new Thread(new SecondaryEstimationThread(
						(SecondaryModel) dataModel, parameterGuesses,
						set.isEnforceLimits(), nParameterSpace, nLevenberg,
						stopWhenSuccessful, runningThreads, finishedThreads));
			} else if (dataModel instanceof TertiaryModel) {
				thread = new Thread(new TertiaryEstimationThread(
						(TertiaryModel) dataModel, parameterGuesses,
						set.isEnforceLimits(), nParameterSpace, nLevenberg,
						stopWhenSuccessful, runningThreads, finishedThreads));
			}

			runningThreads.incrementAndGet();
			thread.start();
		}

		while (true) {
			exec.checkCanceled();
			exec.setProgress((double) finishedThreads.get() / (double) n, "");

			if (runningThreads.get() == 0) {
				break;
			}

			Thread.sleep(100);
		}
	}

}

package de.bund.bfr.knime.pmm.views.predictorview;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.knime.core.node.port.image.ImagePortObject;

import de.bund.bfr.knime.pmm.core.Utilities;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.views.chart.ChartCreator;
import de.bund.bfr.knime.pmm.views.chart.ChartUtilities;
import de.bund.bfr.knime.pmm.views.chart.Plotable;

/**
 * This is the model implementation of PredictorView.
 * 
 * 
 * @author Christian Thoens
 */
public class PredictorViewNodeModel extends NodeModel {

	private PredictorViewSettings set;

	/**
	 * Constructor for the node model.
	 */
	protected PredictorViewNodeModel() {
		super(new PortType[] { PmmPortObject.TYPE },
				new PortType[] { ImagePortObject.TYPE });
		set = new PredictorViewSettings();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		PredictorViewReader reader = new PredictorViewReader(
				(PmmPortObject) inObjects[0]);
		List<String> ids;

		if (set.isSelectAllIDs()) {
			ids = reader.getIds();
		} else {
			ids = set.getSelectedIDs();
		}

		ChartCreator creator = new ChartCreator(reader.getPlotables(),
				reader.getShortLegend(), reader.getLongLegend());

		for (String id : ids) {
			Plotable plotable = reader.getPlotables().get(id);

			if (plotable != null) {
				Map<String, List<Double>> arguments = new LinkedHashMap<String, List<Double>>();

				for (Map.Entry<String, Double> entry : set.getParamXValues()
						.entrySet()) {
					arguments.put(entry.getKey(),
							Arrays.asList(entry.getValue()));
				}

				plotable.setSamples(set.getTimeValues());
				plotable.setFunctionArguments(arguments);
			}
		}

		creator.setParamX(Utilities.TIME);
		creator.setParamY(Utilities.CONCENTRATION);
		set.setToChartCreator(creator);

		ImagePortObject image = ChartUtilities.getImage(creator.getChart(ids),
				set.isExportAsSvg());

		return new PortObject[] { image };
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
		return new PortObjectSpec[] { ChartUtilities.getImageSpec(set
				.isExportAsSvg()) };
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

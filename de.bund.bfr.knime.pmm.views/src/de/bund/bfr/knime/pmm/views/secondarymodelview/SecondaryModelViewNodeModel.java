package de.bund.bfr.knime.pmm.views.secondarymodelview;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.views.chart.ChartCreator;
import de.bund.bfr.knime.pmm.views.chart.ChartUtilities;
import de.bund.bfr.knime.pmm.views.chart.Plotable;

/**
 * This is the model implementation of SecondaryModelView.
 * 
 * 
 * @author Christian Thoens
 */
public class SecondaryModelViewNodeModel extends NodeModel {

	private SecondaryModelViewSettings set;

	/**
	 * Constructor for the node model.
	 */
	protected SecondaryModelViewNodeModel() {
		super(new PortType[] { PmmPortObject.TYPE },
				new PortType[] { ImagePortObject.TYPE });
		set = new SecondaryModelViewSettings();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		SecondaryModelViewReader reader = new SecondaryModelViewReader(
				(PmmPortObject) inObjects[0]);
		ChartCreator creator = new ChartCreator(reader.getPlotables(),
				reader.getShortLegend(), reader.getLongLegend());

		if (set.getSelectedID() != null
				&& reader.getPlotables().get(set.getSelectedID()) != null) {
			Plotable plotable = reader.getPlotables().get(set.getSelectedID());
			Map<String, List<Double>> arguments = new LinkedHashMap<String, List<Double>>();
			Map<String, List<Double>> possibleValues = plotable
					.getPossibleArgumentValues(true, false);

			for (String param : set.getSelectedValuesX().keySet()) {
				List<Double> usedValues = new ArrayList<Double>();
				List<Double> valuesList = possibleValues.get(param);

				if (!param.equals(set.getCurrentParamX())) {
					for (int i = 0; i < set.getSelectedValuesX().get(param)
							.size(); i++) {
						if (set.getSelectedValuesX().get(param).get(i)) {
							usedValues.add(valuesList.get(i));
						}
					}
				} else {
					usedValues.add(0.0);
				}

				arguments.put(param, usedValues);
			}

			plotable.setFunctionArguments(arguments);
			creator.setParamY(plotable.getFunctionValue());
			set.setToChartCreator(creator);
		}

		return new PortObject[] { ChartUtilities.getImage(
				creator.getChart(set.getSelectedID()), set.isExportAsSvg()) };
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

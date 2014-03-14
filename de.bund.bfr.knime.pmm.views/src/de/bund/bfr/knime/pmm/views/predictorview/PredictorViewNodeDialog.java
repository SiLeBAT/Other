package de.bund.bfr.knime.pmm.views.predictorview;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.knime.core.node.DataAwareNodeDialogPane;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObject;

import de.bund.bfr.knime.pmm.core.Utilities;
import de.bund.bfr.knime.pmm.core.math.ConvertException;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.views.chart.ChartAllPanel;
import de.bund.bfr.knime.pmm.views.chart.ChartConfigPanel;
import de.bund.bfr.knime.pmm.views.chart.ChartCreator;
import de.bund.bfr.knime.pmm.views.chart.ChartSamplePanel;
import de.bund.bfr.knime.pmm.views.chart.ChartSelectionPanel;
import de.bund.bfr.knime.pmm.views.chart.ChartUtilities;
import de.bund.bfr.knime.pmm.views.chart.Plotable;

/**
 * <code>NodeDialog</code> for the "PredictorView" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christian Thoens
 */
public class PredictorViewNodeDialog extends DataAwareNodeDialogPane implements
		ChartSelectionPanel.SelectionListener, ChartConfigPanel.ConfigListener,
		ChartSamplePanel.EditListener {

	private PredictorViewReader reader;
	private PredictorViewSettings set;

	private ChartCreator chartCreator;
	private ChartSelectionPanel selectionPanel;
	private ChartConfigPanel configPanel;
	private ChartSamplePanel samplePanel;

	/**
	 * New pane for configuring the TertiaryModelView node.
	 */
	protected PredictorViewNodeDialog() {
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout());
		addTab("Options", panel);
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, PortObject[] input)
			throws NotConfigurableException {
		set = new PredictorViewSettings();
		set.load(settings);
		reader = new PredictorViewReader((PmmPortObject) input[0]);
		((JPanel) getTab("Options")).removeAll();
		((JPanel) getTab("Options")).add(createMainComponent());
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		set.setFromConfigPanel(configPanel);
		set.setFromSelectionPanel(selectionPanel);
		set.setFromSamplePanel(samplePanel);
		set.save(settings);
	}

	private JComponent createMainComponent() {
		Map<String, List<Double>> paramsX = new LinkedHashMap<String, List<Double>>();
		Map<String, Double> minValues = new LinkedHashMap<String, Double>();
		Map<String, Double> maxValues = new LinkedHashMap<String, Double>();

		for (Plotable plotable : reader.getPlotables().values()) {
			paramsX.putAll(plotable.getFunctionArguments());

			for (Map.Entry<String, Double> min : plotable.getMinArguments()
					.entrySet()) {
				Double oldMin = minValues.get(min.getKey());

				if (oldMin == null) {
					minValues.put(min.getKey(), min.getValue());
				} else if (min.getValue() != null) {
					minValues.put(min.getKey(),
							Math.min(min.getValue(), oldMin));
				}
			}

			for (Map.Entry<String, Double> max : plotable.getMaxArguments()
					.entrySet()) {
				Double oldMax = minValues.get(max.getKey());

				if (oldMax == null) {
					maxValues.put(max.getKey(), max.getValue());
				} else if (max.getValue() != null) {
					maxValues.put(max.getKey(),
							Math.max(max.getValue(), oldMax));
				}
			}
		}

		for (String var : paramsX.keySet()) {
			if (minValues.get(var) != null) {
				paramsX.put(var, Arrays.asList(minValues.get(var)));
			}
		}

		configPanel = new ChartConfigPanel(ChartConfigPanel.PARAMETER_FIELDS,
				true);
		configPanel.setParameters(Utilities.CONCENTRATION, paramsX, minValues,
				maxValues,
				ChartUtilities.getUnits(reader.getPlotables().values()),
				Utilities.TIME);
		selectionPanel = new ChartSelectionPanel(reader.getIds(), false,
				reader.getStringColumns(), reader.getDoubleColumns(),
				reader.getConditionValues(), reader.getConditionMinValues(),
				reader.getConditionMaxValues(), reader.getConditionUnits(),
				null, reader.getFilterableColumns(), null,
				reader.getParameterData(), reader.getFormulas());
		chartCreator = new ChartCreator(reader.getPlotables(),
				reader.getShortLegend(), reader.getLongLegend());
		samplePanel = new ChartSamplePanel();

		set.setToConfigPanel(configPanel);
		set.setToSelectionPanel(selectionPanel,
				reader.getStandardVisibleColumns());
		set.setToSamplePanel(samplePanel);
		configPanel.addConfigListener(this);
		selectionPanel.addSelectionListener(this);
		samplePanel.addEditListener(this);
		createChart();

		return new ChartAllPanel(chartCreator, selectionPanel, configPanel,
				samplePanel);
	}

	private void createChart() {
		List<String> ids;

		if (configPanel.isDisplayHighlighted()) {
			ids = Arrays.asList(selectionPanel.getFocusedID());
		} else if (!selectionPanel.getSelectedIDs().isEmpty()) {
			ids = selectionPanel.getSelectedIDs();
		} else {
			ids = new ArrayList<String>();
		}

		if (!ids.isEmpty()) {
			Plotable plotable = reader.getPlotables().get(ids.get(0));

			if (configPanel.getUnitX() == null) {
				configPanel.setUnitX(plotable.getUnits().get(Utilities.TIME));
			}

			if (configPanel.getUnitY() == null) {
				configPanel.setUnitY(plotable.getUnits().get(
						Utilities.CONCENTRATION));
			}
		}

		for (String id : ids) {
			Plotable plotable = reader.getPlotables().get(id);

			if (plotable != null) {
				plotable.setSamples(samplePanel.getTimeValues());
				plotable.setFunctionArguments(configPanel.getParamsX());
			}
		}

		Map<String, double[][]> points = new LinkedHashMap<String, double[][]>();

		for (String id : ids) {
			Plotable plotable = reader.getPlotables().get(id);
			String shortId = reader.getShortIds().get(id);

			if (plotable != null) {
				try {
					points.put(shortId, plotable.getFunctionSamplePoints(
							Utilities.TIME, Utilities.CONCENTRATION,
							configPanel.getUnitX(), configPanel.getUnitY(),
							configPanel.getTransformX(),
							configPanel.getTransformY(),
							Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
							Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
				} catch (ConvertException e) {
					e.printStackTrace();
				}
			}
		}

		samplePanel.setDataPoints(points);
		chartCreator.setParamX(configPanel.getParamX());
		chartCreator.setParamY(configPanel.getParamY());
		chartCreator.setUnitX(configPanel.getUnitX());
		chartCreator.setUnitY(configPanel.getUnitY());
		chartCreator.setTransformX(configPanel.getTransformX());
		chartCreator.setTransformY(configPanel.getTransformY());
		chartCreator.setManualRange(configPanel.isManualRange());
		chartCreator.setMinX(configPanel.getMinX());
		chartCreator.setMinY(configPanel.getMinY());
		chartCreator.setMaxX(configPanel.getMaxX());
		chartCreator.setMaxY(configPanel.getMaxY());
		chartCreator.setDrawLines(configPanel.isDrawLines());
		chartCreator.setShowLegend(configPanel.isShowLegend());
		chartCreator.setAddLegendInfo(configPanel.isAddLegendInfo());
		chartCreator.setShowConfidence(configPanel.isShowConfidence());
		chartCreator.setColors(selectionPanel.getColors());
		chartCreator.setShapes(selectionPanel.getShapes());
		chartCreator.createChart(ids);
	}

	@Override
	public void selectionChanged() {
		createChart();
	}

	@Override
	public void focusChanged() {
		if (configPanel.isDisplayHighlighted()) {
			createChart();
		}
	}

	@Override
	public void configChanged() {
		createChart();
	}

	@Override
	public void timeValuesChanged() {
		createChart();
	}
}

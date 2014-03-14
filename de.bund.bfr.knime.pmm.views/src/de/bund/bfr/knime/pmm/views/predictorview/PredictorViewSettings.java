package de.bund.bfr.knime.pmm.views.predictorview;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import de.bund.bfr.knime.pmm.core.XmlUtilities;
import de.bund.bfr.knime.pmm.views.MultiViewerSettings;
import de.bund.bfr.knime.pmm.views.chart.ChartConfigPanel;
import de.bund.bfr.knime.pmm.views.chart.ChartConstants;
import de.bund.bfr.knime.pmm.views.chart.ChartCreator;
import de.bund.bfr.knime.pmm.views.chart.ChartSamplePanel;
import de.bund.bfr.knime.pmm.views.chart.ChartSelectionPanel;

public class PredictorViewSettings extends MultiViewerSettings {

	protected static final String CFG_PARAMXVALUES = "ParamXValues";
	protected static final String CFG_TIMEVALUES = "TimeValues";
	protected static final String CFG_FITTEDFILTER = "FittedFilter";

	private Map<String, Double> paramXValues;
	private List<Double> timeValues;
	private String fittedFilter;

	public PredictorViewSettings() {
		paramXValues = new LinkedHashMap<String, Double>();
		timeValues = new ArrayList<Double>();
		fittedFilter = null;
	}

	@Override
	public void load(NodeSettingsRO settings) {
		super.load(settings);

		try {
			paramXValues = XmlUtilities.fromXml(
					settings.getString(CFG_PARAMXVALUES),
					new LinkedHashMap<String, Double>());
		} catch (InvalidSettingsException e) {
			paramXValues = new LinkedHashMap<String, Double>();
		}

		try {
			timeValues = XmlUtilities
					.fromXml(settings.getString(CFG_TIMEVALUES),
							new ArrayList<Double>());
		} catch (InvalidSettingsException e1) {
			timeValues = new ArrayList<Double>();
		}

		try {
			fittedFilter = settings.getString(CFG_FITTEDFILTER);
		} catch (InvalidSettingsException e) {
			fittedFilter = null;
		}
	}

	@Override
	public void save(NodeSettingsWO settings) {
		super.save(settings);
		settings.addString(CFG_PARAMXVALUES, XmlUtilities.toXml(paramXValues));
		settings.addString(CFG_TIMEVALUES, XmlUtilities.toXml(timeValues));
		settings.addString(CFG_FITTEDFILTER, fittedFilter);
	}

	@Override
	public void setToChartCreator(ChartCreator creator) {
		super.setToChartCreator(creator);
	}

	@Override
	public void setFromConfigPanel(ChartConfigPanel configPanel) {
		super.setFromConfigPanel(configPanel);
		paramXValues = configPanel.getParamXValues();
	}

	@Override
	public void setToConfigPanel(ChartConfigPanel configPanel) {
		super.setToConfigPanel(configPanel);
		configPanel.setParamXValues(paramXValues);
	}

	@Override
	public void setFromSelectionPanel(ChartSelectionPanel selectionPanel) {
		super.setFromSelectionPanel(selectionPanel);
		fittedFilter = selectionPanel.getFilter(ChartConstants.STATUS);
	}

	@Override
	public void setToSelectionPanel(ChartSelectionPanel selectionPanel,
			Set<String> standardColumns) {
		super.setToSelectionPanel(selectionPanel, standardColumns);
		selectionPanel.setFilter(ChartConstants.STATUS, fittedFilter);
	}

	public void setFromSamplePanel(ChartSamplePanel samplePanel) {
		timeValues = samplePanel.getTimeValues();
	}

	public void setToSamplePanel(ChartSamplePanel samplePanel) {
		samplePanel.setTimeValues(timeValues);
	}

	public Map<String, Double> getParamXValues() {
		return paramXValues;
	}

	public void setParamXValues(Map<String, Double> paramXValues) {
		this.paramXValues = paramXValues;
	}

	public List<Double> getTimeValues() {
		return timeValues;
	}

	public void setTimeValues(List<Double> timeValues) {
		this.timeValues = timeValues;
	}

	public String getFittedFilter() {
		return fittedFilter;
	}

	public void setFittedFilter(String fittedFilter) {
		this.fittedFilter = fittedFilter;
	}

}

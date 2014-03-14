package de.bund.bfr.knime.pmm.views;

import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import de.bund.bfr.knime.pmm.core.Utilities;
import de.bund.bfr.knime.pmm.views.chart.ChartConfigPanel;
import de.bund.bfr.knime.pmm.views.chart.ChartConstants;
import de.bund.bfr.knime.pmm.views.chart.ChartCreator;
import de.bund.bfr.knime.pmm.views.chart.ChartSelectionPanel;

public class MultiModelViewerSettings extends MultiViewerSettings {

	protected static final String CFG_SHOWCONFIDENCE = "ShowConfidence";
	protected static final String CFG_MODELFILTER = "ModelFilter";
	protected static final String CFG_DATAFILTER = "DataFilter";
	protected static final String CFG_FITTEDFILTER = "FittedFilter";

	protected static final boolean DEFAULT_SHOWCONFIDENCE = false;

	private boolean showConfidence;
	private String modelFilter;
	private String dataFilter;
	private String fittedFilter;

	public MultiModelViewerSettings() {
		showConfidence = DEFAULT_SHOWCONFIDENCE;
		modelFilter = null;
		dataFilter = null;
		fittedFilter = null;
	}

	@Override
	public void load(NodeSettingsRO settings) {
		super.load(settings);

		try {
			showConfidence = settings.getBoolean(CFG_SHOWCONFIDENCE);
		} catch (InvalidSettingsException e) {
			showConfidence = DEFAULT_SHOWCONFIDENCE;
		}

		try {
			modelFilter = settings.getString(CFG_MODELFILTER);
		} catch (InvalidSettingsException e) {
			modelFilter = null;
		}

		try {
			dataFilter = settings.getString(CFG_DATAFILTER);
		} catch (InvalidSettingsException e) {
			dataFilter = null;
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
		settings.addBoolean(CFG_SHOWCONFIDENCE, showConfidence);
		settings.addString(CFG_MODELFILTER, modelFilter);
		settings.addString(CFG_DATAFILTER, dataFilter);
		settings.addString(CFG_FITTEDFILTER, fittedFilter);
	}

	@Override
	public void setToChartCreator(ChartCreator creator) {
		super.setToChartCreator(creator);
		creator.setShowConfidence(showConfidence);
	}

	@Override
	public void setFromConfigPanel(ChartConfigPanel configPanel) {
		super.setFromConfigPanel(configPanel);
		showConfidence = configPanel.isShowConfidence();
	}

	@Override
	public void setToConfigPanel(ChartConfigPanel configPanel) {
		super.setToConfigPanel(configPanel);
		configPanel.setShowConfidence(showConfidence);
	}

	@Override
	public void setFromSelectionPanel(ChartSelectionPanel selectionPanel) {
		super.setFromSelectionPanel(selectionPanel);
		modelFilter = selectionPanel.getFilter(Utilities.MODEL);
		dataFilter = selectionPanel.getFilter(Utilities.DATA);
		fittedFilter = selectionPanel.getFilter(ChartConstants.STATUS);
	}

	@Override
	public void setToSelectionPanel(ChartSelectionPanel selectionPanel,
			Set<String> standardColumns) {
		super.setToSelectionPanel(selectionPanel, standardColumns);
		selectionPanel.setFilter(Utilities.MODEL, modelFilter);
		selectionPanel.setFilter(Utilities.DATA, dataFilter);
		selectionPanel.setFilter(ChartConstants.STATUS, fittedFilter);
	}

	public boolean isShowConfidence() {
		return showConfidence;
	}

	public void setShowConfidence(boolean showConfidence) {
		this.showConfidence = showConfidence;
	}

	public String getModelFilter() {
		return modelFilter;
	}

	public void setModelFilter(String modelFilter) {
		this.modelFilter = modelFilter;
	}

	public String getDataFilter() {
		return dataFilter;
	}

	public void setDataFilter(String dataFilter) {
		this.dataFilter = dataFilter;
	}

	public String getFittedFilter() {
		return fittedFilter;
	}

	public void setFittedFilter(String fittedFilter) {
		this.fittedFilter = fittedFilter;
	}
}

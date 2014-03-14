package de.bund.bfr.knime.pmm.views.secondarymodelview;

import java.awt.Color;
import java.awt.Shape;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import de.bund.bfr.knime.pmm.core.XmlUtilities;
import de.bund.bfr.knime.pmm.views.ViewerSettings;
import de.bund.bfr.knime.pmm.views.chart.ChartConfigPanel;
import de.bund.bfr.knime.pmm.views.chart.ChartConstants;
import de.bund.bfr.knime.pmm.views.chart.ChartCreator;
import de.bund.bfr.knime.pmm.views.chart.ChartSelectionPanel;

public class SecondaryModelViewSettings extends ViewerSettings {

	protected static final String CFG_SELECTEDID = "SelectedID";
	protected static final String CFG_CURRENTPARAMX = "CurrentParamX";
	protected static final String CFG_SELECTEDVALUESX = "SelectedValuesX";
	protected static final String CFG_COLORLISTS = "ColorLists";
	protected static final String CFG_SHAPELISTS = "ShapeLists";
	protected static final String CFG_SHOWCONFIDENCE = "ShowConfidence";
	protected static final String CFG_FITTEDFILTER = "FittedFilter";

	protected static final boolean DEFAULT_SHOWCONFIDENCE = false;

	private String selectedID;
	private String currentParamX;
	private Map<String, List<Boolean>> selectedValuesX;
	private Map<String, List<Color>> colorLists;
	private Map<String, List<Shape>> shapeLists;
	private boolean showConfidence;
	private String fittedFilter;

	public SecondaryModelViewSettings() {
		selectedID = null;
		currentParamX = null;
		selectedValuesX = new LinkedHashMap<String, List<Boolean>>();
		colorLists = new LinkedHashMap<String, List<Color>>();
		shapeLists = new LinkedHashMap<String, List<Shape>>();
		showConfidence = DEFAULT_SHOWCONFIDENCE;
		fittedFilter = null;
	}

	@Override
	public void load(NodeSettingsRO settings) {
		super.load(settings);

		try {
			selectedID = settings.getString(CFG_SELECTEDID);
		} catch (InvalidSettingsException e) {
			selectedID = null;
		}

		try {
			currentParamX = settings.getString(CFG_CURRENTPARAMX);
		} catch (InvalidSettingsException e) {
			currentParamX = null;
		}

		try {
			selectedValuesX = XmlUtilities.fromXml(
					settings.getString(CFG_SELECTEDVALUESX),
					new LinkedHashMap<String, List<Boolean>>());
		} catch (InvalidSettingsException e) {
			selectedValuesX = new LinkedHashMap<String, List<Boolean>>();
		}

		try {
			colorLists = XmlUtilities.colorListMapFromXml(settings
					.getString(CFG_COLORLISTS));
		} catch (InvalidSettingsException e) {
			colorLists = new LinkedHashMap<String, List<Color>>();
		}

		try {
			shapeLists = XmlUtilities.shapeListMapFromXml(settings
					.getString(CFG_SHAPELISTS));
		} catch (InvalidSettingsException e) {
			shapeLists = new LinkedHashMap<String, List<Shape>>();
		}

		try {
			showConfidence = settings.getBoolean(CFG_SHOWCONFIDENCE);
		} catch (InvalidSettingsException e) {
			showConfidence = DEFAULT_SHOWCONFIDENCE;
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
		settings.addString(CFG_SELECTEDID, selectedID);
		settings.addString(CFG_CURRENTPARAMX, currentParamX);
		settings.addString(CFG_SELECTEDVALUESX,
				XmlUtilities.toXml(selectedValuesX));
		settings.addString(CFG_COLORLISTS,
				XmlUtilities.colorListMapToXml(colorLists));
		settings.addString(CFG_SHAPELISTS,
				XmlUtilities.shapeListMapToXml(shapeLists));
		settings.addBoolean(CFG_SHOWCONFIDENCE, showConfidence);
		settings.addString(CFG_FITTEDFILTER, fittedFilter);
	}

	@Override
	public void setToChartCreator(ChartCreator creator) {
		super.setToChartCreator(creator);
		creator.setShowConfidence(showConfidence);
		creator.setParamX(currentParamX);
		creator.setColorLists(colorLists);
		creator.setShapeLists(shapeLists);
	}

	@Override
	public void setFromConfigPanel(ChartConfigPanel configPanel) {
		super.setFromConfigPanel(configPanel);
		showConfidence = configPanel.isShowConfidence();
		currentParamX = configPanel.getParamX();
		selectedValuesX = configPanel.getSelectedValuesX();
	}

	@Override
	public void setToConfigPanel(ChartConfigPanel configPanel) {
		super.setToConfigPanel(configPanel);
		configPanel.setShowConfidence(showConfidence);
		configPanel.setParamX(currentParamX);
		configPanel.setSelectedValuesX(selectedValuesX);
	}

	@Override
	public void setFromSelectionPanel(ChartSelectionPanel selectionPanel) {
		super.setFromSelectionPanel(selectionPanel);
		colorLists = selectionPanel.getColorLists();
		shapeLists = selectionPanel.getShapeLists();
		fittedFilter = selectionPanel.getFilter(ChartConstants.STATUS);

		if (!selectionPanel.getSelectedIDs().isEmpty()) {
			selectedID = selectionPanel.getSelectedIDs().get(0);
		} else {
			selectedID = null;
		}
	}

	@Override
	public void setToSelectionPanel(ChartSelectionPanel selectionPanel,
			Set<String> standardColumns) {
		super.setToSelectionPanel(selectionPanel, standardColumns);
		selectionPanel.setColorLists(colorLists);
		selectionPanel.setShapeLists(shapeLists);
		selectionPanel.setFilter(ChartConstants.STATUS, fittedFilter);

		if (getSelectedID() != null) {
			selectionPanel.setSelectedIDs(Arrays.asList(selectedID));
		}
	}

	public String getSelectedID() {
		return selectedID;
	}

	public void setSelectedID(String selectedID) {
		this.selectedID = selectedID;
	}

	public String getCurrentParamX() {
		return currentParamX;
	}

	public void setCurrentParamX(String currentParamX) {
		this.currentParamX = currentParamX;
	}

	public Map<String, List<Boolean>> getSelectedValuesX() {
		return selectedValuesX;
	}

	public void setSelectedValuesX(Map<String, List<Boolean>> selectedValuesX) {
		this.selectedValuesX = selectedValuesX;
	}

	public Map<String, List<Color>> getColorLists() {
		return colorLists;
	}

	public void setColorLists(Map<String, List<Color>> colorLists) {
		this.colorLists = colorLists;
	}

	public Map<String, List<Shape>> getShapeLists() {
		return shapeLists;
	}

	public void setShapeLists(Map<String, List<Shape>> shapeLists) {
		this.shapeLists = shapeLists;
	}

	public boolean isShowConfidence() {
		return showConfidence;
	}

	public void setShowConfidence(boolean showConfidence) {
		this.showConfidence = showConfidence;
	}

	public String getFittedFilter() {
		return fittedFilter;
	}

	public void setFittedFilter(String fittedFilter) {
		this.fittedFilter = fittedFilter;
	}

}

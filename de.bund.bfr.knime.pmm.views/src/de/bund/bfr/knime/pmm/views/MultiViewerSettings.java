package de.bund.bfr.knime.pmm.views;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import de.bund.bfr.knime.pmm.core.XmlUtilities;
import de.bund.bfr.knime.pmm.views.chart.ChartCreator;
import de.bund.bfr.knime.pmm.views.chart.ChartSelectionPanel;

public class MultiViewerSettings extends ViewerSettings {

	protected static final String CFG_SELECTEDIDS = "SelectedIDs";
	protected static final String CFG_COLORS = "Colors";
	protected static final String CFG_SHAPES = "Shapes";
	protected static final String CFG_SELECTALLIDS = "SelectAllIDs";

	protected static final boolean DEFAULT_SELECTALLIDS = false;

	private List<String> selectedIDs;
	private Map<String, Color> colors;
	private Map<String, Shape> shapes;
	private boolean selectAllIDs;

	public MultiViewerSettings() {
		selectedIDs = new ArrayList<String>();
		colors = new LinkedHashMap<String, Color>();
		shapes = new LinkedHashMap<String, Shape>();
		selectAllIDs = DEFAULT_SELECTALLIDS;
	}

	@Override
	public void load(NodeSettingsRO settings) {
		super.load(settings);

		try {
			selectedIDs = XmlUtilities.fromXml(
					settings.getString(CFG_SELECTEDIDS),
					new ArrayList<String>());
		} catch (InvalidSettingsException e) {
			selectedIDs = new ArrayList<String>();
		}

		try {
			colors = XmlUtilities.colorMapFromXml(settings
					.getString(CFG_COLORS));
		} catch (InvalidSettingsException e) {
			colors = new LinkedHashMap<String, Color>();
		}

		try {
			shapes = XmlUtilities.shapeMapFromXml(settings
					.getString(CFG_SHAPES));
		} catch (InvalidSettingsException e) {
			shapes = new LinkedHashMap<String, Shape>();
		}

		try {
			selectAllIDs = settings.getBoolean(CFG_SELECTALLIDS);
		} catch (InvalidSettingsException e) {
			selectAllIDs = DEFAULT_SELECTALLIDS;
		}
	}

	@Override
	public void save(NodeSettingsWO settings) {
		super.save(settings);
		settings.addString(CFG_SELECTEDIDS, XmlUtilities.toXml(selectedIDs));
		settings.addString(CFG_COLORS, XmlUtilities.colorMapToXml(colors));
		settings.addString(CFG_SHAPES, XmlUtilities.shapeMapToXml(shapes));
		settings.addBoolean(CFG_SELECTALLIDS, selectAllIDs);
	}

	@Override
	public void setToChartCreator(ChartCreator creator) {
		super.setToChartCreator(creator);
		creator.setColors(colors);
		creator.setShapes(shapes);
	}

	@Override
	public void setFromSelectionPanel(ChartSelectionPanel selectionPanel) {
		super.setFromSelectionPanel(selectionPanel);
		selectedIDs = selectionPanel.getSelectedIDs();
		selectAllIDs = false;
		colors = selectionPanel.getColors();
		shapes = selectionPanel.getShapes();
	}

	@Override
	public void setToSelectionPanel(ChartSelectionPanel selectionPanel,
			Set<String> standardColumns) {
		super.setToSelectionPanel(selectionPanel, standardColumns);
		selectionPanel.setColors(colors);
		selectionPanel.setShapes(shapes);

		if (selectAllIDs) {
			selectionPanel.selectAllIDs();
		} else {
			selectionPanel.setSelectedIDs(selectedIDs);
		}
	}

	public List<String> getSelectedIDs() {
		return selectedIDs;
	}

	public void setSelectedIDs(List<String> selectedIDs) {
		this.selectedIDs = selectedIDs;
	}

	public Map<String, Color> getColors() {
		return colors;
	}

	public void setColors(Map<String, Color> colors) {
		this.colors = colors;
	}

	public Map<String, Shape> getShapes() {
		return shapes;
	}

	public void setShapes(Map<String, Shape> shapes) {
		this.shapes = shapes;
	}

	public boolean isSelectAllIDs() {
		return selectAllIDs;
	}

	public void setSelectAllIDs(boolean selectAllIDs) {
		this.selectAllIDs = selectAllIDs;
	}

}

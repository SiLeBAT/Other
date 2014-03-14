package de.bund.bfr.knime.pmm.views;

import java.util.LinkedHashSet;
import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import de.bund.bfr.knime.pmm.core.EmfUtilities;
import de.bund.bfr.knime.pmm.core.XmlUtilities;
import de.bund.bfr.knime.pmm.core.common.Unit;
import de.bund.bfr.knime.pmm.views.chart.ChartConfigPanel;
import de.bund.bfr.knime.pmm.views.chart.ChartConstants;
import de.bund.bfr.knime.pmm.views.chart.ChartCreator;
import de.bund.bfr.knime.pmm.views.chart.ChartSelectionPanel;

public class ViewerSettings {

	protected static final String CFG_MANUALRANGE = "ManualRange";
	protected static final String CFG_MINX = "MinX";
	protected static final String CFG_MAXX = "MaxX";
	protected static final String CFG_MINY = "MinY";
	protected static final String CFG_MAXY = "MaxY";
	protected static final String CFG_DRAWLINES = "DrawLines";
	protected static final String CFG_SHOWLEGEND = "ShowLegend";
	protected static final String CFG_ADDLEGENDINFO = "AddLegendInfo";
	protected static final String CFG_DISPLAYHIGHLIGHTED = "DisplayHighlighted";
	protected static final String CFG_EXPORTASSVG = "ExportAsSvg";
	protected static final String CFG_UNITX = "UnitX";
	protected static final String CFG_UNITY = "UnitY";
	protected static final String CFG_TRANSFORMX = "TransformX";
	protected static final String CFG_TRANSFORMY = "TransformY";
	protected static final String CFG_STANDARDVISIBLECOLUMNS = "StandardVisibleColumns";
	protected static final String CFG_VISIBLECOLUMNS = "VisibleColumns";

	protected static final boolean DEFAULT_MANUALRANGE = false;
	protected static final double DEFAULT_MINX = 0.0;
	protected static final double DEFAULT_MAXX = 100.0;
	protected static final double DEFAULT_MINY = 0.0;
	protected static final double DEFAULT_MAXY = 10.0;
	protected static final boolean DEFAULT_DRAWLINES = false;
	protected static final boolean DEFAULT_SHOWLEGEND = true;
	protected static final boolean DEFAULT_ADDLEGENDINFO = false;
	protected static final boolean DEFAULT_DISPLAYHIGHLIGHTED = false;
	protected static final boolean DEFAULT_EXPORTASSVG = false;
	protected static final String DEFAULT_TRANSFORM = ChartConstants.NO_TRANSFORM;
	protected static final boolean DEFAULT_STANDARDVISIBLECOLUMNS = true;

	private boolean manualRange;
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	private boolean drawLines;
	private boolean showLegend;
	private boolean addLegendInfo;
	private boolean displayHighlighted;
	private boolean exportAsSvg;
	private Unit unitX;
	private Unit unitY;
	private String transformX;
	private String transformY;
	private boolean standardVisibleColumns;
	private Set<String> visibleColumns;

	public ViewerSettings() {
		manualRange = DEFAULT_MANUALRANGE;
		minX = DEFAULT_MINX;
		maxX = DEFAULT_MAXX;
		minY = DEFAULT_MINY;
		maxY = DEFAULT_MAXY;
		drawLines = DEFAULT_DRAWLINES;
		showLegend = DEFAULT_SHOWLEGEND;
		addLegendInfo = DEFAULT_ADDLEGENDINFO;
		displayHighlighted = DEFAULT_DISPLAYHIGHLIGHTED;
		exportAsSvg = DEFAULT_EXPORTASSVG;
		unitX = null;
		unitY = null;
		transformX = DEFAULT_TRANSFORM;
		transformY = DEFAULT_TRANSFORM;
		standardVisibleColumns = DEFAULT_STANDARDVISIBLECOLUMNS;
		visibleColumns = new LinkedHashSet<String>();
	}

	public void load(NodeSettingsRO settings) {
		try {
			manualRange = settings.getBoolean(CFG_MANUALRANGE);
		} catch (InvalidSettingsException e) {
			manualRange = DEFAULT_MANUALRANGE;
		}

		try {
			minX = settings.getDouble(CFG_MINX);
		} catch (InvalidSettingsException e) {
			minX = DEFAULT_MINX;
		}

		try {
			maxX = settings.getDouble(CFG_MAXX);
		} catch (InvalidSettingsException e) {
			maxX = DEFAULT_MAXX;
		}

		try {
			minY = settings.getDouble(CFG_MINY);
		} catch (InvalidSettingsException e) {
			minY = DEFAULT_MINY;
		}

		try {
			maxY = settings.getDouble(CFG_MAXY);
		} catch (InvalidSettingsException e) {
			maxY = DEFAULT_MAXY;
		}

		try {
			drawLines = settings.getBoolean(CFG_DRAWLINES);
		} catch (InvalidSettingsException e) {
			drawLines = DEFAULT_DRAWLINES;
		}

		try {
			showLegend = settings.getBoolean(CFG_SHOWLEGEND);
		} catch (InvalidSettingsException e) {
			showLegend = DEFAULT_SHOWLEGEND;
		}

		try {
			addLegendInfo = settings.getBoolean(CFG_ADDLEGENDINFO);
		} catch (InvalidSettingsException e) {
			addLegendInfo = DEFAULT_ADDLEGENDINFO;
		}

		try {
			displayHighlighted = settings.getBoolean(CFG_DISPLAYHIGHLIGHTED);
		} catch (InvalidSettingsException e) {
			displayHighlighted = DEFAULT_DISPLAYHIGHLIGHTED;
		}

		try {
			exportAsSvg = settings.getBoolean(CFG_EXPORTASSVG);
		} catch (InvalidSettingsException e) {
			exportAsSvg = DEFAULT_EXPORTASSVG;
		}

		try {
			unitX = EmfUtilities.fromXml(settings.getString(CFG_UNITX), null);
		} catch (InvalidSettingsException e) {
			unitX = null;
		}

		try {
			unitY = EmfUtilities.fromXml(settings.getString(CFG_UNITY), null);
		} catch (InvalidSettingsException e) {
			unitY = null;
		}

		try {
			transformX = settings.getString(CFG_TRANSFORMX);
		} catch (InvalidSettingsException e) {
			transformX = DEFAULT_TRANSFORM;
		}

		try {
			transformY = settings.getString(CFG_TRANSFORMY);
		} catch (InvalidSettingsException e) {
			transformY = DEFAULT_TRANSFORM;
		}

		try {
			standardVisibleColumns = settings
					.getBoolean(CFG_STANDARDVISIBLECOLUMNS);
		} catch (InvalidSettingsException e) {
			standardVisibleColumns = DEFAULT_STANDARDVISIBLECOLUMNS;
		}

		try {
			visibleColumns = XmlUtilities.fromXml(
					settings.getString(CFG_VISIBLECOLUMNS),
					new LinkedHashSet<String>());
		} catch (InvalidSettingsException e) {
			visibleColumns = new LinkedHashSet<String>();
		}
	}

	public void save(NodeSettingsWO settings) {
		settings.addBoolean(CFG_MANUALRANGE, manualRange);
		settings.addDouble(CFG_MINX, minX);
		settings.addDouble(CFG_MAXX, maxX);
		settings.addDouble(CFG_MINY, minY);
		settings.addDouble(CFG_MAXY, maxY);
		settings.addBoolean(CFG_DRAWLINES, drawLines);
		settings.addBoolean(CFG_SHOWLEGEND, showLegend);
		settings.addBoolean(CFG_ADDLEGENDINFO, addLegendInfo);
		settings.addBoolean(CFG_DISPLAYHIGHLIGHTED, displayHighlighted);
		settings.addBoolean(CFG_EXPORTASSVG, exportAsSvg);
		settings.addString(CFG_UNITX, EmfUtilities.toXml(unitX));
		settings.addString(CFG_UNITY, EmfUtilities.toXml(unitY));
		settings.addString(CFG_TRANSFORMX, transformX);
		settings.addString(CFG_TRANSFORMY, transformY);
		settings.addBoolean(CFG_STANDARDVISIBLECOLUMNS, standardVisibleColumns);
		settings.addString(CFG_VISIBLECOLUMNS,
				XmlUtilities.toXml(visibleColumns));
	}

	public void setToChartCreator(ChartCreator creator) {
		creator.setManualRange(manualRange);
		creator.setMinX(minX);
		creator.setMaxX(maxX);
		creator.setMinY(minY);
		creator.setMaxY(maxY);
		creator.setDrawLines(drawLines);
		creator.setShowLegend(showLegend);
		creator.setAddLegendInfo(addLegendInfo);
		creator.setUnitX(unitX);
		creator.setUnitY(unitY);
		creator.setTransformX(transformX);
		creator.setTransformY(transformY);
	}

	public void setFromConfigPanel(ChartConfigPanel configPanel) {
		manualRange = configPanel.isManualRange();
		minX = configPanel.getMinX();
		maxX = configPanel.getMaxX();
		minY = configPanel.getMinY();
		maxY = configPanel.getMaxY();
		drawLines = configPanel.isDrawLines();
		showLegend = configPanel.isShowLegend();
		addLegendInfo = configPanel.isAddLegendInfo();
		displayHighlighted = configPanel.isDisplayHighlighted();
		exportAsSvg = configPanel.isExportAsSvg();
		unitX = configPanel.getUnitX();
		unitY = configPanel.getUnitY();
		transformX = configPanel.getTransformX();
		transformY = configPanel.getTransformY();
	}

	public void setToConfigPanel(ChartConfigPanel configPanel) {
		configPanel.setManualRange(manualRange);
		configPanel.setMinX(minX);
		configPanel.setMaxX(maxX);
		configPanel.setMinY(minY);
		configPanel.setMaxY(maxY);
		configPanel.setDrawLines(drawLines);
		configPanel.setShowLegend(showLegend);
		configPanel.setAddLegendInfo(addLegendInfo);
		configPanel.setDisplayHighlighted(displayHighlighted);
		configPanel.setExportAsSvg(exportAsSvg);
		configPanel.setUnitX(unitX);
		configPanel.setUnitY(unitY);
		configPanel.setTransformX(transformX);
		configPanel.setTransformY(transformY);
	}

	public void setFromSelectionPanel(ChartSelectionPanel selectionPanel) {
		visibleColumns = selectionPanel.getVisibleColumns();
		standardVisibleColumns = false;
	}

	public void setToSelectionPanel(ChartSelectionPanel selectionPanel,
			Set<String> standardColumns) {
		if (standardVisibleColumns) {
			selectionPanel.setVisibleColumns(standardColumns);
		} else {
			selectionPanel.setVisibleColumns(visibleColumns);
		}
	}

	public boolean isManualRange() {
		return manualRange;
	}

	public void setManualRange(boolean manualRange) {
		this.manualRange = manualRange;
	}

	public double getMinX() {
		return minX;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public double getMaxX() {
		return maxX;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public double getMinY() {
		return minY;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}

	public double getMaxY() {
		return maxY;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public boolean isDrawLines() {
		return drawLines;
	}

	public void setDrawLines(boolean drawLines) {
		this.drawLines = drawLines;
	}

	public boolean isShowLegend() {
		return showLegend;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	public boolean isAddLegendInfo() {
		return addLegendInfo;
	}

	public void setAddLegendInfo(boolean addLegendInfo) {
		this.addLegendInfo = addLegendInfo;
	}

	public boolean isDisplayHighlighted() {
		return displayHighlighted;
	}

	public void setDisplayHighlighted(boolean displayHighlighted) {
		this.displayHighlighted = displayHighlighted;
	}

	public boolean isExportAsSvg() {
		return exportAsSvg;
	}

	public void setExportAsSvg(boolean exportAsSvg) {
		this.exportAsSvg = exportAsSvg;
	}

	public Unit getUnitX() {
		return unitX;
	}

	public void setUnitX(Unit unitX) {
		this.unitX = unitX;
	}

	public Unit getUnitY() {
		return unitY;
	}

	public void setUnitY(Unit unitY) {
		this.unitY = unitY;
	}

	public String getTransformX() {
		return transformX;
	}

	public void setTransformX(String transformX) {
		this.transformX = transformX;
	}

	public String getTransformY() {
		return transformY;
	}

	public void setTransformY(String transformY) {
		this.transformY = transformY;
	}

	public boolean isStandardVisibleColumns() {
		return standardVisibleColumns;
	}

	public void setStandardVisibleColumns(boolean standardVisibleColumns) {
		this.standardVisibleColumns = standardVisibleColumns;
	}

	public Set<String> getVisibleColumns() {
		return visibleColumns;
	}

	public void setVisibleColumns(Set<String> visibleColumns) {
		this.visibleColumns = visibleColumns;
	}

}

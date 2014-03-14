package de.bund.bfr.knime.pmm.views.dataselection;

import java.awt.BorderLayout;
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
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.views.chart.ChartAllPanel;
import de.bund.bfr.knime.pmm.views.chart.ChartConfigPanel;
import de.bund.bfr.knime.pmm.views.chart.ChartCreator;
import de.bund.bfr.knime.pmm.views.chart.ChartSelectionPanel;
import de.bund.bfr.knime.pmm.views.chart.ChartUtilities;

/**
 * <code>NodeDialog</code> for the "DataSelection" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christian Thoens
 */
public class DataSelectionNodeDialog extends DataAwareNodeDialogPane implements
		ChartSelectionPanel.SelectionListener, ChartConfigPanel.ConfigListener {

	private DataSelectionReader reader;
	private DataSelectionSettings set;

	private ChartCreator chartCreator;
	private ChartSelectionPanel selectionPanel;
	private ChartConfigPanel configPanel;

	/**
	 * New pane for configuring the DataSelection node.
	 */
	protected DataSelectionNodeDialog() {
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout());
		addTab("Options", panel);
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, PortObject[] input)
			throws NotConfigurableException {
		reader = new DataSelectionReader((PmmPortObject) input[0]);
		set = new DataSelectionSettings();
		set.load(settings);

		((JPanel) getTab("Options")).removeAll();
		((JPanel) getTab("Options")).add(createMainComponent());
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		set.setFromConfigPanel(configPanel);
		set.setFromSelectionPanel(selectionPanel);
		set.save(settings);
	}

	private JComponent createMainComponent() {
		configPanel = new ChartConfigPanel(ChartConfigPanel.NO_PARAMETER_INPUT,
				false);
		configPanel.setParameters(Utilities.CONCENTRATION, Utilities.TIME,
				ChartUtilities.getUnits(reader.getPlotables().values()));
		selectionPanel = new ChartSelectionPanel(reader.getIds(), false,
				reader.getStringColumns(), null, reader.getConditionValues(),
				null, null, reader.getConditionUnits(), null, null,
				reader.getData(), null, null);
		chartCreator = new ChartCreator(reader.getPlotables(),
				reader.getShortLegend(), reader.getLongLegend());

		set.setToConfigPanel(configPanel);
		set.setToSelectionPanel(selectionPanel,
				reader.getStandardVisibleColumns());
		configPanel.addConfigListener(this);
		selectionPanel.addSelectionListener(this);
		createChart();

		return new ChartAllPanel(chartCreator, selectionPanel, configPanel);
	}

	private void createChart() {
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
		chartCreator.setColors(selectionPanel.getColors());
		chartCreator.setShapes(selectionPanel.getShapes());

		if (configPanel.isDisplayHighlighted()) {
			chartCreator.createChart(selectionPanel.getFocusedID());
		} else {
			chartCreator.createChart(selectionPanel.getSelectedIDs());
		}
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
}

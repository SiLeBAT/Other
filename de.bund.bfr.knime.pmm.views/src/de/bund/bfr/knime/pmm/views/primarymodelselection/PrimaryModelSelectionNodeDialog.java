package de.bund.bfr.knime.pmm.views.primarymodelselection;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

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
import de.bund.bfr.knime.pmm.views.chart.Plotable;

/**
 * <code>NodeDialog</code> for the "PrimaryModelSelection" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christian Thoens
 */
public class PrimaryModelSelectionNodeDialog extends DataAwareNodeDialogPane
		implements ChartSelectionPanel.SelectionListener,
		ChartConfigPanel.ConfigListener {

	private PrimaryModelSelectionReader reader;
	private PrimaryModelSelectionSettings set;

	private ChartCreator chartCreator;
	private ChartSelectionPanel selectionPanel;
	private ChartConfigPanel configPanel;

	/**
	 * New pane for configuring the PrimaryModelSelection node.
	 */
	protected PrimaryModelSelectionNodeDialog() {
		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout());
		addTab("Options", panel);
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, PortObject[] input)
			throws NotConfigurableException {
		set = new PrimaryModelSelectionSettings();
		set.load(settings);
		reader = new PrimaryModelSelectionReader((PmmPortObject) input[0]);
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
				true);
		configPanel.setParameters(Utilities.CONCENTRATION, Utilities.TIME,
				ChartUtilities.getUnits(reader.getPlotables().values()));
		selectionPanel = new ChartSelectionPanel(reader.getIds(), false,
				reader.getStringColumns(), reader.getDoubleColumns(),
				reader.getConditionValues(), null, null,
				reader.getConditionUnits(), reader.getParameterValues(),
				reader.getFilterableStringColumns(), reader.getData(),
				reader.getParameterData(), reader.getFormulas());
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
		List<String> ids;

		if (configPanel.isDisplayHighlighted()) {
			ids = Arrays.asList(selectionPanel.getFocusedID());
		} else {
			ids = selectionPanel.getSelectedIDs();
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
}

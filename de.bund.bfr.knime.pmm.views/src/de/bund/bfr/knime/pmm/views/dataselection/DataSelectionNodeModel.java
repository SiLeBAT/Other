package de.bund.bfr.knime.pmm.views.dataselection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import de.bund.bfr.knime.pmm.core.data.TimeSeries;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.core.port.PmmPortObjectSpec;
import de.bund.bfr.knime.pmm.views.chart.ChartCreator;
import de.bund.bfr.knime.pmm.views.chart.ChartUtilities;

/**
 * This is the model implementation of DataSelection.
 * 
 * 
 * @author Christian Thoens
 */
public class DataSelectionNodeModel extends NodeModel {

	private DataSelectionSettings set;

	/**
	 * Constructor for the node model.
	 */
	protected DataSelectionNodeModel() {
		super(new PortType[] { PmmPortObject.TYPE }, new PortType[] {
				PmmPortObject.TYPE, ImagePortObject.TYPE });
		set = new DataSelectionSettings();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		DataSelectionReader reader = new DataSelectionReader(
				(PmmPortObject) inObjects[0]);
		List<String> ids;

		if (set.isSelectAllIDs()) {
			ids = reader.getIds();
		} else {
			ids = set.getSelectedIDs();
		}

		Set<String> idSet = new LinkedHashSet<String>(ids);
		List<TimeSeries> selectedSeries = new ArrayList<TimeSeries>();

		for (int i = 0; i < reader.getAllIds().size(); i++) {
			if (idSet.contains(reader.getAllIds().get(i))) {
				selectedSeries.add(reader.getTimeSeries().get(i));
			}
		}

		ChartCreator creator = new ChartCreator(reader.getPlotables(),
				reader.getShortLegend(), reader.getLongLegend());

		creator.setParamX(Utilities.TIME);
		creator.setParamY(Utilities.CONCENTRATION);
		set.setToChartCreator(creator);

		PmmPortObject output = new PmmPortObject(selectedSeries,
				PmmPortObjectSpec.DATA_TYPE);
		ImagePortObject image = ChartUtilities.getImage(creator.getChart(ids),
				set.isExportAsSvg());

		return new PortObject[] { output, image };
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
		return new PortObjectSpec[] { inSpecs[0],
				ChartUtilities.getImageSpec(set.isExportAsSvg()) };
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

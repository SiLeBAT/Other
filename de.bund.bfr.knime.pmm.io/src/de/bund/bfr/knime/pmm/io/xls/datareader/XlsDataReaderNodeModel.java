package de.bund.bfr.knime.pmm.io.xls.datareader;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

import de.bund.bfr.knime.pmm.core.data.TimeSeries;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.core.port.PmmPortObjectSpec;
import de.bund.bfr.knime.pmm.io.xls.XlsReader;

/**
 * This is the model implementation of XlsDataReader.
 * 
 * 
 * @author Christian Thoens
 */
public class XlsDataReaderNodeModel extends NodeModel {

	private XlsDataReaderSettings set;

	/**
	 * Constructor for the node model.
	 */
	protected XlsDataReaderNodeModel() {
		super(new PortType[] {}, new PortType[] { PmmPortObject.TYPE });
		set = new XlsDataReaderSettings();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
			throws Exception {
		XlsReader xlsReader = new XlsReader();

		xlsReader.setFile(set.getFileName());
		xlsReader.setSheet(set.getSheetName());

		List<TimeSeries> tuples = xlsReader
				.getTimeSeriesList(set.getColumnMappings(),
						set.getConditions(), set.getConditionUnits(),
						set.getTimeUnit(), set.getConcentrationUnit(),
						set.getOrganism(), set.getOrganismColumn(),
						set.getOrganismMappings(), set.getMatrix(),
						set.getMatrixColumn(), set.getMatrixMappings());

		for (String warning : xlsReader.getWarnings()) {
			setWarningMessage(warning);
		}

		return new PortObject[] { new PmmPortObject(tuples,
				PmmPortObjectSpec.DATA_TYPE) };
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
		return new PortObjectSpec[] { new PmmPortObjectSpec(
				PmmPortObjectSpec.DATA_TYPE) };
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

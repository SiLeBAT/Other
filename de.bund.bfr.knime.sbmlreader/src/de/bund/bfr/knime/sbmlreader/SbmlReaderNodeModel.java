package de.bund.bfr.knime.sbmlreader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import de.bund.bfr.knime.IO;
import de.bund.bfr.knime.KnimeUtilities;

/**
 * This is the model implementation of SbmlReader.
 * 
 * 
 * @author Christian Thoens
 */
public class SbmlReaderNodeModel extends NodeModel {

	protected static final String CFG_IN_PATH = "inPath";

	private SettingsModelString inPath = new SettingsModelString(CFG_IN_PATH,
			null);

	/**
	 * Constructor for the node model.
	 */
	protected SbmlReaderNodeModel() {
		super(0, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
			final ExecutionContext exec) throws Exception {
		File path = KnimeUtilities.getFile(inPath.getStringValue());

		if (!path.isDirectory()) {
			throw new Exception(path + " is not a directory");
		}

		Map<String, DataType> columns = new LinkedHashMap<String, DataType>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		File[] files = path.listFiles();
		int index1 = 0;

		for (File file : files) {
			SBMLDocument doc = SBMLReader.read(file);

			readSBML(doc, columns, rows);
			exec.checkCanceled();
			exec.setProgress((double) index1 / (double) files.length);
			index1++;
		}

		DataTableSpec spec = createSpec(columns);
		BufferedDataContainer container = exec.createDataContainer(spec);
		int index2 = 0;

		for (Map<String, Object> row : rows) {
			DataCell[] cells = new DataCell[spec.getNumColumns()];

			for (int i = 0; i < spec.getNumColumns(); i++) {
				cells[i] = IO.createCellFromObject(row.get(spec
						.getColumnNames()[i]));
			}

			container.addRowToTable(new DefaultRow(index2 + "", cells));
			exec.checkCanceled();
		}

		container.close();

		return new BufferedDataTable[] { container.getTable() };
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
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		inPath.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		inPath.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		inPath.validateSettings(settings);
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

	private void readSBML(SBMLDocument doc, Map<String, DataType> columns,
			List<Map<String, Object>> rows) {
		// TODO
	}

	private static DataTableSpec createSpec(Map<String, DataType> columns) {
		List<DataColumnSpec> specs = new ArrayList<DataColumnSpec>();

		for (String name : columns.keySet()) {
			specs.add(new DataColumnSpecCreator(name, columns.get(name))
					.createSpec());
		}

		return new DataTableSpec(specs.toArray(new DataColumnSpec[0]));
	}

}

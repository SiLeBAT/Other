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
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
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
import org.sbml.jsbml.AlgebraicRule;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Rule;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.UnitDefinition;

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

	private static final String MODEL_ID = "ModelID";
	private static final String ORGANISM = "Organism";
	private static final String MATRIX = "Matrix";
	private static final String FORMULA = "Formula";

	private static final String UNIT = " Unit";

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
			index2++;
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
		Model model = doc.getModel();
		Map<String, Object> row = new LinkedHashMap<String, Object>();

		if (!columns.containsKey(MODEL_ID)) {
			columns.put(MODEL_ID, StringCell.TYPE);
		}

		row.put(MODEL_ID, model.getId());

		Species organism = model.getSpecies(0);
		Compartment matrix = model.getCompartment(0);

		if (organism != null) {
			if (!columns.containsKey(ORGANISM)) {
				columns.put(ORGANISM, StringCell.TYPE);
			}

			row.put(ORGANISM, organism.getName());
		}

		if (matrix != null) {
			if (!columns.containsKey(MATRIX)) {
				columns.put(MATRIX, StringCell.TYPE);
			}

			row.put(MATRIX, matrix.getName());
		}

		AlgebraicRule formula = getAssignmentRule(model.getListOfRules());

		if (!columns.containsKey(FORMULA)) {
			columns.put(FORMULA, StringCell.TYPE);
		}

		row.put(FORMULA, formula.getMath().toFormula());

		for (Parameter param : model.getListOfParameters()) {
			String name = param.getId();
			UnitDefinition unit = param.getUnitsInstance();

			if (unit != null) {
				if (!columns.containsKey(name + UNIT)) {
					columns.put(name + UNIT, StringCell.TYPE);
				}

				row.put(name + UNIT, unit.toString());
			}
		}

		for (Parameter param : model.getListOfParameters()) {
			String name = param.getId();

			if (!columns.containsKey(name)) {
				columns.put(name, DoubleCell.TYPE);
			}

			row.put(name, param.getValue());
		}

		rows.add(row);
	}

	private static DataTableSpec createSpec(Map<String, DataType> columns) {
		List<DataColumnSpec> specs = new ArrayList<DataColumnSpec>();

		for (String name : columns.keySet()) {
			specs.add(new DataColumnSpecCreator(name, columns.get(name))
					.createSpec());
		}

		return new DataTableSpec(specs.toArray(new DataColumnSpec[0]));
	}

	private static AlgebraicRule getAssignmentRule(ListOf<Rule> rules) {
		return (AlgebraicRule) rules.get(0);
	}

}

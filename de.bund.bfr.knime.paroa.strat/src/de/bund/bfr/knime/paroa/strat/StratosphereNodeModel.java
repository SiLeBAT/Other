package de.bund.bfr.knime.paroa.strat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;

/**
 * This is the model implementation of Stratosphere.
 * 
 * 
 * @author Markus Freitag
 */
public class StratosphereNodeModel extends NodeModel {

	// the logger instance
	private static final NodeLogger logger = NodeLogger
			.getLogger(StratosphereNodeModel.class);

	static final String CFGKEY_METHODS = "methods";
	static final String CFGKEY_JAR = "Stratosphere jar";
	static final String CFGKEY_LOCAL = "local";
	static final String CFGKEY_INPUT_SALES = "Sales Data Input";
	static final String CFGKEY_STRAT_PATH = "Stratosphere Path";

	static final String STRAT_OUTBREAKS = "/data/outbreaks/";
	static final String STRAT_SALES = "/data/sales/";
	static final String STRAT_RESULTS = "/data/results/";

	enum METHODS {
		BOTH, SPC, LBM
	};

	enum INPUTS {
		SALES, OUTBREAKS
	}; // order matters!

	static final String DEFAULT_STRAT_PATH = "/opt/stratosphere/";
	static final String DEFAULT_METHODS = METHODS.BOTH.name();
	static final String DEFAULT_EMPTYSTRING = "";
	
	public static final String[] METHOD_CHIOCES = { METHODS.BOTH.name(),
			METHODS.SPC.name(), METHODS.LBM.name() };
	public static final String[] LOCAL = { "LOCAL", "CLUSTER" };

	private final SettingsModelString m_methods = new SettingsModelString(
			StratosphereNodeModel.CFGKEY_METHODS,
			StratosphereNodeModel.DEFAULT_METHODS);

	private final SettingsModelString m_jar = new SettingsModelString(
			StratosphereNodeModel.CFGKEY_JAR,
			StratosphereNodeModel.DEFAULT_EMPTYSTRING);
/*
	private final SettingsModelString m_local = new SettingsModelString(
			StratosphereNodeModel.CFGKEY_LOCAL,
			StratosphereNodeModel.DEFAULT_EMPTYSTRING);
*/
	private final SettingsModelString m_inputSales = new SettingsModelString(
			StratosphereNodeModel.CFGKEY_INPUT_SALES,
			StratosphereNodeModel.DEFAULT_EMPTYSTRING);

	private final SettingsModelString m_stratospherePath = new SettingsModelString(
			StratosphereNodeModel.CFGKEY_STRAT_PATH,
			StratosphereNodeModel.DEFAULT_STRAT_PATH);

	/**
	 * Constructor for the node model.
	 */
	protected StratosphereNodeModel() {

		super(new PortType[] { FlowVariablePortObject.TYPE,
				BufferedDataTable.TYPE }, new PortType[] {
				BufferedDataTable.TYPE, BufferedDataTable.TYPE });
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final PortObject[] inData,
			final ExecutionContext exec) throws Exception {

		String methodsChoice = m_methods.getStringValue();
		String jarChoice = m_jar.getStringValue();
//		String localChoice = m_local.getStringValue();
		String inputSalesChoice = m_inputSales.getStringValue();
		String stratospherePathChoice = m_stratospherePath.getStringValue();

		logger.debug(methodsChoice);
		logger.debug(jarChoice);
//		logger.debug(localChoice);
		logger.debug(inputSalesChoice);

		StratosphereConnection paroa_connection = new StratosphereConnection(stratospherePathChoice);
		FileHandle outbreaksFile = createFileFromOutbreaks((BufferedDataTable) inData[INPUTS.OUTBREAKS.ordinal()]);
		FileHandle paroa_inputOutbreaks = outbreaksFile;
		FileHandle paroa_inputSales = new FileHandle(inputSalesChoice);
		FileHandle paroa_jars = new FileHandle(jarChoice);
		FileHandle paroa_output = new FileHandle(stratospherePathChoice
				+ STRAT_RESULTS + paroa_inputSales.hashCode());

		// here happens the action
		paroa_connection.runParoa(
				paroa_jars, 
				paroa_inputOutbreaks,
				paroa_inputSales, 
				paroa_output
		);

		DataColumnSpec[] allColSpecs = new DataColumnSpec[2];
		allColSpecs[0] = new DataColumnSpecCreator("Product", StringCell.TYPE)
				.createSpec();
		allColSpecs[1] = new DataColumnSpecCreator("Value", DoubleCell.TYPE)
				.createSpec();
		DataTableSpec outputSpec = new DataTableSpec(allColSpecs);

		// reading results file
		File resultFile = new File(paroa_output.getPath());
		FileInputStream stream = new FileInputStream(resultFile);
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader buffered_reader = new BufferedReader(reader);

		BufferedDataContainer spcContainer = exec
				.createDataContainer(outputSpec);
		BufferedDataContainer lbmContainer = exec
				.createDataContainer(outputSpec);

		String currentLine = buffered_reader.readLine();
		while (currentLine != null) {
			String[] lineValues = currentLine.split(";");

			int method = Integer.parseInt(lineValues[0]);
			RowKey key = new RowKey(lineValues[1]);
			DataCell[] cells = new DataCell[2];
			cells[0] = new StringCell(lineValues[1]);
			cells[1] = new DoubleCell(Double.parseDouble(lineValues[2]));

			DataRow row = new DefaultRow(key, cells);
			if (method == 0)
				spcContainer.addRowToTable(row);
			else if (method == 1)
				lbmContainer.addRowToTable(row);
			else {
				logger.debug("unknown method: " + method);
			}
			logger.info("ADGASDG");
			exec.checkCanceled();

			currentLine = buffered_reader.readLine();
		}

		spcContainer.close();
		lbmContainer.close();
		BufferedDataTable spcTable = spcContainer.getTable();
		BufferedDataTable lbmTable = lbmContainer.getTable();

		BufferedDataTable[] output = new BufferedDataTable[2];
		output[0] = spcTable;
		output[1] = lbmTable;

		return output;
	}

	private FileHandle createFileFromOutbreaks(BufferedDataTable outBreakData)
			throws FileNotFoundException {
		File outbreaksFile = new File(m_stratospherePath.getStringValue() + STRAT_OUTBREAKS
				+ "outbreak_" + outBreakData.hashCode());
		FileOutputStream stream = new FileOutputStream(outbreaksFile);
		OutputStreamWriter writer = new OutputStreamWriter(stream);
		BufferedWriter bufferedWriter = new BufferedWriter(writer);

		CloseableRowIterator allRows = outBreakData.iterator();
		try {
			while (allRows.hasNext()) {
				DataRow currentRow = allRows.next();
				String plz = currentRow.getKey().toString();
				String cases = currentRow.getCell(0).toString();
				bufferedWriter.write(plz + "," + cases + "\n");
			}
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String path = outbreaksFile.getAbsolutePath();
		return new FileHandle(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {

		// TODO: check if user settings are available, fit to the incoming
		// table structure, and the incoming types are feasible for the node
		// to execute. If the node can execute in its current state return
		// the spec of its output data table(s) (if you can, otherwise an array
		// with null elements), or throw an exception with a useful user message

		return new PortObjectSpec[] { null, null };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {

		m_methods.saveSettingsTo(settings);
		m_stratospherePath.saveSettingsTo(settings);
//		m_local.saveSettingsTo(settings);
		m_jar.saveSettingsTo(settings);
		m_inputSales.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {

		m_methods.loadSettingsFrom(settings);
		m_stratospherePath.loadSettingsFrom(settings);
//		m_local.loadSettingsFrom(settings);
		m_jar.loadSettingsFrom(settings);
		m_inputSales.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {

		m_methods.validateSettings(settings);
		m_stratospherePath.validateSettings(settings);
//		m_local.validateSettings(settings);
		m_jar.validateSettings(settings);
		m_inputSales.validateSettings(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {

		// TODO load internal data.
		// Everything handed to output ports is loaded automatically (data
		// returned by the execute method, models loaded in loadModelContent,
		// and user settings set through loadSettingsFrom - is all taken care
		// of). Load here only the other internals that need to be restored
		// (e.g. data used by the views).

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {

		// TODO save internal models.
		// Everything written to output ports is saved automatically (data
		// returned by the execute method, models saved in the saveModelContent,
		// and user settings saved through saveSettingsTo - is all taken care
		// of). Save here only the other internals that need to be preserved
		// (e.g. data used by the views).

	}

}

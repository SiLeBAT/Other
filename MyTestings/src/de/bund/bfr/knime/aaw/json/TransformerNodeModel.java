package de.bund.bfr.knime.aaw.json;

import java.io.File;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.date.DateAndTimeValue;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
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

/**
 * This is the model implementation of Transformer.
 * 
 *
 * @author 
 */
public class TransformerNodeModel extends NodeModel {
    
	static final String JSON_PREFIX = "jsonprefix";
	
	private final SettingsModelString m_jsonPrefix = new SettingsModelString(JSON_PREFIX, "");

	/**
     * Constructor for the node model.
     */
    protected TransformerNodeModel() {
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

    	String columns = "\"columns\":[\n";
    	String colHeaders = "\"colHeaders\":[\n";
    	DataTableSpec dts = inData[0].getSpec();
    	/*
    	 * data: 'var',
            type: 'date',
            dateFormat: 'MM/DD/YYYY'
    	 */
    	for (int i=0;i<dts.getNumColumns();i++) { // format: '0.00%',  dateFormat: 'DD.MM.YYYY'
    		DataColumnSpec spec = dts.getColumnSpec(i);
    		DataType dt = spec.getType();
    		String type = "\"type\": \"text\"";
    		if (dt.isCompatible(DoubleValue.class) || dt.isCompatible(IntValue.class)) {
    			type = "\"type\": \"numeric\"";
    		}
    		else if (dt.isCompatible(DateAndTimeValue.class)) {
    			type = "\"type\": \"date\",  \"dateFormat\": \"DD.MM.YYYY\"";
    		}
    		columns += "	{\"data\":\"" + i + "\"," + type + "}" + (i < dts.getNumColumns() - 1 ? "," : "") + "\n";
    		colHeaders += "\"" + JSONObject.escape(spec.getName()) + "\"" + (i < dts.getNumColumns() - 1 ? "," : "") + "\n";
    	}
    	columns += "]";
    	colHeaders += "]";

    	String data = "\"data\":[\n";
		for (DataRow row : inData[0]) {
			data += "{";
			for (int i=0;i<dts.getNumColumns();i++) {
				data += "\"" + i + "\"" + ":";
	    		DataColumnSpec spec = dts.getColumnSpec(i);
	    		DataType dt = spec.getType();
	    		if (row.getCell(i).isMissing()) data += "null";
	    		else {
		    		if (dt.isCompatible(IntValue.class)) {
						data += ((IntCell) row.getCell(i)).getIntValue();
		    		}
		    		else if (dt.isCompatible(DoubleValue.class)) {
						data += ((DoubleCell) row.getCell(i)).getDoubleValue();
		    		}
		    		else if (dt.isCompatible(DateAndTimeValue.class)) {
						data += JSONObject.escape(((DateAndTimeCell) row.getCell(i)).getStringValue());
		    		}
		    		else {
						data += "\"" + JSONObject.escape(((StringCell) row.getCell(i)).getStringValue()) + "\"";
		    		}
	    		}
	    		data += (i < dts.getNumColumns() - 1 ? "," : "");
			}
			data += "},\n";
		}
		data = data.substring(0, data.length() - 2) + "\n]";

		String result = "{\n\"" + m_jsonPrefix.getStringValue() + "\":{\n" + columns + ",\n\n" + colHeaders + ",\n\n" + data + "\n}\n}";
				
		BufferedDataContainer buf = exec.createDataContainer(getSpec());

		DataCell[] cells = new DataCell[1];
		cells[0] = new StringCell(result);
		DataRow outputRow = new DefaultRow(RowKey.createRowKey(buf.size()), cells);
		buf.addRowToTable(outputRow);

		buf.close();
        return new BufferedDataTable[]{buf.getTable()};
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
		return new DataTableSpec[] {getSpec()};
    }
    private DataTableSpec getSpec() {
		DataColumnSpec[] outSpec = new DataColumnSpec[1];
		outSpec[0] = new DataColumnSpecCreator("JSON", StringCell.TYPE).createSpec();
		return new DataTableSpec(outSpec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	m_jsonPrefix.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_jsonPrefix.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_jsonPrefix.validateSettings(settings);
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


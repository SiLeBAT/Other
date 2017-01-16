package de.bund.bfr.knime.aaw.json;

import java.io.File;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
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

/**
 * This is the model implementation of Transformer.
 * 
 *
 * @author 
 */
public class TransformerNodeModel extends NodeModel {
    
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

    	String result = "{\"metadata\":[\n";
    	DataTableSpec dts = inData[0].getSpec();
    	for (int i=0;i<dts.getNumColumns();i++) {
    		result += "	{\"name\":\"" + JSONObject.escape(dts.getColumnNames()[i]) + "\",\"label\":\"" + JSONObject.escape(dts.getColumnNames()[i]) + "\",\"datatype\":\"string\",\"editable\":true}" + (i < dts.getNumColumns() - 1 ? "," : "") + "\n";
    	}
    	result += "],\n\n\"data\":[\n";
    	
    	int rowlfd = 0;
		for (DataRow row : inData[0]) {
			result += "{\"id\":" + rowlfd + ", \"values\":{";
			for (int i=0;i<dts.getNumColumns();i++) {
				result += "\"" + JSONObject.escape(dts.getColumnNames()[i]) + "\":\"" + (row.getCell(i).isMissing() ? "" : JSONObject.escape(((StringCell) row.getCell(i)).getStringValue())) + "\"" + (i < dts.getNumColumns() - 1 ? "," : "");
			}
			result += "}},\n";
			rowlfd++;
			//result += "{"id":1, "values":{"country":"uk","age":33,"name":"Duke","firstname":"Patience","height":1.842,"email":"patience.duke@gmail.com","lastvisit":"11\/12\/2002"}},";			
		}
		result = result.substring(0, result.length() - 2) + "\n]}";

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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
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


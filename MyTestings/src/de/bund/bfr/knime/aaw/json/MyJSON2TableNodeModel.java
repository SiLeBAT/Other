package de.bund.bfr.knime.aaw.json;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
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
import org.knime.core.data.json.JSONValue;


/**
 * This is the model implementation of MyJSON2Table.
 * 
 *
 * @author BfR
 */
public class MyJSON2TableNodeModel extends NodeModel {
    
    /**
     * Constructor for the node model.
     */
    protected MyJSON2TableNodeModel() {
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
        
    	Map<Integer, Map<String, String>> map = new HashMap<>();
    	Map<String, Integer> allColumns = new LinkedHashMap<>();
    	int newCol = 0;
    	for (DataRow row : inData[0]) {
    		JSONValue jv = ((JSONValue) row.getCell(0));
    		JsonObject obj = (JsonObject) jv.getJsonValue();
    		JsonArray ja = (JsonArray) obj.get("data");
            for (int i = 0; i < ja.size(); i++) {
            	Map<String, String> rowmap = new HashMap<>();
             	JsonObject obji = (JsonObject) ja.get(i);
            	for (String key : obji.keySet()) {
            		if (!allColumns.containsKey(key)) {
            			allColumns.put(key, newCol);
            			newCol++;
            		}
            		if (obji.get(key) == JsonValue.NULL) rowmap.put(key, null);
            		else {
            			Object o = obji.get(key);
            			if (o instanceof JsonNumber) rowmap.put(key, o.toString());
            			else rowmap.put(key, obji.getString(key));
            		}
            	}
           		map.put(i, rowmap);
            }
		}
    	System.err.println(map);
        DataColumnSpec[] allColSpecs = new DataColumnSpec[allColumns.size()];
        for (String col : allColumns.keySet()) {
            allColSpecs[allColumns.get(col)] = new DataColumnSpecCreator(col, StringCell.TYPE).createSpec();        	
        }
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        for (Integer rowI : map.keySet()) {
            RowKey key = new RowKey("Row " + rowI);
            // the cells of the current row, the types of the cells must match
            // the column spec (see above)
            DataCell[] cells = new DataCell[allColumns.size()];
            for (String col : allColumns.keySet()) {
            	Map<String, String> mapr = map.get(rowI);
            	if (mapr.containsKey(col) && mapr.get(col) != null) cells[allColumns.get(col)] = new StringCell(mapr.get(col)); 
            	else cells[allColumns.get(col)] = DataType.getMissingCell();
            }
            DataRow row = new DefaultRow(key, cells);
            container.addRowToTable(row);
            
            // check if the execution monitor was canceled
            exec.checkCanceled();
            exec.setProgress(rowI / (double) map.size(), 
                "Adding row");
        }

        // once we are done, we close the container and return its table
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[]{out};
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
        return new DataTableSpec[]{null};
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


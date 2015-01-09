package de.bund.bfr.knime.aaw.lims;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.BooleanCell;
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

/**
 * This is the model implementation of MyTab21.
 * 
 *
 * @author aaw
 */
public class MyTab21NodeModel extends NodeModel {
    
    /**
     * Constructor for the node model.
     */
    protected MyTab21NodeModel() {
        super(2, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	// dataset
    	for (DataRow row : inData[0]) {
    		
    	}
    	
    	// Wirkstoffe cutoffs
    	HashMap<String, Wirkstoff> ws = new HashMap<String, Wirkstoff>();
    	DataTableSpec dts = inData[1].getSpec();
    	String[] cn = dts.getColumnNames();
    	for (DataRow row : inData[1]) {
    		Wirkstoff w = new Wirkstoff();
    		for (int i=0;i<dts.getNumColumns();i++) {
    			DataCell dc = row.getCell(i);
        	    if (!dc.isMissing()) {
        			if (cn[i].equalsIgnoreCase("Gruppe")) w.setGruppe(((StringCell) dc).getStringValue());
        			else if (cn[i].equalsIgnoreCase("Name")) w.setName(((StringCell) dc).getStringValue());
        			else if (cn[i].equalsIgnoreCase("Kurz")) w.setKurz(((StringCell) dc).getStringValue());
        	    }
    		}
    		if (w.getKurz() != null) {
    			ws.put(w.getKurz(), w);
    		}
    	}
    	    	
    	BufferedDataContainer buf2 = exec.createDataContainer(getSpec2());

		RowKey key = RowKey.createRowKey(0);
		DataCell[] cells = new DataCell[5];
		cells[0] = DataType.getMissingCell();
		cells[1] = DataType.getMissingCell();
		cells[2] = DataType.getMissingCell();
		cells[3] = DataType.getMissingCell();
		cells[4] = DataType.getMissingCell();
		DataRow outputRow = new DefaultRow(key, cells);
		buf2.addRowToTable(outputRow);

    	buf2.close();
        return new BufferedDataTable[]{buf2.getTable()};
    }
	private DataTableSpec getSpec2() {
		DataColumnSpec[] spec = new DataColumnSpec[5];
		spec[0] = new DataColumnSpecCreator("Gruppe", StringCell.TYPE).createSpec();
		spec[1] = new DataColumnSpecCreator("Sum", IntCell.TYPE).createSpec();
		spec[2] = new DataColumnSpecCreator("percent", DoubleCell.TYPE).createSpec();
		spec[3] = new DataColumnSpecCreator("totalCount", StringCell.TYPE).createSpec();
		spec[4] = new DataColumnSpecCreator("Programm", StringCell.TYPE).createSpec();
		return new DataTableSpec(spec);
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
		return new DataTableSpec[] {getSpec2()};
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


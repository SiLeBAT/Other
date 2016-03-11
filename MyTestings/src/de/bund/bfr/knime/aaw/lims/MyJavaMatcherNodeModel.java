package de.bund.bfr.knime.aaw.lims;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is the model implementation of MyJavaJoiner.
 * 
 *
 * @author BfR
 */
public class MyJavaMatcherNodeModel extends NodeModel {
    
	static final String COLSEL = "selCol";
	static final String COLSELLIMS = "selColLims";
	static final String WILD = "wild";
	static final String NUMBERSONLY = "numbersonly";
	
	private final SettingsModelString m_colSel = new SettingsModelString(COLSEL, "none");
	private final SettingsModelString m_colSelLims = new SettingsModelString(COLSELLIMS, "none");
	private final SettingsModelBoolean m_wildSearch = new SettingsModelBoolean(WILD, false);
	private final SettingsModelBoolean m_onlyNumbers = new SettingsModelBoolean(NUMBERSONLY, false);
	
	/**
     * Constructor for the node model.
     */
    protected MyJavaMatcherNodeModel() {
        super(2, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	boolean doAll = false;
    	DataTableSpec dts = inData[0].getSpec();
    	DataTableSpec dts1 = inData[1].getSpec();
    	int numOutCols = dts1.getNumColumns() + 1;
		int theCol0 = -1;
    	for (int i=0;i<dts.getNumColumns();i++) {
    		if (dts.getColumnNames()[i].equals(m_colSel.getStringValue())) { // "PN_conTP"
    			theCol0 = i;
    			break;
    		}
    	}
		int theCol1 = -1;
    	for (int i=0;i<dts1.getNumColumns();i++) {
    		if (dts1.getColumnNames()[i].equals(m_colSelLims.getStringValue())) { // "KundenProbenr"
    			theCol1 = i;
    			break;
    		}
    	}
    	Map<String, List<DataRow>> limsMap = new HashMap<>();
		for (DataRow row1 : inData[1]) {
			DataCell dc1 = row1.getCell(theCol1);
			if (!dc1.isMissing()) {
				String limsStr = ((StringCell) dc1).getStringValue();
        		if (m_onlyNumbers.getBooleanValue()) limsStr = limsStr.replaceAll("[^0-9]","");
				if (!limsMap.containsKey(limsStr)) limsMap.put(limsStr, new ArrayList<>());
				List<DataRow> l = limsMap.get(limsStr);
				l.add(row1);
			}
		}

		BufferedDataContainer buf = exec.createDataContainer(getSpec(dts1, m_colSel.getStringValue()));
    	long rk = 0;
    	int rowLfd = 0;
		DataCell[] cells = new DataCell[numOutCols];
    	if (theCol0 >= 0 && theCol1 >= 0) {
        	for (DataRow row : inData[0]) {
        		boolean success = false;
        		DataCell dc = row.getCell(theCol0);
        		if (!dc.isMissing()) {
            		String queryStr = ((StringCell) dc).getStringValue();
            		queryStr = queryStr.replace("%", ".*");
            		if (m_wildSearch.getBooleanValue()) queryStr = ".*" + queryStr + ".*";
            		for (String limsStr : limsMap.keySet()) {
    					if (limsStr.matches(queryStr)) {
    						for (DataRow row1 : limsMap.get(limsStr)) {
        						for (int i=0;i<dts1.getNumColumns();i++) {
        							cells[i] = row1.getCell(i);
        						}
        						cells[numOutCols - 1] = dc;
    							RowKey key = RowKey.createRowKey(rk);rk++;
        		        		DataRow outputRow = new DefaultRow(key, cells);
        		        		buf.addRowToTable(outputRow);
    						}
    		        		success = true;
    					}
            		}
        		}
        		if (doAll && !success) {
					for (int i=0;i<dts1.getNumColumns();i++) {
						cells[i] = DataType.getMissingCell();
					}
					cells[numOutCols - 1] = dc;
					RowKey key = RowKey.createRowKey(rk);rk++;
	        		DataRow outputRow = new DefaultRow(key, cells);
	        		buf.addRowToTable(outputRow);
        		}
        		exec.setProgress(((double)rowLfd)/inData[0].size());
        		rowLfd++;
        	}
    		exec.setProgress(1);
    	}
    	buf.close();
        return new BufferedDataTable[]{buf.getTable()};
    }

	private DataTableSpec getSpec(DataTableSpec inSpec, String lastColname) {
		DataColumnSpec[] outSpec = new DataColumnSpec[inSpec.getNumColumns() + 1];
		for (int i=0;i<inSpec.getNumColumns();i++) {
			DataColumnSpec inSpecCols = inSpec.getColumnSpec(i);
			outSpec[i] = new DataColumnSpecCreator(inSpecCols.getName(), inSpecCols.getType()).createSpec();
		}
		outSpec[inSpec.getNumColumns()] = new DataColumnSpecCreator(lastColname, StringCell.TYPE).createSpec();
		return new DataTableSpec(outSpec);
	}

	/**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
		return new DataTableSpec[] {getSpec(inSpecs[1], m_colSel.getStringValue())};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	m_colSel.saveSettingsTo(settings);
    	m_colSelLims.saveSettingsTo(settings);
    	m_wildSearch.saveSettingsTo(settings);
    	m_onlyNumbers.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_colSel.loadSettingsFrom(settings);
    	if (settings.containsKey(COLSELLIMS)) m_colSelLims.loadSettingsFrom(settings);
    	if (settings.containsKey(WILD)) m_wildSearch.loadSettingsFrom(settings);
    	if (settings.containsKey(NUMBERSONLY)) m_onlyNumbers.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_colSel.validateSettings(settings);
    	if (settings.containsKey(COLSELLIMS)) m_colSelLims.validateSettings(settings);
    	if (settings.containsKey(WILD)) m_wildSearch.validateSettings(settings);
    	if (settings.containsKey(NUMBERSONLY)) m_onlyNumbers.validateSettings(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

}


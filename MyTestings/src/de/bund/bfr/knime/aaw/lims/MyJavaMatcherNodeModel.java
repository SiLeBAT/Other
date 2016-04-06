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
	static final String BVL_SAMPLE = "bvlsample";
	static final String LIMS_SAMPLE = "limssample";
	static final String WILD = "wild";
	static final String NUMBERSONLY = "numbersonly";
	
	private final SettingsModelString m_colSel = new SettingsModelString(COLSEL, "none");
	private final SettingsModelString m_colSelLims = new SettingsModelString(COLSELLIMS, "none");
	private final SettingsModelString m_bvlSample = new SettingsModelString(BVL_SAMPLE, "none");
	private final SettingsModelString m_limsSample = new SettingsModelString(LIMS_SAMPLE, "none");
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
    	DataTableSpec dts0 = inData[0].getSpec();
    	DataTableSpec dts1 = inData[1].getSpec();
    	int numOutCols = dts0.getNumColumns() + dts1.getNumColumns();
		int theCol0 = -1;
    	for (int i=0;i<dts0.getNumColumns();i++) {
    		if (dts0.getColumnNames()[i].equals(m_colSel.getStringValue())) { // "PN_conTP"
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
		int theCol2 = -1;
    	for (int i=0;i<dts0.getNumColumns();i++) {
    		if (dts0.getColumnNames()[i].equals(m_bvlSample.getStringValue())) { // "...TEXT3"
    			theCol2 = i;
    			break;
    		}
    	}
		int theCol3 = -1;
    	for (int i=0;i<dts1.getNumColumns();i++) {
    		if (dts1.getColumnNames()[i].equals(m_limsSample.getStringValue())) { // "Ergebnis"
    			theCol3 = i;
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

		BufferedDataContainer buf = exec.createDataContainer(getSpec(dts0,dts1));
    	long rk = 0;
    	int rowLfd = 0;
		DataCell[] cells = new DataCell[numOutCols];
    	if (theCol0 >= 0 && theCol1 >= 0) {
        	for (DataRow row0 : inData[0]) {
        		boolean success = false;
        		DataCell dc = row0.getCell(theCol0);
        		if (!dc.isMissing()) {
            		String queryStr = ((StringCell) dc).getStringValue();
            		queryStr = queryStr.replace("%", ".*");
            		if (m_wildSearch.getBooleanValue()) queryStr = ".*" + queryStr + ".*";
            		for (String limsStr : limsMap.keySet()) {
    					if (limsStr.matches(queryStr)) {
    						List<DataRow> bestScore = new ArrayList<>();
    						double topScore = 0;
    						for (DataRow row1 : limsMap.get(limsStr)) {
    							double score = 0; 
        						if (theCol2 >= 0 && theCol3 >= 0) {
        							DataCell bvlSample = row0.getCell(theCol2);
        							DataCell limsSample = row1.getCell(theCol3);
        							if (!bvlSample.isMissing() && !limsSample.isMissing()) {
            							String bvl = ((StringCell) bvlSample).getStringValue();
            							String lims = ((StringCell) limsSample).getStringValue();
                						//Hier sollten die ähnlichsten in Bezug auf SamplingResult genommen werden, falls es mehrere Kandiadten gibt!
            							score = StringSimilarity.diceCoefficientOptimized(bvl, lims);
        							}
        						}
    							if (score == topScore) {
    								bestScore.add(row1);
    							}
    							else if (score > topScore) {
    								bestScore.clear();
    								topScore = score;
    								bestScore.add(row1);
    							}
    						}    							
							for (DataRow row1 : bestScore) {
        						for (int i=0;i<dts0.getNumColumns();i++) {
        							cells[i] = row0.getCell(i);
        						}
        						for (int i=0;i<dts1.getNumColumns();i++) {
        							cells[dts0.getNumColumns()+i] = row1.getCell(i);
        						}
        						//cells[numOutCols - 1] = dc;
    							RowKey key = RowKey.createRowKey(rk);rk++;
        		        		DataRow outputRow = new DefaultRow(key, cells);
        		        		buf.addRowToTable(outputRow);
        		        		success = true;
							}
    					}
            		}
        		}
        		if (doAll && !success) {
					for (int i=0;i<dts0.getNumColumns();i++) {
						cells[i] = row0.getCell(i);
					}
					for (int i=0;i<dts1.getNumColumns();i++) {
						cells[dts0.getNumColumns()+i] = DataType.getMissingCell();
					}
					//cells[numOutCols - 1] = dc;
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

	private DataTableSpec getSpec(DataTableSpec inSpec0, DataTableSpec inSpec1) {
		DataColumnSpec[] outSpec = new DataColumnSpec[inSpec0.getNumColumns() + inSpec1.getNumColumns()];		
		for (int i=0;i<inSpec0.getNumColumns();i++) {
			DataColumnSpec inSpecCols = inSpec0.getColumnSpec(i);
			outSpec[i] = new DataColumnSpecCreator(inSpecCols.getName(), inSpecCols.getType()).createSpec();
		}
		for (int i=0;i<inSpec1.getNumColumns();i++) {
			DataColumnSpec inSpecCols = inSpec1.getColumnSpec(i);
			outSpec[inSpec0.getNumColumns()+i] = new DataColumnSpecCreator(inSpecCols.getName(), inSpecCols.getType()).createSpec();
		}
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
		return new DataTableSpec[] {getSpec(inSpecs[0],inSpecs[1])};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	m_colSel.saveSettingsTo(settings);
    	m_colSelLims.saveSettingsTo(settings);
    	m_bvlSample.saveSettingsTo(settings);
    	m_limsSample.saveSettingsTo(settings);
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
    	if (settings.containsKey(BVL_SAMPLE)) m_bvlSample.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_SAMPLE)) m_limsSample.loadSettingsFrom(settings);
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
    	if (settings.containsKey(BVL_SAMPLE)) m_bvlSample.validateSettings(settings);
    	if (settings.containsKey(LIMS_SAMPLE)) m_limsSample.validateSettings(settings);
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


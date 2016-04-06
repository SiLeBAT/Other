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
    
	static final String BVL_PROBENNR = "selCol";
	static final String LIMS_KUNDENPROBENNR = "selColLims";
	static final String BVL_SAMPLE = "bvlsample";
	static final String LIMS_SAMPLE = "limssample";
	static final String BVL_MATRIX_CODE = "bvlmatrixcode";
	static final String LIMS_MATRIX_CODE = "limsmatrixcode";
	static final String WILD = "wild";
	static final String NUMBERSONLY = "numbersonly";
	
	private final SettingsModelString m_bvlProbenNr = new SettingsModelString(BVL_PROBENNR, "none");
	private final SettingsModelString m_limsKundenProbenNr = new SettingsModelString(LIMS_KUNDENPROBENNR, "none");
	private final SettingsModelString m_bvlSample = new SettingsModelString(BVL_SAMPLE, "none");
	private final SettingsModelString m_limsSample = new SettingsModelString(LIMS_SAMPLE, "none");
	private final SettingsModelString m_bvlMatrixCode = new SettingsModelString(BVL_MATRIX_CODE, "");
	private final SettingsModelString m_limsMatrixCode = new SettingsModelString(LIMS_MATRIX_CODE, "");
	private final SettingsModelBoolean m_wildSearch = new SettingsModelBoolean(WILD, false);
	private final SettingsModelBoolean m_onlyNumbers = new SettingsModelBoolean(NUMBERSONLY, false);
	
	/**
     * Constructor for the node model.
     */
    protected MyJavaMatcherNodeModel() {
        super(2, 1);
    }

    private int getCol(DataTableSpec dts, String colname) {
		int theCol = -1;
    	for (int i=0;i<dts.getNumColumns();i++) {
    		if (dts.getColumnNames()[i].equals(colname)) {
    			theCol = i;
    			break;
    		}
    	}
    	return theCol;
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
    	
		int theCol0 = getCol(dts0, m_bvlProbenNr.getStringValue()); // "PN_conTP"
		int theCol1 = getCol(dts1, m_limsKundenProbenNr.getStringValue()); // "KundenProbenr"
		int theCol2 = getCol(dts0, m_bvlSample.getStringValue()); // "PARAMETER_TEXT1"
		int theCol3 = getCol(dts1, m_limsSample.getStringValue()); // "Ergebnis"
		int col_BvlAdvCode = getCol(dts0, m_bvlMatrixCode.getStringValue()); // "ZERL_MATRIX"
		int col_LimsAdvCode = getCol(dts1, m_limsMatrixCode.getStringValue()); // "Matrix-A-Code"

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
        			// 1: check KundenprobenNr
            		String queryStr = ((StringCell) dc).getStringValue();
            		queryStr = queryStr.replace("%", ".*");
            		if (m_wildSearch.getBooleanValue()) queryStr = ".*" + queryStr + ".*";
            		for (String limsStr : limsMap.keySet()) {
    					if (limsStr.matches(queryStr)) {
    						List<DataRow> bestScore = new ArrayList<>();
    						double topScore = 0;
    						for (DataRow row1 : limsMap.get(limsStr)) {
        						// 2: check Matrix-ADV-Code
    							boolean advCodeOK = true;
        						if (col_BvlAdvCode >= 0 && col_LimsAdvCode >= 0) {
        							DataCell bvlSample = row0.getCell(col_BvlAdvCode);
        							DataCell limsSample = row1.getCell(col_LimsAdvCode);
        							if (!bvlSample.isMissing() && !limsSample.isMissing()) {
            							String bvl = ((StringCell) bvlSample).getStringValue();
            							String lims = ((StringCell) limsSample).getStringValue();
                						if (!bvl.equals(lims)) {
                							advCodeOK = false;
                						}
        							}
        						}
        						if (advCodeOK) {
            						// 3: check best Score for SerovarSimilarity
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
    	m_bvlProbenNr.saveSettingsTo(settings);
    	m_limsKundenProbenNr.saveSettingsTo(settings);
    	m_bvlSample.saveSettingsTo(settings);
    	m_limsSample.saveSettingsTo(settings);
    	m_bvlMatrixCode.saveSettingsTo(settings);
    	m_limsMatrixCode.saveSettingsTo(settings);
    	m_wildSearch.saveSettingsTo(settings);
    	m_onlyNumbers.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_bvlProbenNr.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_KUNDENPROBENNR)) m_limsKundenProbenNr.loadSettingsFrom(settings);
    	if (settings.containsKey(BVL_SAMPLE)) m_bvlSample.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_SAMPLE)) m_limsSample.loadSettingsFrom(settings);
    	if (settings.containsKey(BVL_MATRIX_CODE)) m_bvlMatrixCode.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_MATRIX_CODE)) m_limsMatrixCode.loadSettingsFrom(settings);
    	if (settings.containsKey(WILD)) m_wildSearch.loadSettingsFrom(settings);
    	if (settings.containsKey(NUMBERSONLY)) m_onlyNumbers.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_bvlProbenNr.validateSettings(settings);
    	if (settings.containsKey(LIMS_KUNDENPROBENNR)) m_limsKundenProbenNr.validateSettings(settings);
    	if (settings.containsKey(BVL_SAMPLE)) m_bvlSample.validateSettings(settings);
    	if (settings.containsKey(LIMS_SAMPLE)) m_limsSample.validateSettings(settings);
    	if (settings.containsKey(BVL_MATRIX_CODE)) m_bvlMatrixCode.validateSettings(settings);
    	if (settings.containsKey(LIMS_MATRIX_CODE)) m_limsMatrixCode.validateSettings(settings);
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


package de.bund.bfr.knime.aaw.lims;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
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
 * This is the model implementation of MyJavaJoiner.
 * 
 *
 * @author BfR
 */
public class MyJavaMatcherNodeModel extends NodeModel {
    
	static final String BVL_PROBENNR = "selCol";
	static final String BVL_TEILPROBENNR = "bvlteilprobennummer";
	static final String LIMS_KUNDENPROBENNR = "selColLims";
	static final String LIMS_AVV = "limsavv";
	static final String BVL_SAMPLE = "bvlsample";
	static final String LIMS_SAMPLE = "limssample";
	static final String LIMS_SAMPLE_RESULT = "limssampleresult";
	static final String LIMS_SAMPLE_STATUS = "limssamplestatus";
	static final String BVL_MATRIX_CODE = "bvlmatrixcode";
	static final String LIMS_MATRIX_CODE = "limsmatrixcode";
	static final String BVL_SAMPLING_DATE = "bvlsamplingdate";
	static final String LIMS_SAMPLING_DATE = "limssamplingdate";
	static final String LIMS_PROJECT_NAME = "limsprojectname";
	static final String BVL_SAMPLING_ORT = "bvlsamplingort";
	static final String BVL_BETRIEBSART = "bvlbetriebsart";
	static final String LIMS_SAMPLING_ORT = "limssamplingort";
	static final String LIMS_BETRIEBSART = "limsbetriebsart";
	
	private final SettingsModelString m_bvlProbenNr = new SettingsModelString(BVL_PROBENNR, "");
	private final SettingsModelString m_bvlTeilProbenNr = new SettingsModelString(BVL_TEILPROBENNR, "");
	private final SettingsModelString m_limsKundenProbenNr = new SettingsModelString(LIMS_KUNDENPROBENNR, "");
	private final SettingsModelString m_limsAVV = new SettingsModelString(LIMS_AVV, "");
	private final SettingsModelString m_bvlSample = new SettingsModelString(BVL_SAMPLE, "");
	private final SettingsModelString m_limsSample = new SettingsModelString(LIMS_SAMPLE, "");
	private final SettingsModelString m_limsSampleResult = new SettingsModelString(LIMS_SAMPLE_RESULT, "");
	private final SettingsModelString m_limsSampleStatus = new SettingsModelString(LIMS_SAMPLE_STATUS, "");
	private final SettingsModelString m_bvlMatrixCode = new SettingsModelString(BVL_MATRIX_CODE, "");
	private final SettingsModelString m_limsMatrixCode = new SettingsModelString(LIMS_MATRIX_CODE, "");
	private final SettingsModelString m_bvlSamplingDate = new SettingsModelString(BVL_SAMPLING_DATE, "");
	private final SettingsModelString m_limsSamplingDate = new SettingsModelString(LIMS_SAMPLING_DATE, "");
	private final SettingsModelString m_limsProjectName = new SettingsModelString(LIMS_PROJECT_NAME, "");
	private final SettingsModelString m_limsBetriebsart = new SettingsModelString(LIMS_BETRIEBSART, "");
	private final SettingsModelString m_limsSamplingOrt = new SettingsModelString(LIMS_SAMPLING_ORT, "");
	private final SettingsModelString m_bvlBetriebsart = new SettingsModelString(BVL_BETRIEBSART, "");
	private final SettingsModelString m_bvlSamplingOrt = new SettingsModelString(BVL_SAMPLING_ORT, "");
	

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
    	boolean doAll = true;
    	DataTableSpec dts0 = inData[0].getSpec();
    	DataTableSpec dts1 = inData[1].getSpec();
    	
		int col_BvlProbenNr = getCol(dts0, m_bvlProbenNr.getStringValue()); // "PROBEN_NR"
		int col_BvlTeilProbenNr = getCol(dts0, m_bvlTeilProbenNr.getStringValue()); // "PROBEN_NR"m_bvlTeilProbenNr
		int col_limsKundenNr = getCol(dts1, m_limsKundenProbenNr.getStringValue()); // "KundenProbenr"
		int col_limsAVV = getCol(dts1, m_limsAVV.getStringValue()); // "AVV"
		int col_BvlVorbefund = getCol(dts0, m_bvlSample.getStringValue()); // "PARAMETER_TEXT1"
		int col_LimsVorbefund = getCol(dts1, m_limsSample.getStringValue()); // "Vorbefund"
		int col_limsResult = getCol(dts1, m_limsSampleResult.getStringValue()); // "Ergebnis"
		int col_limsStatus = getCol(dts1, m_limsSampleStatus.getStringValue()); // "Status"
		int col_BvlAdvCode = getCol(dts0, m_bvlMatrixCode.getStringValue()); // "ZERL_MATRIX"
		int col_LimsAdvCode = getCol(dts1, m_limsMatrixCode.getStringValue()); // "Matrix-A-Code"
		int col_BvlSamplingDate = getCol(dts0, m_bvlSamplingDate.getStringValue()); // "PROBENAHME_DAT"
		int col_LimsSamplingDate = getCol(dts1, m_limsSamplingDate.getStringValue()); // "Probenahme"
		int col_LimsProjectName = getCol(dts1, m_limsProjectName.getStringValue()); // "Projectname"
		int col_BvlSamplingOrt = getCol(dts0, m_bvlSamplingOrt.getStringValue()); // ""
		int col_BvlBetriebsart = getCol(dts0, m_bvlBetriebsart.getStringValue()); // ""
		int col_LimsSamplingOrt = getCol(dts1, m_limsSamplingOrt.getStringValue()); // ""
		int col_LimsBetriebsart = getCol(dts1, m_limsBetriebsart.getStringValue()); // ""

    	Map<String, List<MyLimsDs>> limsMap = new LinkedHashMap<>();
		for (DataRow row1 : inData[1]) {
			MyLimsDs mld = new MyLimsDs(col_limsKundenNr, col_limsAVV, col_LimsVorbefund, col_limsResult, col_limsStatus, col_LimsAdvCode, col_LimsSamplingDate, col_LimsProjectName, col_LimsSamplingOrt, col_LimsBetriebsart);
			mld.setDr(row1);
			if (limsMap.containsKey(mld.getKey())) {
				//System.err.println("LIMS: " + mld.getKey() + " already existing...");
			}
			else {
				limsMap.put(mld.getKey(), new ArrayList<MyLimsDs>());
			}
			List<MyLimsDs> l = limsMap.get(mld.getKey());
			l.add(mld);
		}
    	Map<String, MyBvlDs> bvlMap = new LinkedHashMap<>();
		for (DataRow row0 : inData[0]) {
			MyBvlDs mbd = new MyBvlDs(col_BvlProbenNr, col_BvlTeilProbenNr, col_BvlVorbefund, col_BvlAdvCode, col_BvlSamplingDate, col_BvlSamplingOrt, col_BvlBetriebsart);
			mbd.setDr(row0);
			if (bvlMap.containsKey(mbd.getKey())) {
				System.err.println("BVL: " + mbd.getKey() + " already existing...");
			}
			else {
				bvlMap.put(mbd.getKey(), mbd);
			}
		}
		
		
		BufferedDataContainer buf = exec.createDataContainer(getSpec(dts0,dts1));
		double percent;
    	int rowLfd = 0;
    	//long ttt = System.currentTimeMillis();
		for (String key : bvlMap.keySet()) {			
			boolean success = false;
			MyBvlDs mbd = bvlMap.get(key);
			String bdo = mbd.getPROBEN_NR().replaceAll("[^0-9]","");
			if (mbd.getPROBEN_NR().equals("N15007019-002")) {
				System.err.print("");
			}
			for (String limsKey : limsMap.keySet()) {
				List<MyLimsDs> mldl = limsMap.get(limsKey);
				String limsKPN = mldl.get(0).getKundenProbenr();
				String limsAVV = mldl.get(0).getAVV();
				if (limsKPN != null && limsKPN.equals("N15007019/2")) {
					System.err.print("");
				}
				double score = StringSimilarity.diceCoefficientOptimized(mbd.getPROBEN_NR(), limsKPN);			
				double scoreAVV = StringSimilarity.diceCoefficientOptimized(mbd.getPROBEN_NR(), limsAVV);
				double maxS = Math.max(score, scoreAVV);
				if (score > 0 && scoreAVV > 0 && score != scoreAVV) maxS = maxS - 0.01;
				if (maxS < 0.7) { // Spezialfälle
					 // z.B. aus Campy 2015, z.B. 50201151567 -> 1567/1
					if (mbd.getPROBEN_NR().startsWith("5020115") && limsKPN.startsWith(mbd.getPROBEN_NR().substring(mbd.getPROBEN_NR().length() - 4) + "/" + mbd.getTEILPROBEN_NR())) maxS = 1;
					// z.B. MRSA 1551211UKF  513
					if (mbd.getPROBEN_NR().startsWith("1551211UKF") && limsKPN.startsWith("15UKF" + mbd.getPROBEN_NR().substring(mbd.getPROBEN_NR().length() - 3) + "/")) maxS = 1;
					// 101201515559 Diagnostik
					// 2015OWL016581 Diagnostik
					int ind = limsKPN.indexOf("(");
					if (ind >= 0) {
						score = StringSimilarity.diceCoefficientOptimized(mbd.getPROBEN_NR(), limsKPN.substring(0, ind));
						if (score > maxS) maxS = score;
					}
				}
				int minLength = 3;
				boolean contains = false;
				if (limsAVV != null && mbd.getPROBEN_NR().length() >= minLength && limsAVV.length() >= minLength) contains = limsAVV.indexOf(mbd.getPROBEN_NR()) >= 0 || mbd.getPROBEN_NR().indexOf(limsAVV) >= 0;
				if (!contains && mbd.getPROBEN_NR().length() >= minLength && limsKPN.length() >= minLength) contains = limsKPN.indexOf(mbd.getPROBEN_NR()) >= 0 || mbd.getPROBEN_NR().indexOf(limsKPN) >= 0;
				boolean numberOnlyContains = false;
				if (limsAVV != null) {
					String ldoAVV = limsAVV.replaceAll("[^0-9]","");	
					if (ldoAVV.length() >= minLength) numberOnlyContains = ldoAVV.indexOf(bdo) >= 0 || bdo.indexOf(ldoAVV) >= 0;
				}
				if (!numberOnlyContains) {
					String ldo = limsKPN.replaceAll("[^0-9]","");
					if (ldo.length() >= minLength) numberOnlyContains = ldo.indexOf(bdo) >= 0 || bdo.indexOf(ldo) >= 0;
				}
				
				if (maxS >= 0.7 || contains || numberOnlyContains) {
					if (!contains && !numberOnlyContains) maxS = maxS / 3;
					mbd.addStringComparison(mldl, maxS);
				}
			}
			percent = ((double)rowLfd)/bvlMap.size();
			//if (rowLfd % 100 == 0) System.err.println(rowLfd + "\t" + percent + "\t" + (System.currentTimeMillis()-ttt));
			//System.out.println(mbd.getPROBEN_NR());
			//mbd.printMap(5);
			Map<List<MyLimsDs>, Double> sm = mbd.getSortedMap();
			double topScore = 0;
			List<MyLimsDs> bestScore = new ArrayList<>();
			for (Map.Entry<List<MyLimsDs>, Double> entry : sm.entrySet()) {
				double pnScore = entry.getValue();
				for (MyLimsDs mld : entry.getKey())  {
					
					double matchQuality = pnScore;
					MyBLTResults mblt = null;
					//if (pnScore >= 0.99 || contains || numberOnlyContains) {
						//matchQuality = matchQuality * 0.3;
						/*
						mblt = mld.setMblt(mld, mbd);
						matchQuality = mblt.getBetriebsartMatch() * 0.1 + mblt.getProbenahmeortMatch() * 0.1 + mblt.getVorbefundScore() * 0.1 +
								(mblt.getV_adv() != null && mblt.getV_adv() ? 0.1 : 0) + (mblt.getV_date() != null && mblt.getV_date() ? 0.1 : 0);
								*/
						if (mbd.getPROBEN_NR().equals("101201514787") && (mld.getKundenProbenr().equals("10-12015-14787"))) {
							System.err.print("");
						}
						if (matchQuality >= topScore) {
							if (mblt == null) mblt = mld.setMblt(mld, mbd);
							mblt.setV_pnScore(pnScore);							

							//if (mblt.getBetriebsartMatch() < 1) matchQuality = matchQuality * 0.5;
							//if (mblt.getProbenahmeortMatch() < 0.8) matchQuality = matchQuality * 0.5;
							
							matchQuality = matchQuality * mblt.getVorbefundScore();
							if (mblt.getV_date() == null) matchQuality = matchQuality * 0.8; else if (!mblt.getV_date()) matchQuality = matchQuality * 0.5; 
							if (mblt.getV_status() != null && !mblt.getV_status()) matchQuality = matchQuality * 0.7;
							if (mblt.getV_adv() == null) matchQuality = matchQuality * 0.75; else if (!mblt.getV_adv()) matchQuality = matchQuality * 0.5; 
							
							if (matchQuality >= topScore && mbd.getPROBEN_NR().startsWith("15TRB1054-001")) {
								System.err.println(matchQuality + "\t" + mbd.getPROBEN_NR() + "\t" + mbd.getTEILPROBEN_NR() + "\t" + mbd.getVORBEFUND() + "\t" + mld.getDr());
							}
							
							if (matchQuality == topScore) {
								bestScore.add(mld);
							}
							else if (matchQuality > topScore) {
								bestScore.clear();
								topScore = matchQuality;
								bestScore.add(mld);
							}
			    			success = true;
						}
					//}
					
				}
			}
			List<String> alreadyIn = new ArrayList<>();
			for (MyLimsDs mld : bestScore) {
				String d_result = mld.getErgebnis();
				if (d_result != null && !alreadyIn.contains(d_result)) {
					alreadyIn.add(d_result);
					addRow(dts0, mbd.getDr(), dts1, mld.getDr(), buf, RowKey.createRowKey(buf.size()), topScore, mld.getMblt(false));					
				}
			}
    		if (doAll && !success) addRow(dts0, mbd.getDr(), dts1, null, buf, RowKey.createRowKey(buf.size()), -1, null);
    		exec.setProgress(percent);
    		exec.checkCanceled();
    		rowLfd++;
		}
		exec.setProgress(1);
	
    	buf.close();
        return new BufferedDataTable[]{buf.getTable()};
    }
    private void addRow(DataTableSpec dts0, DataRow rowBvl, DataTableSpec dts1, DataRow rowLims, BufferedDataContainer buf, RowKey key, double matchQuality, MyBLTResults mblt) {
    	int numOutCols = dts0.getNumColumns() + dts1.getNumColumns() + 6;
		DataCell[] cells = new DataCell[numOutCols];
		for (int i=0;i<dts0.getNumColumns();i++) {
			cells[i] = rowBvl.getCell(i);
		}
		for (int i=0;i<dts1.getNumColumns();i++) {
			if (rowLims == null) cells[dts0.getNumColumns()+i] = DataType.getMissingCell();
			else cells[dts0.getNumColumns()+i] = rowLims.getCell(i);

		}
		cells[dts0.getNumColumns() + dts1.getNumColumns()] = new DoubleCell(matchQuality);
		cells[dts0.getNumColumns() + dts1.getNumColumns() + 1] = (mblt == null) ? DataType.getMissingCell() : new DoubleCell(mblt.getV_pnScore());
		cells[dts0.getNumColumns() + dts1.getNumColumns() + 2] = (mblt == null) ? DataType.getMissingCell() : (mblt.getV_status() == null ? new IntCell(0) : mblt.getV_status() ? new IntCell(1) : new IntCell(-1));
		cells[dts0.getNumColumns() + dts1.getNumColumns() + 3] = (mblt == null) ? DataType.getMissingCell() : (mblt.getV_date() == null ? new IntCell(0) : mblt.getV_date() ? new IntCell(1) : new IntCell(-1));
		cells[dts0.getNumColumns() + dts1.getNumColumns() + 4] = (mblt == null) ? DataType.getMissingCell() : (mblt.getV_adv() == null ? new IntCell(0) : mblt.getV_adv() ? new IntCell(1) : new IntCell(-1));
		cells[dts0.getNumColumns() + dts1.getNumColumns() + 5] = (mblt == null) ? DataType.getMissingCell() : new DoubleCell(mblt.getVorbefundScore());

		DataRow outputRow = new DefaultRow(key, cells);
		buf.addRowToTable(outputRow);
    }

	private DataTableSpec getSpec(DataTableSpec inSpec0, DataTableSpec inSpec1) {
		DataColumnSpec[] outSpec = new DataColumnSpec[inSpec0.getNumColumns() + inSpec1.getNumColumns() + 6];		
		for (int i=0;i<inSpec0.getNumColumns();i++) {
			DataColumnSpec inSpecCol = inSpec0.getColumnSpec(i);
			outSpec[i] = new DataColumnSpecCreator(inSpecCol.getName(), inSpecCol.getType()).createSpec();
		}
		for (int i=0;i<inSpec1.getNumColumns();i++) {
			DataColumnSpec inSpecCol = inSpec1.getColumnSpec(i);
			outSpec[inSpec0.getNumColumns()+i] = new DataColumnSpecCreator(inSpecCol.getName(), inSpecCol.getType()).createSpec();
		}
		outSpec[inSpec0.getNumColumns() + inSpec1.getNumColumns()] = new DataColumnSpecCreator("Match Quality", DoubleCell.TYPE).createSpec();
		outSpec[inSpec0.getNumColumns() + inSpec1.getNumColumns() + 1] = new DataColumnSpecCreator("v_PN_simi", DoubleCell.TYPE).createSpec();
		outSpec[inSpec0.getNumColumns() + inSpec1.getNumColumns() + 2] = new DataColumnSpecCreator("v_Status", IntCell.TYPE).createSpec();
		outSpec[inSpec0.getNumColumns() + inSpec1.getNumColumns() + 3] = new DataColumnSpecCreator("v_Date", IntCell.TYPE).createSpec();
		outSpec[inSpec0.getNumColumns() + inSpec1.getNumColumns() + 4] = new DataColumnSpecCreator("v_Adv", IntCell.TYPE).createSpec();
		outSpec[inSpec0.getNumColumns() + inSpec1.getNumColumns() + 5] = new DataColumnSpecCreator("v_Befund", DoubleCell.TYPE).createSpec();

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
    	m_bvlTeilProbenNr.saveSettingsTo(settings);
    	m_limsKundenProbenNr.saveSettingsTo(settings);
    	m_limsAVV.saveSettingsTo(settings);
    	m_bvlSample.saveSettingsTo(settings);
    	m_limsSample.saveSettingsTo(settings);
    	m_limsSampleResult.saveSettingsTo(settings);
    	m_limsSampleStatus.saveSettingsTo(settings);
    	m_bvlMatrixCode.saveSettingsTo(settings);
    	m_limsMatrixCode.saveSettingsTo(settings);
    	m_bvlSamplingDate.saveSettingsTo(settings);
    	m_limsSamplingDate.saveSettingsTo(settings);
    	m_limsProjectName.saveSettingsTo(settings);
    	m_limsBetriebsart.saveSettingsTo(settings);
    	m_limsSamplingOrt.saveSettingsTo(settings);
    	m_bvlBetriebsart.saveSettingsTo(settings);
    	m_bvlSamplingOrt.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_bvlProbenNr.loadSettingsFrom(settings);
    	if (settings.containsKey(BVL_TEILPROBENNR)) m_bvlTeilProbenNr.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_KUNDENPROBENNR)) m_limsKundenProbenNr.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_AVV)) m_limsAVV.loadSettingsFrom(settings);
    	if (settings.containsKey(BVL_SAMPLE)) m_bvlSample.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_SAMPLE)) m_limsSample.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_SAMPLE_RESULT)) m_limsSampleResult.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_SAMPLE_STATUS)) m_limsSampleStatus.loadSettingsFrom(settings);
    	if (settings.containsKey(BVL_MATRIX_CODE)) m_bvlMatrixCode.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_MATRIX_CODE)) m_limsMatrixCode.loadSettingsFrom(settings);
    	if (settings.containsKey(BVL_SAMPLING_DATE)) m_bvlSamplingDate.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_SAMPLING_DATE)) m_limsSamplingDate.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_PROJECT_NAME)) m_limsProjectName.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_BETRIEBSART)) m_limsBetriebsart.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_SAMPLING_ORT)) m_limsSamplingOrt.loadSettingsFrom(settings);
    	if (settings.containsKey(BVL_BETRIEBSART)) m_bvlBetriebsart.loadSettingsFrom(settings);
    	if (settings.containsKey(BVL_SAMPLING_ORT)) m_bvlSamplingOrt.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_bvlProbenNr.validateSettings(settings);
    	if (settings.containsKey(BVL_TEILPROBENNR)) m_bvlTeilProbenNr.validateSettings(settings);
    	if (settings.containsKey(LIMS_KUNDENPROBENNR)) m_limsKundenProbenNr.validateSettings(settings);
    	if (settings.containsKey(LIMS_AVV)) m_limsAVV.validateSettings(settings);
    	if (settings.containsKey(BVL_SAMPLE)) m_bvlSample.validateSettings(settings);
    	if (settings.containsKey(LIMS_SAMPLE)) m_limsSample.validateSettings(settings);
    	if (settings.containsKey(LIMS_SAMPLE_RESULT)) m_limsSampleResult.validateSettings(settings);
    	if (settings.containsKey(LIMS_SAMPLE_STATUS)) m_limsSampleStatus.validateSettings(settings);
    	if (settings.containsKey(BVL_MATRIX_CODE)) m_bvlMatrixCode.validateSettings(settings);
    	if (settings.containsKey(LIMS_MATRIX_CODE)) m_limsMatrixCode.validateSettings(settings);
    	if (settings.containsKey(BVL_SAMPLING_DATE)) m_bvlSamplingDate.validateSettings(settings);
    	if (settings.containsKey(LIMS_SAMPLING_DATE)) m_limsSamplingDate.validateSettings(settings);
    	if (settings.containsKey(LIMS_PROJECT_NAME)) m_limsProjectName.validateSettings(settings);
    	if (settings.containsKey(LIMS_BETRIEBSART)) m_limsBetriebsart.validateSettings(settings);
    	if (settings.containsKey(LIMS_SAMPLING_ORT)) m_limsSamplingOrt.validateSettings(settings);
    	if (settings.containsKey(BVL_BETRIEBSART)) m_bvlBetriebsart.validateSettings(settings);
    	if (settings.containsKey(BVL_SAMPLING_ORT)) m_bvlSamplingOrt.validateSettings(settings);
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


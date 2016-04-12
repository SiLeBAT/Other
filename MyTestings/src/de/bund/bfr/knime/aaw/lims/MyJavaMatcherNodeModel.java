package de.bund.bfr.knime.aaw.lims;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.knime.core.data.def.StringCell;
import org.knime.core.data.date.DateAndTimeCell;
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
    
	private final static int EXACT = 1;
	private final static int DIGITS_ONLY = 2;
	private final static int WILD_LEFT = 3;
	private final static int WILD_RIGHT = 4;
	private final static int WILD_BOTH = 5;
	private final static int ALLOW_NULL = 6;
	private final static int IGNORE = 7;
	
	static final String BVL_PROBENNR = "selCol";
	static final String LIMS_KUNDENPROBENNR = "selColLims";
	static final String BVL_SAMPLE = "bvlsample";
	static final String LIMS_SAMPLE = "limssample";
	static final String LIMS_SAMPLE_RESULT = "limssampleresult";
	static final String LIMS_SAMPLE_STATUS = "limssamplestatus";
	static final String BVL_MATRIX_CODE = "bvlmatrixcode";
	static final String LIMS_MATRIX_CODE = "limsmatrixcode";
	static final String BVL_SAMPLING_DATE = "bvlsamplingdate";
	static final String LIMS_SAMPLING_DATE = "limssamplingdate";
	//static final String WILD = "wild";
	//static final String LOOSE = "loose";
	//static final String OPTIONAL = "optional";
	//static final String DEGREE_OF_TRUST = "degreeoftrust";
	//static final String NUMBERSONLY = "numbersonly";
	
	private final SettingsModelString m_bvlProbenNr = new SettingsModelString(BVL_PROBENNR, "none");
	private final SettingsModelString m_limsKundenProbenNr = new SettingsModelString(LIMS_KUNDENPROBENNR, "none");
	private final SettingsModelString m_bvlSample = new SettingsModelString(BVL_SAMPLE, "none");
	private final SettingsModelString m_limsSample = new SettingsModelString(LIMS_SAMPLE, "none");
	private final SettingsModelString m_limsSampleResult = new SettingsModelString(LIMS_SAMPLE_RESULT, "");
	private final SettingsModelString m_limsSampleStatus = new SettingsModelString(LIMS_SAMPLE_STATUS, "");
	private final SettingsModelString m_bvlMatrixCode = new SettingsModelString(BVL_MATRIX_CODE, "");
	private final SettingsModelString m_limsMatrixCode = new SettingsModelString(LIMS_MATRIX_CODE, "");
	private final SettingsModelString m_bvlSamplingDate = new SettingsModelString(BVL_SAMPLING_DATE, "");
	private final SettingsModelString m_limsSamplingDate = new SettingsModelString(LIMS_SAMPLING_DATE, "");
	//private final SettingsModelBoolean m_wildSearch = new SettingsModelBoolean(WILD, false);
	//private final SettingsModelBoolean m_looseSearch = new SettingsModelBoolean(LOOSE, false);
	//private final SettingsModelBoolean m_onlyNumbers = new SettingsModelBoolean(NUMBERSONLY, false);
	//private final SettingsModelBoolean m_optional = new SettingsModelBoolean(OPTIONAL, false);
	//private final SettingsModelInteger m_degreeOfTrust = new SettingsModelInteger(DEGREE_OF_TRUST, 0);
	
	private final SimpleDateFormat bvlFormatter = new SimpleDateFormat("dd-MMM-yy"); //"17-Feb-15";
	private final long ONE_DAY = 24*60*60*1000;

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
    // IGNORE, EXACT, ALLOW_NULL
    private boolean getAdvCodeOk(int qMatrix, int col_BvlAdvCode, int col_LimsAdvCode, DataRow rowBvl, DataRow rowLims) {
    	if (qMatrix != EXACT && qMatrix != ALLOW_NULL) return true; // IGNORE
		boolean advCodeOK = (qMatrix == EXACT ? false : true); // else ALLOW_NULL
		if (col_BvlAdvCode >= 0 && col_LimsAdvCode >= 0) {
			DataCell bvlSample = rowBvl.getCell(col_BvlAdvCode);
			DataCell limsSample = rowLims.getCell(col_LimsAdvCode);
			if (!bvlSample.isMissing() && !limsSample.isMissing()) {
				String bvl = ((StringCell) bvlSample).getStringValue();
				String lims = ((StringCell) limsSample).getStringValue();
				boolean criterium = bvl.equals(lims);
				if (criterium == (qMatrix == EXACT ? true : false)) {
					advCodeOK = (qMatrix == EXACT ? true : false);
				}
			}
		}    	
		return advCodeOK;
    }
    // IGNORE, EXACT, ALLOW_NULL
    private boolean getSamplingDateOk(int qDate, int col_BvlSamplingDate, int col_LimsSamplingDate, DataRow rowBvl, DataRow rowLims) throws ParseException {
    	if (qDate != EXACT && qDate != ALLOW_NULL) return true; // IGNORE
		boolean samplingDateOK = (qDate == EXACT ? false : true); // else ALLOW_NULL
		if (col_BvlSamplingDate >= 0 && col_LimsSamplingDate >= 0) {
			DataCell bvlSample = rowBvl.getCell(col_BvlSamplingDate);
			DataCell limsSample = rowLims.getCell(col_LimsSamplingDate);
			if (!bvlSample.isMissing() && !limsSample.isMissing()) {
				String bvl = ((StringCell) bvlSample).getStringValue();
				long bvlMillis = bvlFormatter.parse(bvl).getTime();
				long limsMillis = ((DateAndTimeCell) limsSample).getUTCTimeInMillis();
				boolean criterium = limsMillis >= bvlMillis - ONE_DAY && bvlMillis <= limsMillis + ONE_DAY;
				if (criterium == (qDate == EXACT ? true : false)) {
					samplingDateOK = (qDate == EXACT ? true : false);
				}
			}
		}   
		return samplingDateOK;
    }
	// EXACT, DIGITS_ONLY, WILD_LEFT, WILD_RIGHT, WILD_BOTH
    private String manageString(String str, int todo) {
    	if (todo == EXACT) return str;
    	else if (todo == DIGITS_ONLY) return str.replaceAll("[^0-9]","");
    	else if (todo == WILD_LEFT) return ".*" + str;
    	else if (todo == WILD_RIGHT) return str + ".*";
    	else if (todo == WILD_BOTH) return ".*" + str + ".*";
    	return str;
    }
    private boolean searchInLims(int qProbe, int qBvlProbeWild, int qDate, int qMatrix, boolean offerMoreMatches, int matchQuality, String bvlStr, Map<String, List<DataRow>> limsMap, int col_BvlAdvCode, int col_LimsAdvCode,
    		 int col_BvlVorbefund, int col_LimsVorbefund, int col_BvlSamplingDate, int col_LimsSamplingDate, int col_limsResult,
    		DataRow rowBvl, DataTableSpec dts0, DataTableSpec dts1, BufferedDataContainer buf) throws ParseException {
    	boolean success = false;
    	String c_bvl = manageString(bvlStr, qProbe);
    	c_bvl = manageString(c_bvl, qBvlProbeWild);
		double topScore = 0;
		Map<DataRow, String> bestScore = new LinkedHashMap<>();
		for (String limsStr : limsMap.keySet()) {
			String c_lims = manageString(limsStr, qProbe);
			if (c_lims.matches(c_bvl)) {
				for (DataRow rowLims : limsMap.get(limsStr)) {
					
					// 2: check Matrix-ADV-Code
					boolean advCodeOk = getAdvCodeOk(qMatrix, col_BvlAdvCode, col_LimsAdvCode, rowBvl, rowLims);
					// 3: check Sampling Date
					boolean samplingDateOk = getSamplingDateOk(qDate, col_BvlSamplingDate, col_LimsSamplingDate, rowBvl, rowLims);
					
					if (bvlStr.indexOf("1552211MK   166") >= 0) {
						System.err.print("");
					}
					
					
					if (advCodeOk && samplingDateOk) {
						// 4: check best Score for SerovarSimilarity
						double score = 0; 
						if (col_BvlVorbefund >= 0 && col_LimsVorbefund >= 0) {
							DataCell bvlSample = rowBvl.getCell(col_BvlVorbefund);
							DataCell limsSample = rowLims.getCell(col_LimsVorbefund);
							if (!bvlSample.isMissing() && !limsSample.isMissing()) {
    							String bvl = ((StringCell) bvlSample).getStringValue();
    							String lims = ((StringCell) limsSample).getStringValue();
        						//Hier sollten die ähnlichsten in Bezug auf SamplingResult genommen werden, falls es mehrere Kandidaten gibt!
    							score = StringSimilarity.diceCoefficientOptimized(bvl, lims);
							}
						}
						if (score == topScore) {
							bestScore.put(rowLims, limsStr);
						}
						else if (score > topScore) {
							bestScore.clear();
							topScore = score;
							bestScore.put(rowLims, limsStr);
						}
					}
				}    							
			}
		}
		Set<String> limsResults = new HashSet<>();
		if (bestScore.size() > 1) {
			System.out.println("\nBVL:\n" + rowBvl);
			System.out.println("LIMS:");
			for (DataRow rowLims : bestScore.keySet()) {
				System.out.println(rowLims);
			}
		}
		for (DataRow rowLims : bestScore.keySet()) {
			DataCell rc = col_limsResult >= 0 ? rowLims.getCell(col_limsResult) : null;
			String limsResult = rc != null && !rc.isMissing() ? ((StringCell) rowLims.getCell(col_limsResult)).getStringValue() : ""+rowLims;
			if (offerMoreMatches || !limsResults.contains(limsResult)) { // e.g. one BVL dataset can result in two limsResults. This is only allowed, if the results are different, e.g. c.coli and c.jejuni
				limsResults.add(limsResult);
    			addRow(dts0, rowBvl, dts1, rowLims, buf, RowKey.createRowKey(buf.size()), matchQuality, null);
    			String limsStr = bestScore.get(rowLims);
    			removeLimsStr(limsMap, limsStr, rowLims);
    			if (bestScore.size() > 1) System.out.println("LIMS_chosen:\n" + rowLims + "\n");
        		success = true;
			}
		}
		return success;
    }
    private void removeLimsStr(Map<String, List<DataRow>> limsMap, String limsStr, DataRow rowLims) {
    	List<DataRow> ldr = limsMap.get(limsStr);
		ldr.remove(rowLims);
		if (ldr.size() == 0) limsMap.remove(limsStr);    	
    }
    private Boolean checkDates(Long date1, Long date2) {
    	if (date1 == null || date2 == null) return null;
		boolean criterium = date1 >= date2 - ONE_DAY && date1 <= date2 + ONE_DAY;
		return criterium;
    }
    private Boolean checkAdv(String adv1, String adv2) {
    	if (adv1 == null || adv2 == null) return null;
    	return adv1.equals(adv2);
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
		int col_limsKundenNr = getCol(dts1, m_limsKundenProbenNr.getStringValue()); // "KundenProbenr"
		int col_BvlVorbefund = getCol(dts0, m_bvlSample.getStringValue()); // "PARAMETER_TEXT1"
		int col_LimsVorbefund = getCol(dts1, m_limsSample.getStringValue()); // "Vorbefund"
		int col_limsResult = getCol(dts1, m_limsSampleResult.getStringValue()); // "Ergebnis"
		int col_limsStatus = getCol(dts1, m_limsSampleStatus.getStringValue()); // "Status"
		int col_BvlAdvCode = getCol(dts0, m_bvlMatrixCode.getStringValue()); // "ZERL_MATRIX"
		int col_LimsAdvCode = getCol(dts1, m_limsMatrixCode.getStringValue()); // "Matrix-A-Code"
		int col_BvlSamplingDate = getCol(dts0, m_bvlSamplingDate.getStringValue()); // "PROBENAHME_DAT"
		int col_LimsSamplingDate = getCol(dts1, m_limsSamplingDate.getStringValue()); // "Probenahme"

    	Map<String, List<MyLimsDs>> limsMap = new LinkedHashMap<>();
		for (DataRow row1 : inData[1]) {
			MyLimsDs mld = new MyLimsDs(col_limsKundenNr, col_LimsVorbefund, col_limsResult, col_limsStatus, col_LimsAdvCode, col_LimsSamplingDate);
			mld.setDr(row1);
			if (limsMap.containsKey(mld.getKey())) {
				//System.err.println("LIMS: " + mld.getKey() + " already existing...");
			}
			else {
				limsMap.put(mld.getKey(), new ArrayList<>());
			}
			List<MyLimsDs> l = limsMap.get(mld.getKey());
			l.add(mld);
		}
    	Map<String, MyBvlDs> bvlMap = new LinkedHashMap<>();
		for (DataRow row0 : inData[0]) {
			MyBvlDs mbd = new MyBvlDs(col_BvlProbenNr, col_BvlVorbefund, col_BvlAdvCode, col_BvlSamplingDate);
			mbd.setDr(row0);
			if (bvlMap.containsKey(mbd.getKey())) {
				System.err.println("BVL: " + mbd.getKey() + " already existing...");
			}
			else {
				bvlMap.put(mbd.getKey(), mbd);
			}
		}
		
		
		BufferedDataContainer buf = exec.createDataContainer(getSpec(dts0,dts1));
    	int rowLfd = 0;
		for (String key : bvlMap.keySet()) {
			boolean success = false;
			MyBvlDs mbd = bvlMap.get(key);
			String bdo = mbd.getPROBEN_NR().replaceAll("[^0-9]","");
			if (mbd.getPROBEN_NR().indexOf("15LMT0498-01") >= 0) {
				System.err.print("");
			}
			for (String limsKey : limsMap.keySet()) {
				List<MyLimsDs> mldl = limsMap.get(limsKey);
				String limsKPN = mldl.get(0).getKundenProbenr();
				double score = StringSimilarity.diceCoefficientOptimized(mbd.getPROBEN_NR(), limsKPN);			
				mbd.addStringComparison(mldl, score);
			}
			//System.out.println(mbd.getPROBEN_NR());
			//mbd.printMap(5);
			Map<List<MyLimsDs>, Double> sm = mbd.getSortedMap();
			double topScore = 0;
			List<MyLimsDs> bestScore = new ArrayList<>();
			for (Map.Entry<List<MyLimsDs>, Double> entry : sm.entrySet()) {
				double matchQuality = entry.getValue();
				for (MyLimsDs mld : entry.getKey())  {
					boolean contains = mld.getKundenProbenr().indexOf(mbd.getPROBEN_NR()) >= 0 || mbd.getPROBEN_NR().indexOf(mld.getKundenProbenr()) >= 0;
					String ldo = mld.getKundenProbenr().replaceAll("[^0-9]","");
					boolean numberOnlyContains = ldo.indexOf(bdo) >= 0 || bdo.indexOf(ldo) >= 0;
					if (matchQuality < 1 && !contains && !numberOnlyContains) matchQuality = matchQuality * 0.5;
					if (matchQuality >= topScore) {
						MyBLTResults mblt = mld.getMblt(true);
						mblt.setV_pnScore(matchQuality);
						mblt.setV_status(mld.getStatus() == null ? null : mld.getStatus().equalsIgnoreCase("ev"));
						Boolean b_date = checkDates(mbd.getProbenahmeDate(), mld.getProbenahme());
						mblt.setV_date(b_date);
						Boolean b_adv = checkAdv(mbd.getZERL_MATRIX(), mld.getMatrixACode());		
						mblt.setV_adv(b_adv);
						Double d_befund = StringSimilarity.diceCoefficientOptimized(mbd.getVORBEFUND(), mld.getVorbefund());
						mblt.setVorbefundScore(d_befund);
						
						if (b_date == null) matchQuality = matchQuality * 0.75; else if (!b_date) matchQuality = 0; 
						if (mld.getStatus() != null && !mld.getStatus().toLowerCase().endsWith("v")) matchQuality = matchQuality * 0.7;
						if (b_adv == null) matchQuality = matchQuality * 0.5; else if (!b_adv) matchQuality = matchQuality * 0.5; 
						matchQuality = matchQuality * d_befund;
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
    		exec.setProgress(((double)rowLfd)/bvlMap.size());
    		exec.checkCanceled();
    		rowLfd++;
		}
		exec.setProgress(1);
	
    	rowLfd = 0;
    	if (col_BvlProbenNr >= 0 && col_limsKundenNr >= 0 && false) {
        	for (DataRow rowBvl : inData[0]) {
        		boolean success = false;
        		DataCell dc = rowBvl.getCell(col_BvlProbenNr);
        		if (!dc.isMissing()) {
        			// 1: check KundenprobenNr
            		String bvlStr = ((StringCell) dc).getStringValue();
            		//bvlStr = bvlStr.replace("%", ".*");
            		//if (m_wildSearch.getBooleanValue()) bvlStr = ".*" + bvlStr + ".*";  
            		// EXACT, DIGITS_ONLY
            		// WILD_LEFT, WILD_RIGHT, WILD_BOTH
            		
            		/*
            		int mq = 0;
            		mq++; if (!success) success = searchInLims(EXACT, EXACT, ALLOW_NULL, ALLOW_NULL, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		
            		mq++; if (!success) success = searchInLims(EXACT, WILD_RIGHT, EXACT, EXACT, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(EXACT, WILD_RIGHT, ALLOW_NULL, EXACT, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(EXACT, WILD_RIGHT, EXACT, ALLOW_NULL, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(EXACT, WILD_RIGHT, ALLOW_NULL, ALLOW_NULL, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(EXACT, WILD_RIGHT, IGNORE, EXACT, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(EXACT, WILD_RIGHT, EXACT, IGNORE, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, EXACT, EXACT, EXACT, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, EXACT, ALLOW_NULL, EXACT, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, EXACT, EXACT, ALLOW_NULL, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, EXACT, ALLOW_NULL, ALLOW_NULL, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, EXACT, IGNORE, EXACT, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, EXACT, EXACT, IGNORE, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, WILD_RIGHT, EXACT, EXACT, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, WILD_RIGHT, ALLOW_NULL, EXACT, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, WILD_RIGHT, EXACT, ALLOW_NULL, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, WILD_RIGHT, ALLOW_NULL, ALLOW_NULL, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, WILD_RIGHT, IGNORE, EXACT, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, WILD_RIGHT, EXACT, IGNORE, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, WILD_BOTH, EXACT, EXACT, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, WILD_BOTH, ALLOW_NULL, EXACT, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, WILD_BOTH, EXACT, ALLOW_NULL, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, WILD_BOTH, ALLOW_NULL, ALLOW_NULL, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, WILD_BOTH, IGNORE, EXACT, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		mq++; if (!success) success = searchInLims(DIGITS_ONLY, WILD_BOTH, EXACT, IGNORE, false, mq, bvlStr, limsMap, col_BvlAdvCode, col_LimsAdvCode, col_BvlVorbefund, col_LimsVorbefund, col_BvlSamplingDate, col_LimsSamplingDate, col_limsResult, rowBvl, dts0, dts1, buf);
            		
            		*/
        		}
        		if (doAll && !success) addRow(dts0, rowBvl, dts1, null, buf, RowKey.createRowKey(buf.size()), -1, null);
        		
        		exec.setProgress(((double)rowLfd)/inData[0].size());
        		rowLfd++;
        	}
    		exec.setProgress(1);
    	}
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
    	m_limsKundenProbenNr.saveSettingsTo(settings);
    	m_bvlSample.saveSettingsTo(settings);
    	m_limsSample.saveSettingsTo(settings);
    	m_limsSampleResult.saveSettingsTo(settings);
    	m_limsSampleStatus.saveSettingsTo(settings);
    	m_bvlMatrixCode.saveSettingsTo(settings);
    	m_limsMatrixCode.saveSettingsTo(settings);
    	m_bvlSamplingDate.saveSettingsTo(settings);
    	m_limsSamplingDate.saveSettingsTo(settings);
    	/*
    	m_wildSearch.saveSettingsTo(settings);
    	m_looseSearch.saveSettingsTo(settings);
    	m_onlyNumbers.saveSettingsTo(settings);
    	m_optional.saveSettingsTo(settings);
    	m_degreeOfTrust.saveSettingsTo(settings);
    	*/
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
    	if (settings.containsKey(LIMS_SAMPLE_RESULT)) m_limsSampleResult.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_SAMPLE_STATUS)) m_limsSampleStatus.loadSettingsFrom(settings);
    	if (settings.containsKey(BVL_MATRIX_CODE)) m_bvlMatrixCode.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_MATRIX_CODE)) m_limsMatrixCode.loadSettingsFrom(settings);
    	if (settings.containsKey(BVL_SAMPLING_DATE)) m_bvlSamplingDate.loadSettingsFrom(settings);
    	if (settings.containsKey(LIMS_SAMPLING_DATE)) m_limsSamplingDate.loadSettingsFrom(settings);
    	/*
    	if (settings.containsKey(WILD)) m_wildSearch.loadSettingsFrom(settings);
    	if (settings.containsKey(LOOSE)) m_looseSearch.loadSettingsFrom(settings);
    	if (settings.containsKey(NUMBERSONLY)) m_onlyNumbers.loadSettingsFrom(settings);
    	if (settings.containsKey(OPTIONAL)) m_optional.loadSettingsFrom(settings);
    	if (settings.containsKey(DEGREE_OF_TRUST)) m_degreeOfTrust.loadSettingsFrom(settings);
    	*/
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
    	if (settings.containsKey(LIMS_SAMPLE_RESULT)) m_limsSampleResult.validateSettings(settings);
    	if (settings.containsKey(LIMS_SAMPLE_STATUS)) m_limsSampleStatus.validateSettings(settings);
    	if (settings.containsKey(BVL_MATRIX_CODE)) m_bvlMatrixCode.validateSettings(settings);
    	if (settings.containsKey(LIMS_MATRIX_CODE)) m_limsMatrixCode.validateSettings(settings);
    	if (settings.containsKey(BVL_SAMPLING_DATE)) m_bvlSamplingDate.validateSettings(settings);
    	if (settings.containsKey(LIMS_SAMPLING_DATE)) m_limsSamplingDate.validateSettings(settings);
    	/*
    	if (settings.containsKey(WILD)) m_wildSearch.validateSettings(settings);
    	if (settings.containsKey(LOOSE)) m_looseSearch.validateSettings(settings);
    	if (settings.containsKey(NUMBERSONLY)) m_onlyNumbers.validateSettings(settings);
    	if (settings.containsKey(OPTIONAL)) m_optional.validateSettings(settings);
    	if (settings.containsKey(DEGREE_OF_TRUST)) m_degreeOfTrust.validateSettings(settings);
    	*/
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


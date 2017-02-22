package de.bund.bfr.knime.aaw.lims;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SimiMap extends HashMap<Object[], Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void addStringComparison(MyBvlDs mbd, MyLimsDs mld, double val) {
		Object[] oa = new Object[2];
		oa[0] = mbd;
		oa[1] = mld;
		this.put(oa, val);
	}
	public Map<Object[], Double> getSortedMap() {
		return sortByComparator();
	}
	public void printMap(Map<List<MyLimsDs>, Double> map, int top) {
		Map<List<MyLimsDs>, Double> sortedMap = map;
		int i=0;
		for (Map.Entry<List<MyLimsDs>, Double> entry : sortedMap.entrySet()) {
			System.out.println("[Key] : " + entry.getKey() + " [Value] : " + entry.getValue());
			i++;
			if (i >= top) break;
		}
	}
	private Map<Object[], Double> sortByComparator() {

		// Convert Map to List
		List<Map.Entry<Object[], Double>> list = new LinkedList<>(this.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Object[], Double>>() {
			public int compare(Map.Entry<Object[], Double> o1, Map.Entry<Object[], Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<Object[], Double> sortedMap = new LinkedHashMap<>();
		for (Iterator<Map.Entry<Object[], Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Object[], Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}


	private MyBLTResults mblt = null;
	
	public MyBLTResults getMblt(boolean refresh) {
		if (refresh || mblt == null) mblt = new MyBLTResults();
		return mblt;
	}
	public MyBLTResults setMblt(MyBvlDs mbd, MyLimsDs mld) {
		mblt = new MyBLTResults();
		mblt.setV_status(mld.getStatus() == null ? null : mld.getStatus().toLowerCase().indexOf("v") >= 0);
		Boolean b_date = checkDates(mbd.getProbenahmeDate(), mld.getProbenahme());
		mblt.setV_date(b_date);
		Boolean b_adv = checkAdv(mbd.getZERL_MATRIX(), mld.getMatrixACode());		
		mblt.setV_adv(b_adv);
		Double d_befund = StringSimilarity.diceCoefficientOptimized(mbd.getVORBEFUND(), mld.getVorbefund());
		if (mld.getProjectName() != null && mbd.getVORBEFUND() != null && mld.getProjectName().startsWith("Moni-ESBL-") && mbd.getVORBEFUND().indexOf("ESBL") >= 0) d_befund = 1.0;
		if (mld.getVorbefund() != null && mbd.getVORBEFUND() != null && mld.getVorbefund().equals("MRSA") && mbd.getVORBEFUND().indexOf("MRSA positiv") >= 0) d_befund = 1.0;
		mblt.setVorbefundScore(d_befund);
		double betriebsartMatch = StringSimilarity.diceCoefficientOptimized(mbd.getBetriebsart(), mld.getBetriebsart());
		double probenahmeortMatch = StringSimilarity.diceCoefficientOptimized(mbd.getProbenahmeOrt(), mld.getProbenahmeOrt());
		mblt.setBetriebsartMatch(betriebsartMatch);
		mblt.setProbenahmeortMatch(probenahmeortMatch);
		return mblt;
	}
	
	private final long ONE_DAY = 24*60*60*1000;
    private Boolean checkDates(Long date1, Long date2) {
    	if (date1 == null || date2 == null) return null;
		boolean criterium = date1 >= date2 - ONE_DAY && date1 <= date2 + ONE_DAY;
		return criterium;
    }
    private Boolean checkAdv(String adv1, String adv2) {
    	if (adv1 == null || adv2 == null) return null;
    	if (adv1.equals("899999") || adv2.equals("899999")) return null;
    	return adv1.equals(adv2);
    }
}

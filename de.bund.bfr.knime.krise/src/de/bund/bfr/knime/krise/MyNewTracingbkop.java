package de.bund.bfr.knime.krise;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.xstream.XStream;

public class MyNewTracingbkop {

	private HashMap<Integer, MyDeliverybkop> allDeliveries;
	private HashMap<Integer, Double> caseStations = null;
	private HashSet<Integer> ccStations = null;
	//private HashSet<Integer> ccDeliveries = null;
	private LinkedHashMap<Integer, HashSet<Integer>> sortedStations = null;
	private LinkedHashMap<Integer, HashSet<Integer>> sortedDeliveries = null;
	private double caseSum = 0;
	
	public MyNewTracingbkop(HashMap<Integer, MyDeliverybkop> allDeliveries, HashMap<Integer, Double> caseStations, HashSet<Integer> ccStations, double caseSum) {
		this.allDeliveries = allDeliveries;
		this.caseStations = caseStations;
		this.ccStations = ccStations;
		this.caseSum = caseSum;
	}
	
	
	public boolean isStationStart(int id) {
		boolean result = true;
		for (Integer key : allDeliveries.keySet()) {
			if (allDeliveries.get(key).getRecipientID() == id) {
				result = false;
				break;
			}
		}
		return result;
	}
	public boolean isStationEnd(int id) {
		boolean result = true;
		for (Integer key : allDeliveries.keySet()) {
			if (allDeliveries.get(key).getSupplierID() == id) {
				result = false;
				break;
			}
		}
		return result;
	}
	public Double getStationScore(int id) {
		if (sortedStations == null) getScores(true);
		if (caseSum > 0 && sortedStations.get(id) != null) {
			double sum = 0;
			for (Integer key : sortedStations.get(id)) {
				sum += caseStations.get(key);
			}
			return sum / caseSum;
		}
		else return -1.0;
	}
	public Double getDeliveryScore(int id) {
		if (sortedDeliveries == null) getScores(false);
		if (caseSum > 0 && sortedDeliveries.get(id) != null) {
			double sum = 0;
			for (Integer key : sortedDeliveries.get(id)) {
				sum += caseStations.get(key);
			}
			return sum / caseSum;
//			return ((double) sortedDeliveries.get(id).size()) / caseStations.size();
		}
		else return -1.0;
	}
	@SuppressWarnings("unchecked")
	public LinkedHashMap<Integer, HashSet<Integer>> getScores(boolean stations) {
		// getForwardStationsWithCases counts for each delivery. But: it might be the case that a station delivers into "different" directions (deliveries), and all of them have cases!!!
		// Therefore, we sum here based on the suppliers (supplierSum), not on the deliveries!!!
		HashMap<Integer, HashSet<Integer>> supplierSum = new HashMap<Integer, HashSet<Integer>>(); 
		HashMap<Integer, HashSet<Integer>> deliverySum = new HashMap<Integer, HashSet<Integer>>(); 
		for (MyDeliverybkop md : allDeliveries.values()) {
			if (supplierSum.containsKey(md.getSupplierID())) {
				HashSet<Integer> hi = (HashSet<Integer>) supplierSum.get(md.getSupplierID());
				for (Integer i : md.getForwardStationsWithCases()) {
					hi.add(i);
				}
				supplierSum.put(md.getSupplierID(), hi);
			}
			else {
				supplierSum.put(md.getSupplierID(), (HashSet<Integer>) md.getForwardStationsWithCases().clone());
			}

			deliverySum.put(md.getId(), (HashSet<Integer>) md.getForwardStationsWithCases().clone());
		}
		
		sortedStations = sortByValues(supplierSum);
		sortedDeliveries = sortByValues(deliverySum);		
		/*
        for (Integer key : sortedStations.keySet()) {
        	HashSet<Integer> hi = sortedStations.get(key);
            System.err.println("SupplierID: " + key + ":\t" + hi.size() + (caseStations != null ? " / " + caseStations.size() : "") + "\t" + hi.toString());
        }     
        for (Integer key : sortedDeliveries.keySet()) {
        	HashSet<Integer> hi = sortedDeliveries.get(key);
            System.err.println("DeliveryID: " + key + ":\t" + hi.size() + (caseStations != null ? " / " + caseStations.size() : "") + "\t" + hi.toString());
        }             
        */
        return stations ? sortedStations : sortedDeliveries;
	}
	private LinkedHashMap<Integer, HashSet<Integer>> sortByValues(HashMap<Integer, HashSet<Integer>> map){
        List<Map.Entry<Integer, HashSet<Integer>>> entries = new LinkedList<Map.Entry<Integer, HashSet<Integer>>>(map.entrySet());
      
        Collections.sort(entries, new Comparator<Map.Entry<Integer, HashSet<Integer>>>() {

            @Override
            public int compare(Entry<Integer, HashSet<Integer>> o1, Entry<Integer, HashSet<Integer>> o2) {
                return o2.getValue().size() - o1.getValue().size();
            }
        });
      
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        LinkedHashMap<Integer, HashSet<Integer>> sortedMap = new LinkedHashMap<Integer, HashSet<Integer>>();
      
        for(Map.Entry<Integer, HashSet<Integer>> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
      
        return sortedMap;
    }

	public static XStream getXStream() {
		XStream xstream = new XStream(); // new DomDriver()
		xstream.setClassLoader(MyNewTracingbkop.class.getClassLoader());
		//xstream.alias("mynewtracing", MyNewTracing.class);
		xstream.omitField(MyNewTracingbkop.class, "ccStations");
		xstream.omitField(MyNewTracingbkop.class, "sortedStations");
		xstream.omitField(MyNewTracingbkop.class, "sortedDeliveries");
		
		xstream.omitField(MyDeliverybkop.class, "caseStations");
		xstream.omitField(MyDeliverybkop.class, "ccStations");
		xstream.omitField(MyDeliverybkop.class, "forwardStationsWithCases");
		xstream.omitField(MyDeliverybkop.class, "backwardStationsWithCases");
		xstream.omitField(MyDeliverybkop.class, "alreadySeen");
		xstream.omitField(MyDeliverybkop.class, "allNext");
		xstream.omitField(MyDeliverybkop.class, "allPrevious");
		xstream.omitField(MyDeliverybkop.class, "allDeliveries");

		return xstream;
	}
	public void fillDeliveries() {
		for (MyDeliverybkop md : allDeliveries.values()) {
			md.setCaseStations(caseStations);
			md.fillPrevsNexts(allDeliveries);
		}
	}
	public void setCase(int stationID, double priority) {
		if (priority < 0) priority = 0;
		else if (priority > 1) priority = 1;
		if (caseStations.containsKey(stationID)) {
			if (priority == 0) caseStations.remove(stationID);
			caseSum = caseSum - caseStations.get(stationID) + priority;
			caseStations.put(stationID, priority);
		}
		else {
			caseSum = caseSum + priority;
			caseStations.put(stationID, priority);
		}
		sortedStations = null;
		sortedDeliveries = null;
	}
	public void setCrossContamination(int stationID, boolean possible) {
		if (ccStations == null) ccStations = new HashSet<Integer>();
		if (possible) ccStations.add(stationID);
		else if (ccStations.contains(stationID)) ccStations.remove(stationID);  
		sortedStations = null;
		sortedDeliveries = null;
	}
	/*
	public void setCrossContaminationDelivery(int deliveryID, boolean possible) {
		if (ccDeliveries == null) ccDeliveries = new HashSet<Integer>();
		if (possible) ccDeliveries.add(deliveryID);
		else if (ccDeliveries.contains(deliveryID)) ccDeliveries.remove(deliveryID);  
		sortedStations = null;
		sortedDeliveries = null;
	}
	*/
}

package de.bund.bfr.knime.krise;

import java.util.HashMap;
import java.util.HashSet;

public class MyDeliverybkop {

	private int id;
	private int supplierID, recipientID;
	private Long deliveryDateAsSeconds;
	private HashMap<Integer, Double> caseStations;
	private HashSet<Integer> ccStations;
	//private HashSet<Integer> ccDeliveries;
	private HashMap<Integer, MyDeliverybkop> allDeliveries;
	
	private HashSet<MyDeliverybkop> allNext;
	private HashSet<MyDeliverybkop> allPrevious;
	private HashSet<Integer> allNextIDs;
	private HashSet<Integer> allPreviousIDs;
	private HashSet<Integer> forwardStationsWithCases;
	private HashSet<Integer> backwardStationsWithCases;
	
	private HashSet<MyDeliverybkop> alreadySeen;
	
	public MyDeliverybkop(int id, int supplierID, int recipientID, HashMap<Integer, Double> caseStations, Long deliveryDateAsSeconds, HashSet<Integer> ccStations) {
		this.id = id;
		this.supplierID = supplierID;
		this.recipientID = recipientID;
		this.caseStations = caseStations;
		this.ccStations = ccStations;
		this.deliveryDateAsSeconds = deliveryDateAsSeconds;
		
		forwardStationsWithCases = null;
		backwardStationsWithCases = null;
		allNext = new HashSet<MyDeliverybkop>();
		allPrevious = new HashSet<MyDeliverybkop>();
		allNextIDs = new HashSet<Integer>();
		allPreviousIDs = new HashSet<Integer>();
	}
	public void setCaseStations(HashMap<Integer, Double> caseStations) {
		this.caseStations = caseStations;
	}
	public void fillPrevsNexts(HashMap<Integer, MyDeliverybkop> allDeliveries) {
		this.allDeliveries = allDeliveries;
		allNext = new HashSet<MyDeliverybkop>();
		allPrevious = new HashSet<MyDeliverybkop>();
		for (Integer id : allNextIDs) {
			allNext.add(allDeliveries.get(id));
		}
		for (Integer id : allPreviousIDs) {
			allPrevious.add(allDeliveries.get(id));
		}
	}
	
	public int getSupplierID() {
		return supplierID;
	}

	public int getRecipientID() {
		return recipientID;
	}
	public int getId() {
		return id;
	}
	public Long getDeliveryDateAsSeconds() {
		return deliveryDateAsSeconds;
	}

	public HashSet<MyDeliverybkop> getNexts() {
		return allNext;
	}
	public void addNext(MyDeliverybkop next) {
		allNext.add(next);
		allNextIDs.add(next.getId());
	}
	public HashSet<MyDeliverybkop> getPreviouss() {
		return allPrevious;
	}
	public void addPrevious(MyDeliverybkop previous) {
		allPrevious.add(previous);
		allPreviousIDs.add(previous.getId());
	}
	
	
	
	
	public HashSet<Integer> getForwardStationsWithCases() {
		if (forwardStationsWithCases == null) {
			forwardStationsWithCases = new HashSet<Integer>();
			alreadySeen = new HashSet<MyDeliverybkop>();
			searchFFCases(this, forwardStationsWithCases);
		}
		return forwardStationsWithCases;
	}
	public HashSet<Integer> getBackwardStationsWithCases() {
		if (backwardStationsWithCases == null) {
			backwardStationsWithCases = new HashSet<Integer>();
			alreadySeen = new HashSet<MyDeliverybkop>();
			searchFBCases(this, backwardStationsWithCases);
		}
		return backwardStationsWithCases;
	}
	private void searchFBCases(MyDeliverybkop md, HashSet<Integer> stemmingStationsWithCases) {
		if (md == null || alreadySeen.contains(md)) return;
		else alreadySeen.add(md);
		if (caseStations.containsKey(md.getSupplierID())) stemmingStationsWithCases.add(md.getSupplierID());
		HashSet<MyDeliverybkop> n = md.getPreviouss();
		for (MyDeliverybkop d : n) {
			searchFBCases(d, stemmingStationsWithCases);
		}
		// check individual cross contamination
		if (allDeliveries != null) {
			if (ccStations != null && ccStations.contains(md.getSupplierID())) {
				for (MyDeliverybkop d : allDeliveries.values()) {
					if (d.getRecipientID() == md.getSupplierID() &&
							(md.getDeliveryDateAsSeconds() == null || md.getDeliveryDateAsSeconds() == 0 ||
									d.getDeliveryDateAsSeconds() == null || d.getDeliveryDateAsSeconds() == 0 ||
									md.getDeliveryDateAsSeconds() >= d.getDeliveryDateAsSeconds())) {
						searchFFCases(d, stemmingStationsWithCases);
					}
				}
			}			
		}
	}
	private void searchFFCases(MyDeliverybkop md, HashSet<Integer> headingStationsWithCases) {
		if (md == null || alreadySeen.contains(md)) return;
		else alreadySeen.add(md);
		//System.err.println(md);
		if (caseStations.containsKey(md.getRecipientID())) headingStationsWithCases.add(md.getRecipientID());
		HashSet<MyDeliverybkop> n = md.getNexts();
		for (MyDeliverybkop d : n) {
			searchFFCases(d, headingStationsWithCases);
		}
		// check individual cross contamination
		if (allDeliveries != null) {
			if (ccStations != null && ccStations.contains(md.getRecipientID())) {
				for (MyDeliverybkop d : allDeliveries.values()) {
					if (d.getSupplierID() == md.getRecipientID() &&
							(md.getDeliveryDateAsSeconds() == null || md.getDeliveryDateAsSeconds() == 0 ||
									d.getDeliveryDateAsSeconds() == null || d.getDeliveryDateAsSeconds() == 0 ||
									md.getDeliveryDateAsSeconds() <= d.getDeliveryDateAsSeconds())) {
						searchFFCases(d, headingStationsWithCases);
					}
				}
			}			
		}
	}
}

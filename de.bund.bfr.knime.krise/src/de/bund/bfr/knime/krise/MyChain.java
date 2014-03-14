package de.bund.bfr.knime.krise;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class MyChain extends LinkedHashMap<Integer, Integer> { // lieferID, stationID

	/**
	 * 
	 */
	private static final long serialVersionUID = 5003703791257260934L;
	private Integer caseID = null;
	private Integer caseLieferID = null;
	private HashSet<Integer> objectsInChain = null;
	private int negativeIndex;
	
	public MyChain() { // key: lieferID, value: stationID (Empfänger)
		negativeIndex = 0;
	}
	
	public void putDeliverer(Integer stationID) {
		if (!this.containsValue(stationID)) {
			negativeIndex--;
			this.put(negativeIndex, stationID);			
		}
	}

	public Integer getCaseID() {
		return caseID;
	}
	public Integer getCaseLieferID() {
		return caseLieferID;
	}
	public Integer getPreviousStationID() {
		Integer prevID = null;
		Iterator<java.util.Map.Entry<Integer, Integer>> it = this.entrySet().iterator();
		if (it.hasNext()) it.next(); // caseID
		if (it.hasNext()) prevID = it.next().getValue();
		return prevID;
	}

	public void calculateInfos(MyRelations mr, int objectType) { // HashMap<Integer, List<Integer>> objectListFromLieferID, 
		// calc caseID
		if (this.entrySet().iterator().hasNext()) {
			caseID = this.entrySet().iterator().next().getValue();
			caseLieferID = this.entrySet().iterator().next().getKey();
		}
		
		// calc objectsInChain
		objectsInChain = new HashSet<Integer>();
    	for (Integer lieferID : this.keySet()) {
    		if (lieferID > 0) objectsInChain.add(mr.getObject(lieferID, objectType));
    		else if (objectType == MyRelations.STATION) objectsInChain.add(this.get(lieferID));
    		/*
    		try {
    			List<Integer> ol = objectListFromLieferID.get(lieferID);
    			checkTZ(ol, mr.getObject(lieferID, objectType));
    			for (Integer o : ol) {
    				objectsInChain.add(o);
    				//System.err.println(lieferID + "\t" + o);
    			}
    		}
    		catch (Exception e) {
    			System.err.println(lieferID + " not there");
    		}
    		*/
    	}
	}
	/*
	private void checkTZ(List<Integer> li, int oi) {
		if (li.size() != 1 || li.size() == 1 && li.get(0) != oi) {
			System.err.println("getObjectFromLieferID\t" + oi);
		}
	}
	*/

	public HashSet<Integer> getObjectsInChain() {
		return objectsInChain;
	}

    public List<Integer> calcCommonLieferObjects(List<Integer> oldGemeinsamObjects) {
    	List<Integer> newGemeinsamObjects = new ArrayList<Integer>();
		for (Integer oo : oldGemeinsamObjects) {
			if (objectsInChain.contains(oo)) {
				newGemeinsamObjects.add(oo);
			}
		}
    	return newGemeinsamObjects;
    }
}

package de.bund.bfr.knime.krise;

import java.util.HashMap;
import java.util.HashSet;

public class MyRealChain {

	private HashMap<Integer, HashSet<Integer>> nextLieferIDs;
	private HashMap<Integer, HashSet<Integer>> previousLieferIDs;
	
	public MyRealChain() {
		nextLieferIDs = new HashMap<Integer, HashSet<Integer>>();
		previousLieferIDs = new HashMap<Integer, HashSet<Integer>>();
	}
	
	public HashSet<Integer> getPrevious(int lieferID) {
		return previousLieferIDs.get(lieferID);
	}
	public HashSet<Integer> getNext(int lieferID) {
		return nextLieferIDs.get(lieferID);
	}
	
	public void addData(int fromLieferID, int toLieferID) {
		if (!nextLieferIDs.containsKey(fromLieferID)) nextLieferIDs.put(fromLieferID, new HashSet<Integer>());
		HashSet<Integer> next = nextLieferIDs.get(fromLieferID);
		next.add(toLieferID);
		if (!previousLieferIDs.containsKey(toLieferID)) previousLieferIDs.put(toLieferID, new HashSet<Integer>());
		HashSet<Integer> prev = previousLieferIDs.get(toLieferID);
		prev.add(fromLieferID);
	}
}

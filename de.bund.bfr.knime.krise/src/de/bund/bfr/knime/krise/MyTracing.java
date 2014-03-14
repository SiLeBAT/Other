package de.bund.bfr.knime.krise;

import java.util.HashSet;
import java.util.List;

public class MyTracing {
	
	private HashSet<List<Integer>> gemeinsamObjectsSet = null;
	private HashSet<Integer> omittedNodes = null;
	private HashSet<Integer> onTraceLiefer = null;
	private HashSet<Integer> onTraceStation = null;
	private int numNodesUsed = 0;
	private List<MyChain> chainCluster = null; // getAllChainsHavingCase
	private boolean foundFkLink = false;

	public MyTracing() {
		this(null);
	}
	public MyTracing(List<MyChain> chainCluster) {
		this.chainCluster = chainCluster;
		gemeinsamObjectsSet = new HashSet<List<Integer>>();
		omittedNodes = new HashSet<Integer>();
		onTraceLiefer = new HashSet<Integer>();
		onTraceStation = new HashSet<Integer>();
	}

	public boolean isClusterCenter(int stationID) {
		for (MyChain mc : chainCluster) {
			if (mc.getCaseID() == stationID) return true;
		}
		return false;
	}
	public HashSet<Integer> getClusterCenters() {
		HashSet<Integer> result = new HashSet<Integer>();
		for (MyChain mc : chainCluster) {
			result.add(mc.getCaseID());
		}
		return result;
	}
	public int getNumNodesUsed() {
		return numNodesUsed;
	}

	public void setNumNodesUsed(int numNodesUsed) {
		this.numNodesUsed = numNodesUsed;
	}

	public int getNumNodesTotal() {
		return chainCluster.size();
	}

	public List<MyChain> getChainCluster() {
		return chainCluster;
	}

	public boolean isFoundFkLink() {
		return foundFkLink;
	}

	public void setFoundFkLink(boolean foundFkLink) {
		this.foundFkLink = foundFkLink;
	}

	public HashSet<List<Integer>> getGemeinsamObjectsSet() {
		return gemeinsamObjectsSet;
	}

	public HashSet<Integer> getOmittedNodes() {
		return omittedNodes;
	}
	
	public void calcOnTrace(MyRealChain mrc, MyRelations mr, int objectType) {
		onTraceLiefer = new HashSet<Integer>();
		onTraceStation = new HashSet<Integer>();
		if (chainCluster != null && chainCluster.size() > 0) {
			HashSet<Integer> caseLieferIDs = new HashSet<Integer>(); 
			for (MyChain mc : chainCluster) {
				onTraceLiefer.add(mc.getCaseLieferID());
				onTraceStation.add(mc.get(mc.getCaseLieferID()));
				caseLieferIDs.add(mc.getCaseLieferID());
			}
			for (List<Integer> gosl : gemeinsamObjectsSet) {
				for (Integer gos : gosl) {
					onTraceStation.add(mr.getStationFromObject(gos, objectType));
					HashSet<Integer> lfo = mr.getLieferungFromObject(gos, objectType);
					for (Integer lo : lfo) {
						for (MyChain mc : chainCluster) {
							if (mc.containsKey(lo)) {
								onTraceLiefer.add(lo);
								onTraceStation.add(mc.get(lo));
								// ist das so richtig?????? Ist der MyChain immer auch bis zum Ende auf Trace???? Wenn das so hier nicht gesetzt wird, dann werden sie auch nicht auf OnTrace gesetzt, weil die Station z.B. keine Lieferung mehr hat... 
								onTraceLiefer.add(mc.getCaseLieferID());
								onTraceStation.add(mc.getCaseID());
								// ist das so richtig??????
								break;
							}
						}
						checkNext(mrc, caseLieferIDs, lo);
					}
				}			
			}
		}
	}
	private boolean checkNext(MyRealChain mrc, HashSet<Integer> caseLieferIDs, Integer lo) {
		boolean result = false;
		HashSet<Integer> nextHS;
		if ((nextHS = mrc.getNext(lo)) != null) {
			for (Integer nextLID : nextHS) {
				if (caseLieferIDs.contains(nextLID)) {
					for (MyChain mc : chainCluster) {
						if (mc.containsKey(nextLID)) {
							onTraceLiefer.add(nextLID);
							onTraceStation.add(mc.get(nextLID));
							break;
						}
					}
					return true;
				}
				else {
					if (checkNext(mrc, caseLieferIDs, nextLID)) {
						result = true;
						for (MyChain mc : chainCluster) {
							if (mc.containsKey(nextLID)) {
								onTraceLiefer.add(nextLID);
								onTraceStation.add(mc.get(nextLID));
								break;
							}
						}
					}
				}
			}
		}		
		return result;
	}
	public boolean isOnTrace(int id, boolean isStation) {
		if (isStation) return onTraceStation.contains(id);
		else return onTraceLiefer.contains(id);
	}
}

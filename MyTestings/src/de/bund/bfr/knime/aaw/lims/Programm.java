package de.bund.bfr.knime.aaw.lims;

import java.util.HashMap;
import java.util.HashSet;

public class Programm {

	private String name;
	private int numSamples;
	private HashMap<Integer, Integer> numResistentArr;
	private int maxResi;
	private HashSet<String> groupResistance;
	private HashMap<String, Integer> groupResistanceCount; 
	private HashMap<String, Integer> numPostive; 
	
	public Programm() {
		numSamples = 1;
		groupResistance = new HashSet<String>();
		numResistentArr = new HashMap<Integer, Integer>();
		maxResi = 0;
		groupResistanceCount = new HashMap<String, Integer>();
		numPostive = new HashMap<String, Integer>();
	}

	public int getNumResistent(int i) {
		if (numResistentArr.containsKey(i)) return numResistentArr.get(i);
		else return 0;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNumSamples() {
		return numSamples;
	}
	public HashSet<String> getGroupResistance() {
		return groupResistance;
	}
	public HashMap<String, Integer> getNumPositive() {
		return numPostive;
	}
	public int getMaxResi() {
		return maxResi;
	}
	public HashMap<String, Integer> getGroupResistanceCount() {
		return groupResistanceCount;
	}

	public void addWirkstoff(Wirkstoff w, double value) {
		String kurz = w.getKurz();
		if (value > w.getCutoff()) {
			if (numPostive.containsKey(kurz)) numPostive.put(kurz, numPostive.get(kurz)+1);
			else numPostive.put(kurz, 1);
			groupResistance.add(w.getGruppe());
		}
	}
	public void sampleFin() {
		numResistentArr.put(groupResistance.size(), 1);
		for (String group : groupResistance) {
			groupResistanceCount.put(group, 1);
		}
	}
	public void merge(Programm p) {
		int pnr = p.getGroupResistance().size();
		if (!numResistentArr.containsKey(pnr)) numResistentArr.put(pnr, 1);
		else numResistentArr.put(pnr, numResistentArr.get(pnr) + 1);
		if (pnr > maxResi) maxResi = pnr;

		numSamples += p.getNumSamples();

		HashMap<String, Integer> pgrc = p.getGroupResistanceCount();
		if (pgrc != null) {
			for (String group : pgrc.keySet()) {
				if (groupResistanceCount.containsKey(group)) groupResistanceCount.put(group, pgrc.get(group) + groupResistanceCount.get(group));
				else groupResistanceCount.put(group, pgrc.get(group));
			}
		}

		HashMap<String, Integer> pw = p.getNumPositive();
		if (pw != null) {
			for (String kurz : pw.keySet()) {
				if (numPostive.containsKey(kurz)) numPostive.put(kurz, pw.get(kurz) + numPostive.get(kurz));
				else numPostive.put(kurz, pw.get(kurz));
			}
		}
	}
}

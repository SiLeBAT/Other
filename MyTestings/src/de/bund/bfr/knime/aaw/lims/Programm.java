package de.bund.bfr.knime.aaw.lims;

public class Programm {

	private String name;
	private int numSamples, numPositive;
	private int numSensibel;
	private int[] numResistentArr;
	
	public Programm() {
		numSamples = 1;
		numPositive = 0;
		numSensibel = 0;
		numResistentArr = new int[7];
		for (int i=0;i<numResistentArr.length;i++) {
			numResistentArr[i] = 0;
		}
	}
	public void merge(Programm p) {
		this.numPositive += p.getNumPositive();
		this.numSensibel += p.getNumSensibel();
		for (int i=0;i<numResistentArr.length;i++) {
			numResistentArr[i] += p.getNumResistent(i);
		}
		numSamples += p.getNumSamples();
	}
	
	public int getNumSensibel() {
		return numSensibel;
	}

	public void setNumSensibel(int numSensibel) {
		this.numSensibel = numSensibel;
	}

	public int getNumResistent(int i) {
		if (i < numResistentArr.length) return numResistentArr[i];
		else return 0;
	}

	public void setNumResistent(int i, int numResistent) {
		if (i < numResistentArr.length) numResistentArr[i] = numResistent;
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
	public void setNumSamples(int numSamples) {
		this.numSamples = numSamples;
	}
	public int getNumPositive() {
		return numPositive;
	}
	public void setNumPositive(int numPositive) {
		this.numPositive = numPositive;
	}
	
}

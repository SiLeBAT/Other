package de.bund.bfr.knime.aaw.lims;

public class Wirkstoff {

	private String name, gruppe, kurz;
	private int indexSort;
	private double cutoff, cutoffL, cutoffH;

	public int getIndexSort() {
		return indexSort;
	}

	public void setIndexSort(int indexSort) {
		this.indexSort = indexSort;
	}

	public double getCutoff() {
		return cutoff;
	}

	public void setCutoff(double cutoff) {
		this.cutoff = cutoff;
	}

	public double getCutoffL() {
		return cutoffL;
	}

	public void setCutoffL(double cutoffL) {
		this.cutoffL = cutoffL;
	}

	public double getCutoffH() {
		return cutoffH;
	}

	public void setCutoffH(double cutoffH) {
		this.cutoffH = cutoffH;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGruppe() {
		return gruppe;
	}

	public void setGruppe(String gruppe) {
		this.gruppe = gruppe;
	}

	public String getKurz() {
		return kurz;
	}

	public void setKurz(String kurz) {
		this.kurz = kurz;
	}
}

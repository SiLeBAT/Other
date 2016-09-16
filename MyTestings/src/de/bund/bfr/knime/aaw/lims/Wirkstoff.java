package de.bund.bfr.knime.aaw.lims;

public class Wirkstoff {

	private String name, gruppe, kurz;

	private Integer indexSort;
	private double cutoff, cutoff2, rangeLow, rangeHigh;

	public Integer getIndexSort() {
		return indexSort;
	}

	public void setIndexSort(Integer indexSort) {
		this.indexSort = indexSort;
	}

	public double getCutoff2() {
		return cutoff2;
	}

	public void setCutoff2(double cutoff2) {
		this.cutoff2 = cutoff2;
	}

	public double getCutoff() {
		return cutoff;
	}

	public void setCutoff(double cutoff) {
		this.cutoff = cutoff;
	}

	public double getRangeLow() {
		return rangeLow;
	}

	public void setRangeLow(double rangeLow) {
		this.rangeLow = rangeLow;
	}

	public double getRangeHigh() {
		return rangeHigh;
	}

	public void setRangeHigh(double rangeHigh) {
		this.rangeHigh = rangeHigh;
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

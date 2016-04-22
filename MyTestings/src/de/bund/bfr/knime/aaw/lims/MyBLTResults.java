package de.bund.bfr.knime.aaw.lims;

public class MyBLTResults {

	private double vorbefundScore, v_pnScore, betriebsartMatch, probenahmeortMatch;
	private Boolean v_date, v_adv, v_status;
	
	public double getVorbefundScore() {
		return vorbefundScore;
	}
	public void setVorbefundScore(double vorbefundScore) {
		this.vorbefundScore = vorbefundScore;
	}
	public double getV_pnScore() {
		return v_pnScore;
	}
	public void setV_pnScore(double v_pnScore) {
		this.v_pnScore = v_pnScore;
	}
	public Boolean getV_date() {
		return v_date;
	}
	public void setV_date(Boolean v_date) {
		this.v_date = v_date;
	}
	public Boolean getV_adv() {
		return v_adv;
	}
	public void setV_adv(Boolean v_adv) {
		this.v_adv = v_adv;
	}
	public Boolean getV_status() {
		return v_status;
	}
	public void setV_status(Boolean v_status) {
		this.v_status = v_status;
	}
	public double getBetriebsartMatch() {
		return betriebsartMatch;
	}
	public void setBetriebsartMatch(double betriebsartMatch) {
		this.betriebsartMatch = betriebsartMatch;
	}
	public double getProbenahmeortMatch() {
		return probenahmeortMatch;
	}
	public void setProbenahmeortMatch(double probenahmeortMatch) {
		this.probenahmeortMatch = probenahmeortMatch;
	}
}

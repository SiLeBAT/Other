package de.bund.bfr.knime.aaw.hartung;

import java.util.HashMap;

public class Testings {

	private String agent = null;
	private HashMap<Integer, Quant> quants = null;
	
	public Testings() {
		quants = new HashMap<Integer, Quant>();
	}
	
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public HashMap<Integer, Quant> getQuants() {
		return quants;
	}
	public void setQuants(HashMap<Integer, Quant> quants) {
		this.quants = quants;
	}
}

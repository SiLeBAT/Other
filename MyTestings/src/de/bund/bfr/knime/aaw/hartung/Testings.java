package de.bund.bfr.knime.aaw.hartung;

import java.util.HashMap;

public class Testings {

	private String agent = null;
	private Integer agentCol = null;
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

	public Integer getAgentCol() {
		return agentCol;
	}

	public void setAgentCol(Integer agentCol) {
		this.agentCol = agentCol;
	}
	public boolean hasKBE() {
		return quants != null && quants.size() > 1;
	}
	public Integer getQuantSum() {
		Integer result = null;
		for (Quant q : quants.values()) {
			if (q.getAmount() != null) {
				if (result == null) result = 0;
				result += q.getAmount();
			}
		}
		return result;
	}
}

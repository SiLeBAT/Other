package de.bund.bfr.knime.aaw.hartung;

public class Quant {
	
	private String quantum = null;
	private Integer amount = null;

	public Quant(String quantum, Integer amount) {
		this.quantum = quantum;
		this.amount = amount;
	}

	public String getQuantum() {
		return quantum;
	}
	public void setQuantum(String quantum) {
		this.quantum = quantum;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
}

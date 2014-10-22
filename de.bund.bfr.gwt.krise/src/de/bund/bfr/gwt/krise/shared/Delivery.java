package de.bund.bfr.gwt.krise.shared;

import java.io.Serializable;

public class Delivery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4827438816852722078L;
	private int id;
	private int from;
	private int to;
	
	public Delivery() {
		
	}
	public Delivery(int id, int from, int to) {
		this.id = id;
		this.from = from;
		this.to = to;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public int getTo() {
		return to;
	}
	public void setTo(int to) {
		this.to = to;
	}
}

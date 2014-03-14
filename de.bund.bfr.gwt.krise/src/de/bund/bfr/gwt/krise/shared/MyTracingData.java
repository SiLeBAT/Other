package de.bund.bfr.gwt.krise.shared;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class MyTracingData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -329036924098450803L;
	
	public static final int STATION = 0;
	public static final int PRODUCT = 1;
	public static final int LOT = 2;
	public static final int DELIVERY = 3;
	
	private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Boolean>>> items;
	public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Boolean>>> getItems() {
		return items;
	}
	public void setItems(LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Boolean>>> items) {
		this.items = items;
	}
	
	private LinkedHashSet<MyField> cols;
	public LinkedHashSet<MyField> getCols() {
		return cols;
	}
	public void setCols(LinkedHashSet<MyField> cols) {
		this.cols = cols;
	}
	
	private List<List<String>> rows;
	public List<List<String>> getRows() {
		return rows;
	}
	public void setRows(List<List<String>> rows) {
		this.rows = rows;
	}
	public static String getTable(int tableType) {
		if (tableType == STATION) return "station";
		else if (tableType == PRODUCT) return "product";
		else if (tableType == LOT) return "lot";
		else if (tableType == DELIVERY) return "delivery";
		else return "";
	}
}

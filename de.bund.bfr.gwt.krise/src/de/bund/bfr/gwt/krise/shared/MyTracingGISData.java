package de.bund.bfr.gwt.krise.shared;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class MyTracingGISData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2094870544300208335L;
	private LinkedHashMap<Integer, Station> stations;
	private LinkedHashMap<Integer, Delivery>  deliveries;

	public LinkedHashMap<Integer, Delivery>  getDeliveries() {
		return deliveries;
	}
	public void setDeliveries(LinkedHashMap<Integer, Delivery>  deliveries) {
		this.deliveries = deliveries;
	}
	public LinkedHashMap<Integer, Station> getStations() {
		return stations;
	}
	public void setStations(LinkedHashMap<Integer, Station> stations) {
		this.stations = stations;
	}
	
}

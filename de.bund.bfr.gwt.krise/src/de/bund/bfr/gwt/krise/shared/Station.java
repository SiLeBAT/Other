package de.bund.bfr.gwt.krise.shared;

import java.io.Serializable;

import org.gwtopenmaps.openlayers.client.geometry.Point;

public class Station implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6253886759343797874L;
	private int id;
	private String name;
	private double lat;
	private double lon;

	public Station() {
		
	}
	public Station(int id, String name, double lon, double lat) {
		this.id = id;
		this.name = name;
		this.lon = lon;
		this.lat = lat;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public Point getPoint() {
		return new Point(lon, lat);
	}
}

package de.bund.bfr.gwt.krise.client;

import org.thechiselgroup.choosel.protovis.client.PVColor;

public class MyNodes {
	private PVColor color;
	private String label;
	public String getLabel() {
		return label;
	}
	public int getValue() {
		return value;
	}
	private int value;
	public MyNodes(String label, int value, PVColor color) {
		this.label = label;
		this.value = value;
		this.color = color;
	}

	public PVColor getColor() {
		return color;
	}
}

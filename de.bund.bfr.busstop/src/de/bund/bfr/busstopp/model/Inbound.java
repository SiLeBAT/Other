package de.bund.bfr.busstopp.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Inbound {
	private String filename = "";
	private String comment = "";
	private String environment = "";

	public String getEnvironment() {
		return environment.trim();
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
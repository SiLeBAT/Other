package de.bund.bfr.busstopp.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Item {
	private Long id;
	private String filename = "";
	private String comment = "";

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

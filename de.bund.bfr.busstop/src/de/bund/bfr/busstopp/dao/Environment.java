package de.bund.bfr.busstopp.dao;

import java.util.LinkedHashMap;
import java.util.Map;

public class Environment {

	private Map<Long, ItemLoader> contentProvider = null;
	private Map<Long, ItemLoader> contentDelProvider = null;
	private String environment = null;
	
	public Environment(String environment) {
		this.environment = environment;
		contentProvider = new LinkedHashMap<>();
		contentDelProvider = new LinkedHashMap<>();
	}
	
	public Map<Long, ItemLoader> getContentProvider() {
		return contentProvider;
	}

	public void setContentProvider(Map<Long, ItemLoader> contentProvider) {
		this.contentProvider = contentProvider;
	}

	public Map<Long, ItemLoader> getContentDelProvider() {
		return contentDelProvider;
	}

	public void setContentDelProvider(Map<Long, ItemLoader> contentDelProvider) {
		this.contentDelProvider = contentDelProvider;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}
}

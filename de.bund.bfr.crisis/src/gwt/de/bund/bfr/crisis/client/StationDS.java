package de.bund.bfr.crisis.client;

import com.smartgwt.client.data.fields.DataSourceTextField;

public class StationDS extends GrailsDataSource {

	private static StationDS instance = null;

	public static StationDS getInstance() {
		if (instance == null) {
			instance = new StationDS();
		}
		return instance;
	}

	public StationDS() {
		super("station");

		String[] fieldNames =
			{ "name", "vatNumber", "street", "houseNumber", "zipCode", "city", "postOfficeBox", "county", "country" };
		for (String fieldName : fieldNames) 
			addField(new DataSourceTextField(fieldName));
	}
}
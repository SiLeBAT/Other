package de.bund.bfr.crisis.client;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class ProductDS extends GrailsDataSource {

	private static ProductDS instance = null;

	public static ProductDS getInstance() {
		if (instance == null) {
			instance = new ProductDS();
		}
		return instance;
	}

	public ProductDS() {
		super("product");

		String[] fieldNames =
			{ "itemNumber", "denomination", "processing", "intendedUse", "station" };
		for (String fieldName : fieldNames) 
			addField(new DataSourceTextField(fieldName));
		
		DataSourceField stationField = getField("station");
		stationField.setHidden(true);
		stationField.setForeignKey("station.id");
	}
}
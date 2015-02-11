package de.bund.bfr.crisis.client;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class DeliveryDS extends GrailsDataSource {

	private static DeliveryDS instance = null;

	public static DeliveryDS getInstance() {
		if (instance == null) {
			instance = new DeliveryDS();
		}
		return instance;
	}

	public DeliveryDS() {
		super("delivery");

		addField(new DataSourceFloatField("packagingUnits"));
		addField(new DataSourceTextField("packagingType"));
		addField(new DataSourceFloatField("amount"));
		addField(new DataSourceTextField("unit"));
		
		String[] fieldNames = { "deliveryDateDay", "deliveryDateMonth", "deliveryDateYear" };
		for (String fieldName : fieldNames) 
			addField(new DataSourceIntegerField(fieldName));
		
		DataSourceField lotField = new DataSourceTextField("lot");
		lotField.setHidden(true);
		lotField.setForeignKey("lot.id");
		addField(lotField);
	}
}
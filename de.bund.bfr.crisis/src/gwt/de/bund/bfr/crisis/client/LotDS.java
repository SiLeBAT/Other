package de.bund.bfr.crisis.client;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class LotDS extends GrailsDataSource {

	private static LotDS instance = null;

	public static LotDS getInstance() {
		if (instance == null) {
			instance = new LotDS();
		}
		return instance;
	}

	public LotDS() {
		super("lot");

		addField(new DataSourceTextField("lotNumber"));
		addField(new DataSourceFloatField("amount"));
		addField(new DataSourceTextField("unit"));
		
		String[] fieldNames =
			{ "bestBeforeDay", "bestBeforeMonth", "bestBeforeYear", "productionDay", "productionMonth", "productionYear" };
		for (String fieldName : fieldNames) 
			addField(new DataSourceIntegerField(fieldName));
		
		DataSourceField productField = new DataSourceTextField("product");
		productField.setHidden(true);
		productField.setForeignKey("product.id");
		addField(productField);
	}
}
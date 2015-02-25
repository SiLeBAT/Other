package de.bund.bfr.crisis.client;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.form.validator.FloatRangeValidator;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;

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
		
		DataSourceField recipientField = new DataSourceTextField("recipient");
		recipientField.setForeignKey("station.id");
		recipientField.setDisplayField("name");
		addField(recipientField);
		DataSourceTextField recipientName = new DataSourceTextField("recipientName");
		recipientName.setHidden(true);
		recipientName.setCanEdit(false);
		addField(recipientName);

		IntegerRangeValidator dayValidator = new IntegerRangeValidator();
		dayValidator.setMin(1);
		dayValidator.setMax(31);
		getField("deliveryDateDay").setValidators(dayValidator);
		IntegerRangeValidator monthValidator = new IntegerRangeValidator();
		monthValidator.setMin(1);
		monthValidator.setMax(12);
		getField("deliveryDateMonth").setValidators(monthValidator);
		IntegerRangeValidator yearValidator = new IntegerRangeValidator();
		yearValidator.setMin(1900);
		yearValidator.setMax(2100);
		getField("deliveryDateYear").setValidators(yearValidator);
		
		FloatRangeValidator amountValidator = new FloatRangeValidator();
		amountValidator.setMin(0);
		getField("amount").setValidators(amountValidator);
	}
}
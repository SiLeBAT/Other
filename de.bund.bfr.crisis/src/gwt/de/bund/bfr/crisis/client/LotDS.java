package de.bund.bfr.crisis.client;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.form.validator.FloatRangeValidator;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;

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
		
		IntegerRangeValidator dayValidator = new IntegerRangeValidator();
		dayValidator.setMin(1);
		dayValidator.setMax(31);
		getField("bestBeforeDay").setValidators(dayValidator);
		getField("productionDay").setValidators(dayValidator);
		IntegerRangeValidator monthValidator = new IntegerRangeValidator();
		monthValidator.setMin(1);
		monthValidator.setMax(12);
		getField("bestBeforeMonth").setValidators(monthValidator);
		getField("productionMonth").setValidators(monthValidator);
		IntegerRangeValidator yearValidator = new IntegerRangeValidator();
		yearValidator.setMin(1900);
		yearValidator.setMax(2100);
		getField("bestBeforeYear").setValidators(yearValidator);
		getField("productionYear").setValidators(yearValidator);
		
		FloatRangeValidator amountValidator = new FloatRangeValidator();
		amountValidator.setMin(0);
		getField("amount").setValidators(amountValidator);
	}
}
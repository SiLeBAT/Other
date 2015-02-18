package de.bund.bfr.crisis.client;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.form.validator.FloatRangeValidator;

public class FoodRecipeDS extends GrailsDataSource {

	private static FoodRecipeDS instance = null;

	public static FoodRecipeDS getInstance() {
		if (instance == null) {
			instance = new FoodRecipeDS();
		}
		return instance;
	}

	public FoodRecipeDS() {
		super("foodRecipe");

		addField(new DataSourceFloatField("mixtureRatio"));
		
		DataSourceField lotField = new DataSourceTextField("lot");
		lotField.setHidden(true);
		lotField.setForeignKey("lot.id");
		addField(lotField);
		
		DataSourceField ingredientField = new DataSourceTextField("ingredient");
		ingredientField.setHidden(true);
		ingredientField.setForeignKey("delivery.id");
		addField(ingredientField);
		
		FloatRangeValidator mixtureRatioValidator = new FloatRangeValidator();
		mixtureRatioValidator.setMin(0);
		getField("mixtureRatio").setValidators(mixtureRatioValidator);
	}
}
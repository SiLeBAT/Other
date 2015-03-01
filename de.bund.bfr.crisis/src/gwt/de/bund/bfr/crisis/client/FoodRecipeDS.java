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
		
		DataSourceField ingredientField = new DataSourceTextField("ingredient");
		ingredientField.setForeignKey("delivery.id");
		ingredientField.setHidden(true);
		addField(ingredientField);
		
		DataSourceField lotField = new DataSourceTextField("lot");
		lotField.setHidden(true);
		lotField.setForeignKey("lot.id");
		addField(lotField);
//		DataSourceTextField deliveringStation = new DataSourceTextField("deliveringStation");
//		deliveringStation.setCanEdit(false);
//		addField(deliveringStation);
//		DataSourceTextField originalProduct = new DataSourceTextField("originalProduct");
//		originalProduct.setCanEdit(false);
//		addField(originalProduct);
//		DataSourceTextField originalLot = new DataSourceTextField("originalLot");
//		originalLot.setCanEdit(false);
//		addField(originalLot);
		
		FloatRangeValidator mixtureRatioValidator = new FloatRangeValidator();
		mixtureRatioValidator.setMin(0);
		getField("mixtureRatio").setValidators(mixtureRatioValidator);
	}
}
package de.bund.bfr.crisis

import java.util.Collection;

class Lot {
	String lotNumber
	
	double amount
	String unit
	
	Integer bestBeforeDay, bestBeforeMonth, bestBeforeYear
	Integer productionDay, productionMonth, productionYear 
	
	String originCountry, examinationResult
	
	Serial serial
	String comment
	
    static constraints = {
		bestBeforeDay range: 1..31
		bestBeforeMonth range: 1..12
		bestBeforeYear min: 2000
		productionDay range: 1..31
		productionMonth range: 1..12
		productionDay min: 2000
		amount min: 0d
    }
	
	static belongsTo = [product: Product]
	
	Collection<Delivery> deliveries
	Collection<FoodRecipe> foodRecipes
	
	static hasMany = [deliveries: Delivery, foodRecipes: FoodRecipe]		
}

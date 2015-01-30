package de.bund.bfr.crisis

import java.util.Collection;

class Delivery {
	int id
	
	double packagingUnits
	String packagingType
	
	double amount
	String unit
	
	Integer day, month, year
	
	boolean isEnd
	String endExplanation
	boolean furtherTraceback
	Serial serial
	String comment
	
	Station getStation() {
		this.lot.product.station
	}
	
	Station getRecipient() {
		this.foodRecipes ? this.foodRecipes[0].lot.product.station : null
	}
	
    static constraints = {
		day range: 1..31
		month range: 1..12
		year range: 1900..2100
		packagingUnits min: 0d
    }
	
	Collection<FoodRecipe> foodRecipes
	
	static hasMany = [foodRecipes: FoodRecipe]
	
	static belongsTo = [lot: Lot]
}

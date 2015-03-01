package de.bund.bfr.crisis

import java.util.Collection;

class Delivery {
	double packagingUnits
	String packagingType
	
	double amount
	String unit
	
	Integer deliveryDateDay, deliveryDateMonth, deliveryDateYear
	
	boolean isEnd
	String endExplanation
	boolean furtherTraceback
	Serial serial
	String comment
	Station recipient
	
	Station getStation() {
		this?.lot?.product?.station
	}
	
	String getRecipientName() {
		recipient?.name
	}
	
    static constraints = {
		deliveryDateDay range: 1..31
		deliveryDateMonth range: 1..12
		deliveryDateYear range: 1900..2100
		packagingUnits min: 0d
    }
	
	Collection<FoodRecipe> foodRecipes
	
	static hasMany = [foodRecipes: FoodRecipe]
	
	static belongsTo = [lot: Lot]
}

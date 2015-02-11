package de.bund.bfr.crisis

class FoodRecipe {
	Lot lot
	
	Double mixtureRatio
	
	Serial serial
	String comment
	
	static belongsTo = [ingredient: Delivery]

    static constraints = {
		lot nullable: false
    }
}

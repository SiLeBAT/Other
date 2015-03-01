package de.bund.bfr.crisis

class FoodRecipe {
	Lot lot
	
//	String getDeliveringStation() {
//		lot?.product?.station?.name
//	}
//	
//	String getOriginalProduct() {
//		lot?.product?.denomination
//	}
//	
//	String getOriginalLot() {
//		lot?.lotNumber
//	}
//	
//	Integer getDeliveryDay() {
//		ingredient?.deliveryDateDay
//	}
//	
//	Integer getDeliveryMonth() {
//		ingredient?.deliveryDateMonth
//	}
//	
//	Integer getDeliveryYear() {
//		ingredient?.deliveryDateYear
//	}
	
	Double mixtureRatio
	
	Serial serial
	String comment
	
	static belongsTo = [ingredient: Delivery]

    static constraints = {
		lot nullable: false
    }
}

package de.bund.bfr.crisis

class Station {
	int id
	
	String name, vatNumber
	
	String street, city, zipCode, postOfficeBox, county, country, houseNumber

	double latitude, longitude
	
	String businessNumber, typeOfBusiness
	
	Serial serial
	String comment
	
    static constraints = {
    }
	
	Collection<Product> products
	
	static hasMany = [products: Product]
}
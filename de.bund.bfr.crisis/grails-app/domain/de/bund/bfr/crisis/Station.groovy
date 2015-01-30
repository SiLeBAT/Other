package de.bund.bfr.crisis

class Station {
	int id
	
	String name, vatNumber
	
	String street, city, zipCode, postOfficeBox, county, country, houseNumber

	BigDecimal latitude, longitude
	
	String businessNumber, typeOfBusiness
	
	Serial serial
	String comment
	
    static constraints = {
		latitude scale: 16
		longitude scale: 16
    }
	
	Collection<Product> products
	
	static hasMany = [products: Product]
}
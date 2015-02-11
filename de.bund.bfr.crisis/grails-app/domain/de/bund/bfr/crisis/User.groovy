package de.bund.bfr.crisis


class User {
	String name, email
	
	// settings for default view
	int zoomlevel		
	BigDecimal latitude, longitude

    static constraints = {
		email email: true, blank: false
		name: blank: false
    }
}

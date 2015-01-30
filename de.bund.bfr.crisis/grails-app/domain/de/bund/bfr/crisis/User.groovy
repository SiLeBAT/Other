package de.bund.bfr.crisis

class User {
	String name, email

    static constraints = {
		email email: true, blank: false
		name: blank: false
    }
}

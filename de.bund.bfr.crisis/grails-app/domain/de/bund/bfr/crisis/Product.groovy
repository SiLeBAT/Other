package de.bund.bfr.crisis

import java.util.Collection;

class Product {
	String itemNumber, denomination
	
	String processing, intendedUse
	
	Serial serial
	String comment
	
    static constraints = {
    }
	
	static belongsTo = [station: Station]
	
	Collection<Lot> lots
	
	static hasMany = [lots: Lot]
}

package de.bund.bfr.crisis

class Serial {
	String serial
	User author
	User reviewer
	
	String rejectReason
	
	Serial(String serial) {
		this.serial = serial
	}
    static constraints = {
    }
}

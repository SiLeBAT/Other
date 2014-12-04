package de.bund.bfr.gnuml;

import groovy.transform.InheritConstructors;

@InheritConstructors
class NuMLException extends RuntimeException {
	def errors = []

	@Override
	String getMessage() {
		"${super.getMessage()}:\n${errors.join('\n')}"
	}
}
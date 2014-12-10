package de.bund.bfr.numl;

import groovy.transform.InheritConstructors

import org.apache.log4j.Level

/**
 * Thrown when reading or writing invalid NuML with a detailed list of {@link ConformityMessage}s.
 */
@InheritConstructors
class NuMLException extends RuntimeException {
	List<ConformityMessage> messages = []

	List<ConformityMessage> getErrors() {
		messages.findAll { it.level.isGreaterOrEqual(Level.ERROR) }
	}

	@Override
	String getMessage() {
		"${super.getMessage()}:\n${messages.join('\n')}"
	}
}
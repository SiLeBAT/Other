package de.bund.bfr.numl;

import groovy.transform.InheritConstructors;

/**
 * Thrown when reading or writing invalid NuML with a detailed list of {@link ConformityMessage}s.
 */
@InheritConstructors
class NuMLException extends RuntimeException {
	List<ConformityMessage> errors = []

	@Override
	String getMessage() {
		"${super.getMessage()}:\n${errors.join('\n')}"
	}
}
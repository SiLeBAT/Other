/*******************************************************************************
 * Copyright (c) 2015 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Department Biological Safety - BfR
 *******************************************************************************/
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
/*******************************************************************************
 * Copyright (c) 2014 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.bund.bfr.pmf

import groovy.transform.InheritConstructors

import org.apache.log4j.Level

import de.bund.bfr.numl.ConformityMessage

/**
 * An exception indicating invalid settings in a {@link PMFDocument} while parsing or writing the document.
 */
@InheritConstructors
class PMFException extends RuntimeException {
	List<ConformityMessage> messages = []

	List<ConformityMessage> getErrors() {
		messages.findAll { it.level.isGreaterOrEqual(Level.ERROR) }
	}
	
	@Override
	String getMessage() {
		"${super.getMessage()}:\n${messages.join('\n')}"
	}
}

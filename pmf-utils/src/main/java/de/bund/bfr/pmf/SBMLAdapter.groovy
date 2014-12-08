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

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.spi.LoggingEvent
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.SBMLException
import org.sbml.jsbml.SBMLReader
import org.sbml.jsbml.SBMLWriter
import org.sbml.jsbml.validator.SBMLValidator

import de.bund.bfr.numl.ConformityMessage


class SBMLAdapter {
	SBMLReader reader = new SBMLReader()
	SBMLDocument document
	static logAdapter
	boolean validating = false
	def messages
	
	static {
		logAdapter = Logger.getLogger(SBMLReader).hierarchy.currentLoggers.findResult { it.allAppenders.find { it } }
	}
	
	SBMLDocument read(InputStream stream) {		
		parseText(stream.text)
	}
	
	SBMLDocument parseText(String xmlString) {
		this.messages = logAdapter.messages = []
		
		try {
			document = reader.readSBMLFromString(xmlString)
			logAdapter.messages = []
			if(document.level == -1)
				document = null
			if(!validating) {
				def messages = this.getParseMessages(Level.ERROR)
				if(messages)
					throw new SBMLException("Invalid SBML document ${messages.join('\n')}")
			} else { // try additional validation
				def errorLog = SBMLValidator.checkConsistency(xmlString)
				this.messages += errorLog.validationErrors.collect { error -> 
					new ConformityMessage(level: error.severity as Level, message: error.message)
				}
			}
		} catch(e) {		
			def errorLog = SBMLValidator.checkConsistency(xmlString)
			this.messages += errorLog.validationErrors.collect { error ->
				new ConformityMessage(level: error.severity as Level, message: error.message)
			}
			if(!validating)
				throw new SBMLException("Invalid SBML document ${messages.join('\n')}", e)
		}
		document
	}
	
	String toString(SBMLDocument document) {
		new SBMLWriter().writeSBMLToString(document)
	}
	
	List<ConformityMessage> getParseMessages(Level level = Level.WARN) {
		messages.grep { it.level.isGreaterOrEqual(level) }
	}
}

class SBMLLogAdapater extends AppenderSkeleton {
	List<ConformityMessage> messages = []
	
	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		messages << new ConformityMessage(level: event.level, message: event.message)
	}	
}
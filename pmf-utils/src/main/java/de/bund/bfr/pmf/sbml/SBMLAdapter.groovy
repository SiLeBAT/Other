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
package de.bund.bfr.pmf.sbml

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
import de.bund.bfr.pmf.PMFUtil


class SBMLAdapter {
	SBMLReader reader = new SBMLReader()
	SBMLDocument document
	boolean validating = false
	def messages
		
	SBMLDocument read(InputStream stream) {		
		parseText(stream.text)
	}
	
	SBMLDocument parseText(String xmlString) {
		this.messages = []
		this.document = null
		
		try {
			document = reader.readSBMLFromString(xmlString)
			if(document.level == -1)
				document = null
		} catch(e) {
			// ignore exception for now; we use validator for more fine-grain descriptions		
			this.messages += e.message
		}
		
		// validate in case of error
		if(validating || !document) {				
			def errorLog = SBMLValidator.checkConsistency(xmlString)
			this.messages += errorLog.validationErrors.collect { error ->
				new ConformityMessage(level: error.severity as Level, message: error.message)
			}
		}
			
		if(!validating) {
			def messages = this.getParseMessages(Level.ERROR)
			if(messages)
				throw new SBMLException("Invalid SBML document ${messages.join('\n')}")
		} 
		document
	}
	
	String toString(SBMLDocument document) {
		SBMLDocument nsDocument = PMFUtil.wrap(document.clone())
		PMFUtil.standardPrefixes.each { prefix, uri ->
			nsDocument.addDeclaredNamespace("$prefix", uri)
		}		 
		PMFUtil.addStandardPrefixes(nsDocument)
		def xmlString = new SBMLWriter().writeSBMLToString(nsDocument)
		xmlString
	}
	
	List<ConformityMessage> getParseMessages(Level level = Level.WARN) {
		messages.grep { it.level.isGreaterOrEqual(level) }
	}
}
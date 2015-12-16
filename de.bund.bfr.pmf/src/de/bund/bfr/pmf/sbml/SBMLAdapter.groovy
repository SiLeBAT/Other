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
package de.bund.bfr.pmf.sbml

import java.beans.PropertyChangeEvent
import java.nio.charset.StandardCharsets;

import javax.swing.tree.TreeNode

import org.apache.log4j.Level
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.SBMLException
import org.sbml.jsbml.util.TreeNodeChangeListener
import org.sbml.jsbml.util.TreeNodeRemovedEvent
import org.sbml.jsbml.validator.SBMLValidator
import org.sbml.jsbml.xml.stax.SBMLReader
import org.sbml.jsbml.xml.stax.SBMLWriter

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
		
		TreeNodeChangeListener noLogging = new TreeNodeChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
			}
			
			@Override
			public void nodeRemoved(TreeNodeRemovedEvent event) {
			}
			
			@Override
			public void nodeAdded(TreeNode node) {
			}
		}

		try {
			document = reader.readSBMLFromString(xmlString, noLogging)
			if(document.level == -1)
				document = null
		} catch(e) {
			// ignore exception for now; we use validator for more fine-grain descriptions
			this.messages += e.message
		}

		// validate in case of error
		if(validating || !document) {
			def errorLog = SBMLValidator.checkConsistency(xmlString, [:])
			if(errorLog == null)
				this.messages += new ConformityMessage(level: Level.WARN, message: 'Cannot use validation service. If you are behind a proxy configure it with http://docs.oracle.com/javase/6/docs/technotes/guides/net/proxies.html')
			else
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
		def xmlString = null
		
		new ByteArrayOutputStream().withStream {
			new SBMLWriter().write(nsDocument, it)
			xmlString = it.toString(StandardCharsets.UTF_8.name());
		}
		xmlString
	}

	List<ConformityMessage> getParseMessages(Level level = Level.WARN) {
		messages.grep { it.level.isGreaterOrEqual(level) }
	}
}
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
package de.bund.bfr.gnuml

import java.util.logging.Level;

import javax.xml.XMLConstants;

import groovy.xml.XmlUtil;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource
import org.xml.sax.SAXParseException;

/**
 * 
 */
class NuMLReader {
	private XmlParser parser = new XmlParser(XmlUtil.newSAXParser(XMLConstants.W3C_XML_SCHEMA_NS_URI, true, false,
	    NuMLReader.class.getResource("/NUMLSchema.xsd").toURI().toURL()))
	
	private NuMLDocument document
	
	boolean lenient

	def NuMLReader() {
		parser.errorHandler = new CollectingErrorHandler()
	}

	NuMLDocument read(File file) {
		parseNode(parser.parse(file))
	}

	NuMLDocument read(InputSource input) {
		parseNode(parser.parse(input))
	}

	NuMLDocument read(InputStream input) {
		parseNode(parser.parse(input))
	}

	NuMLDocument read(Reader reader) {
		parseNode(parser.parse(reader))
	}

	NuMLDocument read(String uri) {
		parseNode(parser.parse(uri))
	}

	NuMLDocument parseText(String text) {
		parseNode(parser.parseText(text))
	}

	NuMLDocument parseNode(Node node) {
		// reset
		document = null
		parser.errorHandler.messages.clear()
		
		document = new NuMLDocument(originalNode: node)
		if(!lenient) {
			def messages = this.parseMessages
			if(messages)
				throw new NuMLException("Invalid NuML document").with { errors = messages; it }
		}
		document
	}
	
	List<ConformityMessage> getParseMessages(Level level = Level.SEVERE) {
		document.invalidSettings + 
			parser.errorHandler.messages.grep { it.level.intValue() >= level.intValue() }
	}
}

class CollectingErrorHandler implements ErrorHandler {
	def messages = []

	void error(final SAXParseException ex) {
		addMsg(Level.SEVERE, "XML validation error", ex)
	}

	void fatalError(final SAXParseException ex) {
		addMsg(Level.SEVERE,"Fatal XML validation error", ex);
	}

	void warning(final SAXParseException ex) {
		addMsg(Level.WARNING,"XML validation warning", ex);
	}

	private addMsg(Level level, String msg, SAXParseException ex) {
		messages << new ConformityMessage(level: level, 
			message: "$msg @ line ${ex.lineNumber}, column ${ex.columnNumber}: ${ex.message}")
	}
}
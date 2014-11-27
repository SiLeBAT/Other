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
	
	boolean lenient

	def NuMLReader() {
		parser.errorHandler = new CollectingErrorHandler()
	}

	NuMLDocument parse(File file) {
		parseNode(parser.parse(file))
	}

	NuMLDocument parse(InputSource input) {
		parseNode(parser.parse(input))
	}

	NuMLDocument parse(InputStream input) {
		parseNode(parser.parse(input))
	}

	NuMLDocument parse(Reader reader) {
		parseNode(parser.parse(reader))
	}

	NuMLDocument parse(String uri) {
		parseNode(parser.parse(uri))
	}

	NuMLDocument parseText(String text) {
		parseNode(parser.parseText(text))
	}

	NuMLDocument parseNode(Node node) {
		def doc = new NuMLDocument(originalNode: node)

		if(!lenient) {
			def invalidSettings = parser.errorHandler.errors + doc.invalidSettings
			if(invalidSettings)
				throw new NuMLException("Invalid NuML document").with { errors = invalidSettings; it }
		}

		doc
	}
	
	List<String> getParseErrors() {
		parser.errorHandler.errors
	}
}

class CollectingErrorHandler implements ErrorHandler {
	def errors = [], fatals = [], warnings = []

	void error(final SAXParseException ex) {
		errors << formatMsg("XML validation error", ex)
	}

	void fatalError(final SAXParseException ex) {
		fatals << formatMsg("Fatal XML validation error", ex);
	}

	void warning(final SAXParseException ex) {
		warnings << formatMsg("XML validation warning", ex);
	}

	private formatMsg(final String msg, final SAXParseException ex) {
		"$msg @ line ${ex.lineNumber}, column ${ex.columnNumber}: ${ex.message}"
	}
}
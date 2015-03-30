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
package de.bund.bfr.numl

import groovy.xml.XmlUtil

import java.nio.file.Path

import javax.xml.XMLConstants

import org.apache.log4j.Level
import org.xml.sax.ErrorHandler
import org.xml.sax.InputSource
import org.xml.sax.SAXParseException

/**
 * Reads a NuML file from various sources.<br/> 
 * <br/>
 * When {@link #validating} is set, it does not throw an {@link NuMLException} when encountering invalid NuML. 
 * Instead {@link #getParseMessages(Level)} returns a list of warnings and errors.
 */
class NuMLReader {
	private XmlParser parser = new XmlParser(XmlUtil.newSAXParser(XMLConstants.W3C_XML_SCHEMA_NS_URI, true, false,
	NuMLReader.class.getResource("/NUMLSchema.xsd").toURI().toURL()))

	NuMLDocument document

	boolean validating = false

	def NuMLReader() {
		parser.errorHandler = new CollectingErrorHandler()
	}

	NuMLDocument read(File file) {
		parser.errorHandler.messages.clear()
		parseNode(parser.parse(file))
	}

	NuMLDocument read(Path file) {
		read(file.toFile())
	}

	NuMLDocument read(InputSource input) {
		parser.errorHandler.messages.clear()
		parseNode(parser.parse(input))
	}

	NuMLDocument read(InputStream input) {
		parser.errorHandler.messages.clear()
		parseNode(parser.parse(input))
	}

	NuMLDocument read(Reader reader) {
		parser.errorHandler.messages.clear()
		parseNode(parser.parse(reader))
	}

	NuMLDocument read(String uri) {
		parser.errorHandler.messages.clear()
		parseNode(parser.parse(uri))
	}

	NuMLDocument parseText(String text) {
		parser.errorHandler.messages.clear()
		parseNode(parser.parseText(text))
	}

	NuMLDocument parseNode(Node node) {
		// reset
		document = null

		document = new NuMLDocument(originalNode: node)
		if(!validating) {
			def messages = this.getParseMessages(Level.ERROR)
			if(messages)
				throw new NuMLException("Invalid NuML document").with { it.messages = messages; it }
		}
		document
	}

	List<ConformityMessage> getParseMessages(Level level = Level.WARN) {
		document.invalidSettings +
				parser.errorHandler.messages.grep { it.level.isGreaterOrEqual(level) }
	}
}

class CollectingErrorHandler implements ErrorHandler {
	def messages = []

	void error(final SAXParseException ex) {
		addMsg(Level.ERROR, "XML validation error", ex)
	}

	void fatalError(final SAXParseException ex) {
		addMsg(Level.FATAL,"Fatal XML validation error", ex);
	}

	void warning(final SAXParseException ex) {
		addMsg(Level.WARN,"XML validation warning", ex);
	}

	private addMsg(Level level, String msg, SAXParseException ex) {
		messages << new ConformityMessage(level: level,
		message: "$msg @ line ${ex.lineNumber}, column ${ex.columnNumber}: ${ex.message}")
	}
}
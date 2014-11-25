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

import org.xml.sax.InputSource

/**
 * 
 */
class NUMLReader {
	private XmlParser parser = new XmlParser()

	NUMLDocument parse(File file) {
		parseNode(parser.parse(file))
	}

	NUMLDocument parse(InputSource input) {
		parseNode(parser.parse(input))
	}

	NUMLDocument parse(InputStream input) {
		parseNode(parser.parse(input))
	}

	NUMLDocument parse(Reader reader) {
		parseNode(parser.parse(reader))
	}

	NUMLDocument parse(String uri) {
		parseNode(parser.parse(uri))
	}

	NUMLDocument parseText(String text) {
		parseNode(parser.parseText(text))
	}

	NUMLDocument parseNode(Node node) {
		new NUMLDocument(originalNode: node)
	}
}

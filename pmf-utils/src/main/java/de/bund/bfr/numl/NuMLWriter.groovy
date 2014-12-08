
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
package de.bund.bfr.numl

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.InputSource;

import groovy.util.Node;
import groovy.xml.MarkupBuilder;
import groovy.xml.NamespaceBuilder;

/**
 * Writes a {@link NuMLDocument} to a writable source (file, stream, writer). 
 * The writer always validates the document before writing and potentially throws a {@link NuMLException}.
 */
class NuMLWriter {
	Map<String, String> namespaces = [:]

	def write(NuMLDocument doc, def writable) {
		writable.withWriter { writer ->
			write(doc, writer)
			writer
		}
	}

	String toString(NuMLDocument doc) {
		write(doc, new StringWriter()).toString()
	}

	def write(NuMLDocument doc, Writer writer) {
		if(doc.invalidSettings)
			throw new NuMLException("Invalid NuML document").with { errors = doc.invalidSettings; it }

		def builder = NodeBuilder.newInstance()
		//		builder.mkp.xmlDeclaration(version: '1.0')
		def nb = NamespaceBuilder.newInstance(namespaces, builder)
		nb.namespace("http://www.numl.org/numl/level${doc.level}/version${doc.version}")
		def root = doc.write(nb)
		def printer = new XmlNodePrinter(writer.newPrintWriter())
		printer.preserveWhitespace = true
		printer.print(root)
		writer
	}
}

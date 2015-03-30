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

import groovy.xml.NamespaceBuilder
import groovy.xml.StreamingMarkupBuilder

import org.apache.log4j.Level

/**
 * Writes a {@link NuMLDocument} to a writable source (file, stream, writer). 
 * The writer always validates the document before writing and potentially throws a {@link NuMLException}.
 */
class NuMLWriter {
	Map<String, String> namespaces = [:]

	def write(NuMLDocument doc, def writable) {
		writable.withWriter("UTF-8") { writer ->
			writeInternal(doc, writer)
			writer
		}
	}

	String toString(NuMLDocument doc) {
		writeInternal(doc, new StringWriter()).toString()
	}

	private def writeInternal(NuMLDocument doc, Writer writer) {
		if(doc.invalidSettings.find { it.level.isGreaterOrEqual(Level.ERROR) })
			throw new NuMLException("Invalid NuML document").with { it.messages = doc.invalidSettings; it }

		def builder = NodeBuilder.newInstance()
		def nb = NamespaceBuilder.newInstance(namespaces, builder)
		nb.namespace("http://www.numl.org/numl/level${doc.level}/version${doc.version}")
		def root = doc.write(nb)
		
		writer << new StreamingMarkupBuilder().bind { mkp.pi( xml:[ version:'1.0', encoding:'UTF-8' ] ) }
		writer << "\n"
		def printer = new XmlNodePrinter(writer.newPrintWriter())
		printer.preserveWhitespace = true
		printer.print(root)
		writer
	}
}

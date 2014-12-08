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

import groovy.xml.MarkupBuilder
import groovy.xml.NamespaceBuilder

import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import de.bund.bfr.numl.NuMLDocument
import de.bund.bfr.numl.NuMLException
import de.bund.bfr.numl.NuMLWriter

/**
 * 
 */
class PMFWriter {
	Map<String, String> namespaces = [:]
	
	def write(PMFDocument doc, def streamable) {
		streamable.withOutputStream { stream ->
			ZipOutputStream out = new ZipOutputStream(stream)
			toStrings(doc).each { name, xmlDoc ->
				out.putNextEntry(new ZipEntry(name))
				out << xmlDoc.getBytes(Charset.forName('utf-8'))
			}
		}
		streamable
	}

	Map<String, String> toStrings(PMFDocument doc) {
		if(doc.invalidSettings)
			throw new PMFException("Invalid PMF document").with { errors = doc.invalidSettings; it }
			
		doc.models.collectEntries { name, sbml ->
			[(name): new SBMLAdapter().toString(sbml)]
		} +
		doc.dataSets.collectEntries { name, sbml ->
			[(name): new NuMLWriter().toString(sbml)]
		}
	}

}

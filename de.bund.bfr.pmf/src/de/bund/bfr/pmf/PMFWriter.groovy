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
package de.bund.bfr.pmf

import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import org.apache.log4j.Level

import de.bund.bfr.numl.NuMLWriter
import de.bund.bfr.pmf.sbml.SBMLAdapter;

/**
 * 
 */
class PMFWriter {
	Map<String, String> namespaces = [:]
	
	def write(PMFDocument doc, OutputStream out) {
		def docStrings = toStrings(doc)
		out.withStream { stream ->
			new ZipOutputStream(stream).withStream { zipStream ->
				docStrings.each { name, xmlDoc ->
					zipStream.putNextEntry(new ZipEntry(name))
					zipStream << xmlDoc.getBytes(Charset.forName('utf-8'))
					zipStream.closeEntry()
				}
			}
		}
		out
	}
	
	def write(PMFDocument doc, def streamable) {
		streamable.withOutputStream { stream ->
			write(doc, stream)
		}
		streamable
	}
	
	byte[] toBytes(PMFDocument doc) {
		new ByteArrayOutputStream().withStream { stream ->
			write(doc, stream)
		}.toByteArray()
	}

	Map<String, String> toStrings(PMFDocument doc) {
		if(doc.invalidSettings.find { it.level.isGreaterOrEqual(Level.ERROR) })
			throw new PMFException('Invalid PMF document').with { it.messages = doc.invalidSettings; it }
			
		doc.models.collectEntries { name, sbml ->
			[(name): new SBMLAdapter().toString(sbml)]
		} +
		doc.dataSets.collectEntries { name, numl ->
			PMFUtil.addStandardPrefixes(numl)
			[(name): new NuMLWriter(namespaces: PMFUtil.standardPrefixes.clone()).toString(numl)]
		}
	}

}

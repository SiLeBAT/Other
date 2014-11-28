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
package de.bund.bfr.gpmf

import java.util.zip.ZipFile

import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.SBMLReader

import de.bund.bfr.gnuml.NuMLDocument
import de.bund.bfr.gnuml.NuMLReader

/**
 * 
 */
class PMFReader {
	def fileTypeReaders = [sbml: SBMLAdapter, numl: NuMLReader, xml: [SBMLAdapter, NuMLReader]]
	
	def PMFReader() {
	}
	
	PMFDocument read(File zip) {
		read(new ZipFile(zip))
	}

	PMFDocument read(InputStream zipStream) {
		cacheAndDo(zipStream, { readZip(it[0]) })
	}

	PMFDocument read(String zipUrl) {
		new URL(zipUrl).withInputStream { read(it) }
	}

	PMFDocument read(byte[] zipData) {
		new ByteArrayInputStream(zipData).withStream { read(it) }
	}
	
	PMFDocument read(ZipFile file) {
		readNamedStreams(file.entries().collectEntries { entry -> 
			[(entry.name): { file.getInputStream(entry) }]
		})
	}

	PMFDocument readFileSet(File... files) {
		readNamedStreams(files.collectEntries { file ->
			[(file.name): { new FileInputStream(file) }]
		})
	}
	
	PMFDocument readFileSet(... fileUrls) {
		readNamedStreams(fileUrls.collect { it as URL }.collectEntries { fileUrl ->
			[(fileUrl): { fileUrl.openStream() }]
		})
	}
	
	void cacheAndDo(InputStream[] inputs, Closure action) {		
		def cachedInputs = inputs.collect { input ->
			def temp = File.createTempFile("pmf", "", null)
			new BufferedInputStream(input).withStream { binput ->
				temp.withOutputStream { it << binput }
			}
			temp
		}
		try {
			action(cachedInputs)
		} finally {
			cachedInputs*.delete()
		}
	}
	
	PMFDocument readNamedStreams(Map<String, Closure> streamFactories) {
		def messages = [], readDocuments = [:], ignoredFiles = []
		
		streamFactories.each { name, streamFactory ->
			def fileExtension = (name =~ /.*?(?:\.(.*))?$/)[0][1].toLowerCase()
			def doc = fileTypeReaders[fileExtension]?.findResult { readerType ->
				def stream = streamFactory()
				readerType.newInstance().read(stream)
			}
			if(doc)
				readDocuments[name] = doc
			else ignoredFiles << name
		}
		
		new PMFDocument(experiments: readDocuments.findAll { it instanceof NuMLDocument }, 
			models: readDocuments.findAll { it instanceof SBMLDocument })
	}
}

class SBMLAdapter {
	SBMLReader reader = new SBMLReader()
	
	def read(InputStream stream) {
		def doc = reader.readSBMLFromStream(stream)
		doc.level == -1 ? null : doc
	}
}
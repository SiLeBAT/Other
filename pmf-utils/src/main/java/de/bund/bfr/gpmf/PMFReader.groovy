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

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.spi.LoggingEvent
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.SBMLException
import org.sbml.jsbml.SBMLReader
import org.sbml.jsbml.validator.SBMLValidator;

import de.bund.bfr.gnuml.ConformityMessage
import de.bund.bfr.gnuml.NuMLDocument
import de.bund.bfr.gnuml.NuMLReader

/**
 * 
 */
class PMFReader {
	static final fileTypeReaders = [sbml: SBMLAdapter, numl: NuMLReader, xml: [SBMLAdapter, NuMLReader]]
	
	private PMFDocument document
	
	private List<ConformityMessage> messages = []
	
	boolean validating = false
	
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
		def readDocuments = [:], ignoredFiles = [], documentStreamFactories = [:]
		this.messages = []
		this.document = null
		
		streamFactories.each { name, streamFactory ->
			def fileExtension = (name =~ /.*?(?:\.(.*))?$/)[0][1].toLowerCase()
			def validReader = fileTypeReaders[fileExtension]*.newInstance(validating: validating)?.find() { reader ->
				def stream = streamFactory()
				reader.read(stream)
			}
			if(validReader) {
				readDocuments[name] = validReader.document
				documentStreamFactories[validReader.document] = streamFactory
				messages.addAll(validReader.parseMessages.collect { it.message = "$name: " + it.message; it })
			}
			else ignoredFiles << name
		}
		
		if(readDocuments) {
			this.document = new PMFDocument(dataSets: readDocuments.findAll { it.value instanceof NuMLDocument }, 
				models: readDocuments.findAll { it.value instanceof SBMLDocument },
				documentStreamFactories: documentStreamFactories)
			messages = this.document.invalidSettings
		}
		this.document
	}
	
	List<ConformityMessage> getParseMessages(Level level = Level.WARN) {
		messages.grep { it.level.isGreaterOrEqual(level) }
	}
	
	/**
	 * Returns the document.
	 * 
	 * @return the document
	 */
	PMFDocument getDocument() {
		this.document
	}
	
	/**
	 * Returns the messages.
	 * 
	 * @return the messages
	 */
	List<ConformityMessage> getMessages() {
		this.messages
	}
}


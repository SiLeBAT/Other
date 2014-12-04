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

import java.nio.charset.Charset

import org.sbml.jsbml.SBMLDocument

import de.bund.bfr.gnuml.ConformityMessage
import de.bund.bfr.gnuml.NuMLDocument
import de.bund.bfr.gnuml.NuMLWriter

/**
 * 
 */
class PMFDocument {
	Map<String, SBMLDocument> models = new ObservableMap()
	Map<String, NuMLDocument> dataSets = new ObservableMap()
		
	PMFDocument() {
		models.addPropertyChangeListener({ models.each { PMFUtil.wrap(it.value) } })
		dataSets.addPropertyChangeListener({ dataSets.each { PMFUtil.wrap(it.value) } })
	}
	
	// mostly for additional validation (XPath evaluation etc)
	Map<Object, Closure<InputStream>> documentStreamFactories = [:]
	
	InputStream getInputStream(Object doc) {
		documentStreamFactories[doc] ?: createFallbackFactory(doc)
	}
	
	static final fileTypeWriters = [SBMLDocument: SBMLAdapter, NuMLDocument: NuMLWriter]
	
	def createFallbackFactory(Object doc) {
		def writer = fileTypeWriters[doc.class]
		def factory = {
			def xmlString = writer.toString(doc)
			new ByteArrayInputStream(xmlString.getBytes(Charset.forName("utf-8")))
		}
		factory
	}
	
	def resolve(String xlinkRef) {
		def doc = models[xlinkRef] ?: dataSets[xlinkRef]
		if(!doc)
			throw new IllegalArgumentException("Unknown document $xlinkRef")
		doc
	}
	
	/**
	 * Sets the dataSets to the specified value.
	 *
	 * @param dataSets the dataSets to set
	 */
	public void setDataSets(Map<String, NuMLDocument> dataSets) {
		if (dataSets == null)
			throw new NullPointerException("dataSets must not be null");

		this.dataSets.clear()
		this.dataSets.putAll(dataSets)
	}
	
	List<String> getInvalidSettings(String prefix = 'pmf') {
		dataSets.collect { name, numl -> numl.getInvalidSettings("$prefix/$name/numl") } +
		models.collect { name, sbml -> PMFUtil.getInvalidSettings(sbml, "$prefix/$name/sbml", this) }
	}
	
	/**
	 * Sets the models to the specified value.
	 *
	 * @param models the models to set
	 */
	public void setModels(Map<String, SBMLDocument> models) {
		if (models == null)
			throw new NullPointerException("models must not be null");
			
		this.models.clear()
		this.models.putAll(models)
	}
}

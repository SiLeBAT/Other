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
package de.bund.bfr.numl;

import java.util.logging.Level;

import org.xml.sax.InputSource;

import groovy.transform.EqualsAndHashCode
import groovy.xml.NamespaceBuilderSupport

/**
 * The root element of every NuML document. It contains a list of {@link OntologyTerm}s and {@link ResultComponent}s.
 */
@EqualsAndHashCode(callSuper = true)
class NuMLDocument extends NMBase {
	@Required
	int level = 1, version = 1
	
	final List<OntologyTerm> ontologyTerms = new ObservableList()
	
	final List<ResultComponent> resultComponents = new ObservableList()
		
	NuMLDocument() {
		this.elementName = 'numl'
		resultComponents.addPropertyChangeListener { resultComponents*.parent = this }
		ontologyTerms.addPropertyChangeListener { ontologyTerms*.parent = this ; resultComponents*.parent = this }
	}
	
	/**
	 * Sets the level to the specified value.
	 *
	 * @param level the level to set
	 */
	void setLevel(int level) {
		if (level < 1)
			throw new IllegalArgumentException("level must be > 0");

		this.level = level;
	}
	
	@Override
	NuMLDocument getDocument() {
		this
	}
	
	/**
	 * Sets the resultComponents to the specified value.
	 *
	 * @param resultComponents the resultComponents to set
	 */
	void setResultComponents(List<ResultComponent> resultComponents) {
		if (resultComponents == null)
			throw new NullPointerException("resultComponents must not be null");

		this.resultComponents.clear()
		this.resultComponents.addAll(resultComponents)
	}
	
	
	/**
	 * Sets the ontologyTerms to the specified value.
	 *
	 * @param ontologyTerms the ontologyTerms to set
	 */
	void setOntologyTerms(List<OntologyTerm> ontologyTerms) {
		if (ontologyTerms == null)
			throw new NullPointerException("ontologyTerms must not be null");
			
		this.ontologyTerms.clear()
		this.ontologyTerms.addAll(ontologyTerms)
	}
		
	/**
	 * Sets the version to the specified value.
	 *
	 * @param version the version to set
	 */
	void setVersion(int version) {
		if (version < 1)
			throw new IllegalArgumentException("version must be > 0");

		this.version = version;
	}
	
	boolean addOntologyTerm(OntologyTerm term) {
		// already existing; do nothing
		if(this.ontologyTerms.contains(term))
			return false
			
		if(term.id) {
			def sameId = this.ontologyTerms.find { term.id == it.id }
			if(sameId)
				throw new IllegalArgumentException("Another ontology with that id exists $sameId")
		} else {
			// increment number in label
			def lastId = this.ontologyTerms ? this.ontologyTerms.last().id : 'term0'
			def idParts = (lastId =~ /(.*)(\d+)/)[0]
			term.id = idParts[1] + (idParts[2].toInteger() + 1)
		}
		this.ontologyTerms.add(term)
		term.parent = this
	}
	
	/* (non-Javadoc)
	 * @see de.bund.bfr.numl.NMBase#getInvalidSettings()
	 */
	List<String> getInvalidSettings(String prefix = '') {
		def invalidSettings = []
		
		if(!ontologyTerms)
			invalidSettings << new ConformityMessage("$prefix/$elementName must have ontologyTerms section with at least one ontologyTerm")
			
		if(!resultComponents)
			invalidSettings << new ConformityMessage("$prefix/$elementName must have at least one resultComponent section")
			
		invalidSettings + super.getInvalidSettings(prefix)
	}
	
	@Override
	void setOriginalNode(Node node) {		
		super.setOriginalNode(node)
		
		setOntologyTerms(node.ontologyTerms?.ontologyTerm.collect { 
			new OntologyTerm(parent: this, originalNode: it) 
		})
				
		setResultComponents(node.resultComponent.collect { 
			new ResultComponent(parent: this, originalNode: it) 
		})
	}
	
	@Override
	def write(BuilderSupport builder) {
		def attributes = [version: this.version, level: this.level]
//		if(builder instanceof NamespaceBuilderSupport)
//			attributes += builder.nsMap.collectEntries { prefix, uri -> [prefix ? "xmlns:$prefix" : "xmlns", uri] }
		builder.numl(attributes) {		
			builder.ontologyTerms {
				this.ontologyTerms*.write(builder)
			}
			this.resultComponents*.write(builder)
		}
	}
}

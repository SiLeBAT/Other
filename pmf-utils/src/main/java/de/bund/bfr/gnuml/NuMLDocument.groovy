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
package de.bund.bfr.gnuml;

import java.util.List;

import groovy.util.Node;

/**
 * 
 */
public class NuMLDocument extends NMBase {
	@Required
	int level = 1, version = 1
	
	List<OntologyTerm> ontologyTerms = []
	
	List<ResultComponent> resultComponents = []
	
	NuMLDocument() {
		this.document = this
	}
	
	/**
	 * Sets the level to the specified value.
	 *
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		if (level < 1)
			throw new IllegalArgumentException("level must be > 0");

		this.level = level;
	}
	
	/**
	 * Sets the version to the specified value.
	 *
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		if (version == null)
			throw new IllegalArgumentException("version must be > 0");

		this.version = version;
	}
	
	/* (non-Javadoc)
	 * @see de.bund.bfr.gnuml.NMBase#getInvalidSettings()
	 */
	List<String> getInvalidSettings(String prefix = '') {
		def invalidSettings = []
		
		if(!ontologyTerms)
			invalidSettings << "$prefix Document must have ontologyTerms section with at least one ontologyTerm"
			
		if(!resultComponents)
			invalidSettings << "$prefix Document must have at least one resultComponent section"
			
		invalidSettings + super.getInvalidSettings(prefix)
	}
	
	/* (non-Javadoc)
	 * @see de.bund.bfr.gnuml.NMBase#setOriginalNode(groovy.util.Node)
	 */
	@Override
	public void setOriginalNode(Node node) {		
		super.setOriginalNode(node)
		
		ontologyTerms = node.ontologyTerms?.ontologyTerm.collect { 
			new OntologyTerm(document: this, originalNode: it) 
		}
				
		resultComponents = node.resultComponent.collect { 
			new ResultComponent(document: this, originalNode: it) 
		}
	}
}

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

import groovy.transform.AutoClone;
import groovy.transform.EqualsAndHashCode

/**
 * Vocabulary/term from standard ontology source to describe various types of data associated with the model
 */
@AutoClone
class OntologyTerm extends NMBase {
	@Required
	String id, term, sourceTermId
	@Required
	String ontologyURI
	
	void setOntologyURI(URI ontologyURI) {
		this.ontologyURI = ontologyURI?.toString()
	}

	URI getOntologyURI() {
		this.ontologyURI ? new URI(this.ontologyURI) : null
	}

	void setId(String id) {
		checkParamNMId(id, 'id')
		this.id = id
	}

	@Override
	public void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode)

		this.id = originalNode.'@id'
		this.term = originalNode.'@term'
		this.sourceTermId = originalNode.'@sourceTermId'
		this.ontologyURI = originalNode.'@ontologyURI'
	}

	@Override
	List<String> getInvalidSettings(String prefix) {
		def invalidSettings = []

		if(id && !isValidNMId(id))
			invalidSettings << "$prefix $id is not a valid NMId"

		if(this.ontologyURI)
			try {
				new URI(this.ontologyURI)
			} catch(e) {
				invalidSettings << "$prefix Invalid ontologyURI ${originalNode.'@ontologyURI'}"
			}

		invalidSettings + super.getInvalidSettings(prefix)
	}
}

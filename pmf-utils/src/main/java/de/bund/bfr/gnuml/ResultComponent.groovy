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
package de.bund.bfr.gnuml

import java.util.List;

import groovy.util.Node;

/**
 * 
 */
class ResultComponent extends NMBase {
	@Required
	String id
	@Required
	Object dimension

	String name
	@Required
	Descriptor descriptor

	@Override
	List<String> getInvalidSettings(String prefix) {
		def invalidSettings = []
		
		if(id && !isValidNMId(id))
			invalidSettings << "$prefix $id is not a valid NMId"
		
		invalidSettings + super.getInvalidSettings(prefix)
	}

	@Override
	public void setOriginalNode(Node originalNode) {		
		super.setOriginalNode(originalNode)
		
		this.id = originalNode.'@id'
		this.name = originalNode.'@name'

		def description = originalNode.dimensionDescription?.first()?.children()?.first()
		this.descriptor = null
		this.dimension = null
		if(description) {
			this.descriptor = Descriptor.fromDescription(this.document, description)
			if(originalNode.dimension?.first())
				this.dimension = descriptor.parse(originalNode.dimension?.first())
		}
	}
}

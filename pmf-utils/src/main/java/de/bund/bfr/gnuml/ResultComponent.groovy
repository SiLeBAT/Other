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

import groovy.util.Node;

/**
 * 
 */
class ResultComponent extends NMBase {
	@Required
	String id
	@Required
	Object dimensions
	
	String name
	
	Descriptor descriptor
	
	/* (non-Javadoc)
	 * @see de.bund.bfr.gnuml.NMBase#setOriginalNode(groovy.util.Node)
	 */
	@Override
	public void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode);
		
		this.id = originalNode.'@id'
		this.name = originalNode.'@name'
		
		descriptor = Descriptor.fromDescription(this.document, this.originalNode.dimensionDescription.first().children().first())
		dimensions = descriptor.parse(this.originalNode.dimension.first())
	}
}

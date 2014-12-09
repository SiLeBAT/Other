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
package de.bund.bfr.numl

import groovy.transform.EqualsAndHashCode

/**
 * The actual numerical result. The structure is described in {@link #dimensionDescription} and the values are stored in {@link #dimension}.
 */
@EqualsAndHashCode(callSuper = true)
class ResultComponent extends NMBase {
	@Required
	String id
	@Required
	Object dimension

	String name
	@Required
	Description dimensionDescription

	@Override
	List<String> getInvalidSettings(String prefix) {
		def invalidSettings = []

		if(id && !isValidNMId(id))
			invalidSettings << new ConformityMessage("$prefix $id is not a valid NMId")

		invalidSettings + super.getInvalidSettings(prefix)
	}

	@Override
	protected Map<String, Object> getAttributeValues() {
		def attributeValues = super.getAttributeValues()
		attributeValues.remove('dimension')
		attributeValues
	}

	@Override
	public void writeBody(BuilderSupport builder, Map subTypes) {
		builder.dimensionDescription { dimensionDescription.write(builder) }
		builder.dimension {
			dimensionDescription.writeData(builder, this.dimension)
		}
	}

	/**
	 * Sets the dimensionDescription to the specified value.
	 *
	 * @param dimensionDescription the dimensionDescription to set
	 */
	void setDimensionDescription(Description dimensionDescription) {
		if (dimensionDescription == null)
			throw new NullPointerException("dimensionDescription must not be null");

		this.dimensionDescription = dimensionDescription
		dimensionDescription.parent = this
	}


	@Override
	public void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode)

		this.id = originalNode.'@id'
		this.name = originalNode.'@name'

		def description = originalNode.dimensionDescription?.first()?.children()?.first()
		this.dimensionDescription = null
		this.dimension = null
		if(description) {
			setDimensionDescription(Description.fromNuML(this, description))
			if(originalNode.dimension?.first())
				this.dimension = dimensionDescription.parseData(originalNode.dimension?.first())
		}
	}
}

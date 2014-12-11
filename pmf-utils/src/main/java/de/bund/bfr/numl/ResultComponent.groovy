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

import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;

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
	List<ConformityMessage> getInvalidSettings(String prefix) {
		def invalidSettings = []

		if(id && !isValidNMId(id))
			invalidSettings << new ConformityMessage("$prefix $id is not a valid NMId")

		validateData("$prefix/dimension", dimensionDescription, dimension, invalidSettings)
			
		invalidSettings + super.getInvalidSettings(prefix)
	}
	
	@Override
	protected List<String> getIgnoredProperties() {
		super.getIgnoredProperties() + 'dimension'
	}

	@Override
	void writeBody(BuilderSupport builder) {
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
			
		if(dimension)
			validateData('', dimensionDescription, dimension)
		this.dimensionDescription = dimensionDescription
		dimensionDescription.parent = this
	}
	
	/**
	 * Sets the dimension to the specified value.
	 *
	 * @param dimension the dimension to set
	 */
	public void setDimension(Object dimension) {
		if (dimension == null)
			throw new NullPointerException("dimension must not be null");
			
		if(dimensionDescription)
			validateData('', dimensionDescription, dimension)
		this.dimension = dimension;
	}
	
	void validateData(String prefix, Description dimensionDescription, Object dimension, List<ConformityMessage> messages = null) {
		boolean validating = messages != null
		if(messages == null)
			messages = []
		dimensionDescription.validateData(prefix, dimension, messages)
		if(!validating && messages.grep { it.level.isGreaterOrEqual(Level.ERROR) })
			throw new NuMLException("Invalid dimension description for the set data").with { it.messages = messages ; it }
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

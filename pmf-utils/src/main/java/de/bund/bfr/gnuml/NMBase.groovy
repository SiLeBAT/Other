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

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * 
 */
class NMBase {
	String metaId
	
	List<Node> annotations = []
	
	List<Node> notes = []
	
	Node originalNode
	
	NuMLDocument document
	
	/**
	 * Sets the originalNode to the specified value and parses the relevant attributes and elements.
	 *
	 * @param originalNode the originalNode to set
	 */
	void setOriginalNode(Node originalNode) {
		if (originalNode == null)
			throw new NullPointerException("originalNode must not be null")

		this.originalNode = originalNode
		this.metaId = originalNode.'@metaid'
		this.notes = originalNode.notes*.first()
		this.annotations = originalNode.annotations*.first()
	}
	
	/**
	 * Sets the metaId to the specified value.
	 *
	 * @param metaId the metaId to set
	 */
	void setMetaId(String metaId) {
		checkParamNMId(metaId, 'metaId')

		this.metaId = metaId
	}
	
	void checkParamNMId(String id, String name) {
		if(id == null)
			throw new NullPointerException("$name must not be null")
		if(!isValidNMId(id))
			throw new IllegalArgumentException("$name $id is not a valid NMId")
	}
	
	boolean isValidNMId(String id) {
		id =~ /[\p{Alpha}_][\p{Alnum}_]*/
	}
	
	List<String> getInvalidSettings(String prefix) {
		def ignoredProperties = ['document']
		def properties = this.metaClass.properties.grep { !(it.name in ignoredProperties)  && it.setter }
		
		def requiredProps = properties.grep { it.field?.field?.getAnnotation(Required) }
		def invalidSettings = requiredProps.grep { it.getProperty(this) == null }.collect { 
			"$prefix Required value $it.name not set for $this"
		}
		
		if(metaId && !isValidNMId(metaId))
			invalidSettings << "$prefix metaId $metaId is not a valid NMId"
		
		def subTypes = properties.collect { it.getProperty(this) }.flatten().grep { it instanceof NMBase }
		def subInvalidSettings = subTypes.collect { it.getInvalidSettings("$prefix/${it.class.simpleName}") } 
		invalidSettings + subInvalidSettings.flatten()
	}
	
	String getName() {
		this.class.simpleName
	}
	
	String toString() {		
		def ignoredProperties = ['document', 'originalNode']
		def properties = this.metaClass.properties.grep { !(it.name in ignoredProperties) && it.setter}
		def propertyValues = properties.collectEntries { [(it.name): it.getProperty(this)] }
		def reversedNonNull = propertyValues.grep { it.value }
		"$name ${reversedNonNull}"
	}
}

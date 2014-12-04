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

import groovy.transform.EqualsAndHashCode

/**
 * 
 */
@EqualsAndHashCode(includes = ['metaId', 'annotations', 'notes'])
class NMBase {
	String metaId
	
	Node annotations
	
	Node notes
	
	NMBase parent
	
	protected transient Node originalNode
	
	protected transient elementName = this.class.simpleName[0].toLowerCase() + this.class.simpleName[1..-1] 
	
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
		this.notes = originalNode.notes[0]
		this.annotations = originalNode.annotations[0]
	}
	
	List<NMBase> getChildren() {
		def propertyValues = this.attributeValues
		def nonNull = propertyValues*.value.findAll{ it }.findAll { 
			it instanceof NMBase || (it instanceof Collection && it[0] instanceof NMBase )
		}.flatten()
	}
	
	NuMLDocument getDocument() {
		parent?.document
	}
	
	protected Map<String, Object> getAttributeValues() {		
		def ignoredProperties = ['document', 'parent', 'originalNode']
		def properties = this.metaClass.properties.grep { !(it.name in ignoredProperties)  && it.setter }
		properties.collectEntries { [(it.name): it.getProperty(this)] }
	}
	
	void write(BuilderSupport builder) {
		def propertyValues = this.attributeValues
		def nonNull = propertyValues.findAll { it.value }
		def children = nonNull.findAll { it.value instanceof NMBase || (it.value instanceof Collection && it.value[0] instanceof NMBase ) }
		def attributes = nonNull - children
		
		builder.invokeMethod(this.elementName, [attributes, {
			if(this.notes)
				builder.append(this.notes)
			if(this.annotations)
				builder.append(this.annotations)
			writeBody(builder, children)
		}])
	}
	
	void writeBody(BuilderSupport builder, Map children) {
		children.each { key, value ->
			value*.write(builder)
		}
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
	
//	void setDocument(NuMLDocument document) {
//		if(!this.document.is(document)) {
//			this.document = document
//			
//			def subTypes = this.attributeValues*.value.flatten().grep { it instanceof NMBase }	
//			subTypes*.document = document			
//		}
//	}
	
	List<ConformityMessage> getInvalidSettings(String prefix = '') {
		def ignoredProperties = ['document', 'parent']
		def properties = this.metaClass.properties.grep { !(it.name in ignoredProperties)  && it.setter }
		
		def requiredProps = properties.grep { it.field?.field?.getAnnotation(Required) }
		def invalidSettings = requiredProps.grep { it.getProperty(this) == null }.collect { 
			new ConformityMessage("$prefix Required value $it.name not set for $this")
		}
		
		if(metaId && !isValidNMId(metaId))
			invalidSettings << new ConformityMessage("$prefix metaId $metaId is not a valid NMId")
		
		def subTypes = properties.collect { it.getProperty(this) }.flatten().grep { it instanceof NMBase }
		def subInvalidSettings = subTypes.collect { it.getInvalidSettings("$prefix/${it.class.simpleName}") } 
		invalidSettings + subInvalidSettings.flatten()
	}
		
	String toString() {		
		def ignoredProperties = ['parent', 'document', 'originalNode']
		def properties = this.metaClass.properties.grep { !(it.name in ignoredProperties)  && it.setter }
		def propertyValues = properties.collectEntries { [(it.name): it.getProperty(this)] }
		def nonNull = propertyValues.findAll { it.value }.collectEntries { key, value ->
			[(key): value instanceof ObservableList ? value as ArrayList : value]
		}
		"<${this.elementName} ${nonNull}>"
	}
}

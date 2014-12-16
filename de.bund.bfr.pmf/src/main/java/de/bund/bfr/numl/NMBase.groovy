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

import groovy.transform.AutoClone;
import groovy.xml.QName

/**
 * Base class for NuML types according to NuML specification.
 */
class NMBase {
	final static String NUML_NS = 'http://www.numl.org/numl/level1/version1'
	
	/**
	 * The metaid attribute is present for supporting metadata annotations using RDF
	 */
	String metaId
	
	/**
	 * Represents a container element for optional software-generated content not meant to be shown to humans.
	 */
	Node annotation = new Node(null, new QName(NUML_NS, 'annotation'))
	
	/**
	 * Represents a container element for XHTML 1.0.
	 */
	Node notes = new Node(null, new QName(NUML_NS, 'notes'))
	
	NMBase parent
	
	/**
	 * Original XML node returned from parser.
	 */
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
		this.notes = originalNode.notes[0] ?: new Node(originalNode, new QName(NUML_NS, 'notes'))
		this.annotation = originalNode.annotation[0] ?: new Node(originalNode, new QName(NUML_NS, 'annotation'))
	}
	
	/**
	 * Returns all NuML children.
	 */
	Map<String, NMBase> getNamedChildren() {
		def propertyValues = this.propertyValues
		def nonNull = propertyValues.findAll { 
			it.value instanceof NMBase || (it.value instanceof Collection && it.value[0] instanceof NMBase )
		}
	}
	
	List<NMBase> getChildren() {
		this.namedChildren*.value.flatten()
	}
	
	NuMLDocument getDocument() {
		parent?.document
	}
	
	protected List<String> getIgnoredProperties() {
		['document', 'parent', 'originalNode', 'notes', 'annotation']
	}
	
	protected Map<String, Object> getPropertyValues(MetaClass metaClass = this.metaClass) {
		def ignoredProperties = this.ignoredProperties
		def properties = metaClass.properties.grep { !(it.name in ignoredProperties)  && it.setter }
		properties.collectEntries { [(it.name): it.getProperty(this)] }
	}
		
	protected Map<String, Object> getAttributeValues() {		
		this.propertyValues - namedChildren
	}
	
	/**
	 * Writes the element using the provided builder.
	 */
	def write(BuilderSupport builder) {		
		builder.invokeMethod(this.elementName, [attributeValues.findAll { it.value }, {
			if(this.notes?.value())
				builder.current.append(this.notes)
			if(this.annotation?.value()) {
				builder.current.append(this.annotation)
			}
			writeBody(builder)
		}])
	}
	
	void writeBody(BuilderSupport builder) {
		namedChildren.each { key, value ->
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
	
	void setParent(NMBase parent) {
		if (parent == null)
			throw new NullPointerException("parent must not be null");

		if(!this.parent.is(this.parent = parent))
			ancestoryChanged()
	}
	
	protected void ancestoryChanged() {
		children*.ancestoryChanged()
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
	
	/**
	 * Returns a list of warnings and errors for the current settings.
	 */
	List<ConformityMessage> getInvalidSettings(String prefix = '') {
		def ignoredProperties = ['document', 'parent']
		def properties = this.metaClass.properties.grep { !(it.name in ignoredProperties)  && it.setter }
		
		def requiredProps = properties.grep { it.field?.field?.getAnnotation(Required) }
		def invalidSettings = requiredProps.grep { it.getProperty(this) == null }.collect { 
			new ConformityMessage("$prefix Required value $it.name not set for $localXPath (parent ${parent?.localXPath})")
		}
		
		if(metaId && !isValidNMId(metaId))
			invalidSettings << new ConformityMessage("$prefix metaId $metaId is not a valid NMId")
		
		def subInvalidSettings = this.children.collect { 
			it.getInvalidSettings("$prefix/$elementName") 
		} 
		invalidSettings + subInvalidSettings.flatten()
	}
	
	String getLocalXPath() {		
		def thisSelector = hasProperty('id') && id ? "[id='$id']" : ''
		"$elementName$thisSelector"
	}
	
	String getXPath() {
		def parentXPath = parent?.XPath ?: ''
		"$parentXPath/$localXPath"
	}
		
	String toString() {		
		def propertyValues = this.propertyValues
		def nonNull = propertyValues.findAll { it.value }.collectEntries { key, value ->
			[(key): value instanceof ObservableList ? value as ArrayList : value]
		}
		"<${this.elementName} ${nonNull}>"
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		def properties = [notes: notes, annotation: annotation] + this.propertyValues
		properties.inject(prime) { result, property -> 
			prime * result + (property.value?.hashCode() ?: 0)
		}
	}

	public Map<String, Object> getMismatchedProperties(Object obj) {
		if (!getClass().is(obj.getClass()))
			throw new IllegalArgumentException()
			
		def thisProperties = this.propertyValues 
		def thatProperties = obj.propertyValues
		
		def mismatchedProperties = thisProperties.findAll { name, value ->
			thatProperties[name] != value
		}.collectEntries { name, value ->
			[(name): [value, thatProperties[name]]]
		}
		
		if(!NodeUtil.isEqual(this.notes, obj.notes))
			mismatchedProperties['notes'] = [this.notes, obj.notes]
			
		if(!NodeUtil.isEqual(this.annotation, obj.annotation))
			mismatchedProperties['annotation'] = [this.annotation, obj.annotation]
			
		mismatchedProperties
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this.is(obj))
			return true;
		if (obj.is(null))
			return false;
		if (!getClass().is(obj.getClass()))
			return false;
		def mismatched = getMismatchedProperties(obj)
		!mismatched
	}
	
	
}

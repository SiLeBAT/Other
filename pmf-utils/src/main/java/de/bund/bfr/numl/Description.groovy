/*******************************************************************************
 * Copyright (c) 2014 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General License for more details.
 *
 * You should have received a copy of the GNU General License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.bund.bfr.numl;

import groovy.transform.EqualsAndHashCode

import java.text.*
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathFactory


abstract class Description extends NMBase {
	OntologyTerm ontologyTerm

	String id

	protected parseDataErrors = []

	abstract void writeData(BuilderSupport builder, Object data)
	
	abstract void validateData(String prefix, Object data, List<ConformityMessage> messages)
	
	abstract Object parseData(Node node)

	final static NodeMapping = [
		compositeDescription: CompositeDescription,
		tupleDescription: TupleDescription,
		atomicDescription: AtomicDescription
	]

	static Description fromNuML(NMBase parent, Node description) {
		// We assume the correct namespace here, because the document is validated with the schema separately
		Class descriptionClass = NodeMapping[description.name().localPart]
		descriptionClass.newInstance([parent: parent, originalNode: description])
	}

	void setId(String id) {
		checkParamNMId(id, 'id')
		this.id = id
	}
	
	/* (non-Javadoc)
	 * @see de.bund.bfr.numl.NMBase#getAttributeValues()
	 */
	@Override
	protected Map<String, Object> getAttributeValues() {
		// overwrite ontology term with its id
		def attributeValues = super.getAttributeValues()
		if(ontologyTerm)
			attributeValues['ontologyTerm'] = this.ontologyTerm.id
		attributeValues
	}
	
	@Override
	Map<String, NMBase> getNamedChildren() {
		def children = super.getNamedChildren()
		children.remove('ontologyTerm')
		children
	}
	
	@Override
	List<ConformityMessage> getInvalidSettings(String prefix) {
		// if the ontology term is not registered in document, we cannot really check other values
		if((ontologyTerm || this.originalNode?.'@ontologyTerm') && document && !(ontologyTerm in document.ontologyTerms))
			return [new ConformityMessage(
				"$prefix Ontology term ${ontologyTerm?.id ?: originalNode.'@ontologyTerm'} not registered in document; available: ${document.ontologyTerms*.id}")]
		def invalidSettings = []

		if(id && !isValidNMId(id))
			invalidSettings << new ConformityMessage("$prefix id $id is not a valid NMId")

		invalidSettings + 
			parseDataErrors.collect { it.message = "$prefix: ${it.message}"; it } + 
			super.getInvalidSettings(prefix)
	}
	
	ResultComponent getResultComponent() {
		parent instanceof ResultComponent ? parent : parent?.resultComponent
	}
	
	@Override
	protected void ancestoryChanged() {
		if(ontologyTerm)
			document?.addOntologyTerm(ontologyTerm)
		super.ancestoryChanged()
	}

	void setOntologyTerm(OntologyTerm ontologyTerm) {
		if(ontologyTerm && document && !(ontologyTerm in document.ontologyTerms))
			document.addOntologyTerm(ontologyTerm)
		this.ontologyTerm = ontologyTerm
	}

	@Override
	void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode)

		this.ontologyTerm = document.ontologyTerms.find { it.id == originalNode.'@ontologyTerm' }
		this.id = originalNode.'@id'
	}
}

enum DataType {
	String("string") {
		def parseData(String value) { value	}
		void writeData(BuilderSupport builder, Object value) { builder.atomicValue(value) }
		boolean isValid(Object value) { value instanceof String }		
	},
	XPath("xpath") {
		javax.xml.xpath.XPath xpath = XPathFactory.newInstance().newXPath()
		// we valid the xpath expression, but still return the string, because XPathExpression is not human-readable anymore
		def parseData(String value) { xpath.compile(value) ; value }
		void writeData(BuilderSupport builder, Object value) { builder.atomicValue(value) }
		boolean isValid(Object value) { value instanceof String }
	},
	Float("float") {
//		def format = new DecimalFormat('0.#######E0', DecimalFormatSymbols.getInstance(Locale.US))
//		void writeData(BuilderSupport builder, Object value) { builder.atomicValue(format.format(value)) }
		def parseData(String value) { java.lang.Float.valueOf(value) }
		void writeData(BuilderSupport builder, Object value) { builder.atomicValue(value.floatValue()) }
		boolean isValid(Object value) { value instanceof Number }
	},
	Double("double") {
//		def format = new DecimalFormat('0.###############E0', DecimalFormatSymbols.getInstance(Locale.US))
//		void writeData(BuilderSupport builder, Object value) { builder.atomicValue(format.format(value)) }
		def parseData(String value) { java.lang.Double.valueOf(value) }
		void writeData(BuilderSupport builder, Object value) { builder.atomicValue(value.doubleValue()) }
		boolean isValid(Object value) { value instanceof Number }
	},
	Integer("integer") {
		def parseData(String value) { new BigInteger(value) }
		void writeData(BuilderSupport builder, Object value) { builder.atomicValue(value) }
		boolean isValid(Object value) { value instanceof Number && value.class in [java.lang.Integer, Long, BigInteger] }
	};

	java.lang.String numlName

	abstract Object parseData(String value);
	
	abstract void writeData(BuilderSupport builder, Object value);
	
	abstract boolean isValid(Object value);

	def DataType(java.lang.String numlName) {
		this.numlName = numlName
	}
	
	String toString() {
		this.numlName
	}

	static DataType byNUMLName(String numlName) {
		values().find { it.numlName == numlName }
	}
}

class AtomicDescription extends Description {
	@Required
	String name
	@Required
	DataType valueType

	@Override
	Object parseData(Node node) {
		try {
			return valueType.parseData(node.text())
		} catch(e) {
			parseDataErrors << new ConformityMessage("Unable to parse ${node.text()} of expected type $valueType in $name")
		}
	}

	@Override
	void writeData(BuilderSupport builder, Object data) {
		valueType.writeData(builder, data)
	}
	
	void validateData(String prefix, Object data, List<ConformityMessage> messages) {
		if(data.is(null) || !valueType.isValid(data))
			messages << new ConformityMessage("$prefix/$name: invalid data ${data} of type $data?.class to expected type $valueType in $name")
	}

	@Override
	void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode)

		valueType = DataType.byNUMLName(originalNode.'@valueType')
		name = originalNode.'@name'
	}
}

class CompositeDescription extends Description {
	@Required
	String name
	@Required
	DataType indexType

	Description description

	Object parseData(Node node) {
		def values = [:]
		node.each { child ->
			if(child.'@indexValue') {
				try {
					values[indexType.parseData(child.'@indexValue')] = description.parseData(child)
				} catch(NumberFormatException e) {
					parseDataErrors << new ConformityMessage(
						"Unable to parse index value ${child.'@indexValue'} of expected type $indexType in $name " + e)
				}
			}
			else parseDataErrors << new ConformityMessage(
				"Index value not found at $parent.elementName $parent.id of expected type $indexType in $name")
		}
		values
	}

	@Override
	void writeData(BuilderSupport builder, Object data) {
		data.each { key, value ->
			builder.compositeValue(indexValue: key) {
				description.writeData(builder, value)
			}
		}
	}

	@Override
	void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode)

		name = originalNode.'@name'
		indexType = DataType.byNUMLName(originalNode.'@indexType')
		setDescription(Description.fromNuML(this, originalNode.children().find { Node child ->
			child.name().localPart.endsWith('Description') 
		}))
	}
	
	void validateData(String prefix, Object data, List<ConformityMessage> messages) {
		if(data.is(null) || !(data instanceof Map))
			messages << new ConformityMessage("$prefix/$name: invalid data ${data} of type $data?.class to expected type Map in $name")
		else 
			data.each { key, value ->
				description.validateData("$prefix/$key", value, messages)
			}
	}
	
	/**
	 * Sets the description to the specified value.
	 *
	 * @param description the description to set
	 */
	void setDescription(Description description) {
		if (description == null)
			throw new NullPointerException("description must not be null");

		this.description = description
		this.description.parent = this
	}
}

class TupleDescription extends Description {
	String name

	final List<Description> descriptions = new ObservableList()

	TupleDescription() {
		descriptions.addPropertyChangeListener({ descriptions*.parent = this })
	}
	
	@Override
	Object parseData(Node node) {
		def children = node.tuple.first().children()
		if(descriptions.size() != children.size())
			parseDataErrors << new ConformityMessage("Expected ${descriptions.size()} children in tuple with parent ${node.name()} ${node.attributes()}, but encountered ${children.size()}")

		def values = []
		children.eachWithIndex { child, index ->
			values << descriptions[index]?.parseData(child)
		}
		values
	}

	@Override
	void writeData(BuilderSupport builder, Object data) {
		builder.tuple() {
			descriptions.eachWithIndex { description, index ->
				description.writeData(builder, data[index])
			}
		}
	}
	
	void validateData(String prefix, Object data, List<ConformityMessage> messages) {		
		if(descriptions.size() != data.size())
			messages << new ConformityMessage("$prefix: Expected ${descriptions.size()} children in tuple with parent ${parent?.elementName} ${parent?.id}, but encountered ${data.size()}")
		
		(0..<Math.min(descriptions.size(), data.size())).each { index ->
			descriptions[index].validateData("$prefix/tupleValue", data[index], messages)
		}
	}
	
	/**
	 * Sets the descriptions to the specified value.
	 *
	 * @param descriptions the descriptions to set
	 */
	void setDescriptions(List<Description> descriptions) {
		if (descriptions == null)
			throw new NullPointerException("descriptions must not be null");

		this.descriptions.clear()
		this.descriptions.addAll(descriptions)
	}
		
	@Override
	void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode)

		name = originalNode.'@name'
		setDescriptions(originalNode.findAll { 
			it.name().localPart.endsWith('Description') 
		}.collect { Description.fromNuML(this, it) })
	}
}


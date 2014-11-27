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
package de.bund.bfr.gnuml;

import java.util.Map;

import groovy.transform.EqualsAndHashCode

import javax.xml.xpath.XPathFactory


@EqualsAndHashCode(callSuper = true, includes = ['ontologyTerm', 'id'])
abstract class Description extends NMBase {
	OntologyTerm ontologyTerm

	String id

	protected parseDataErrors = []

	abstract void writeData(BuilderSupport builder, Object data)

	abstract Object parseData(Node node)

	final static NodeMapping = [
		compositeDescription: CompositeDescription,
		tupleDescription: TupleDescription,
		atomicDescription: AtomicDescription
	]

	static Description fromNuML(NuMLDocument document, Node description) {
		// We assume the correct namespace here, because the document is validated with the schema separately
		Class descriptionClass = NodeMapping[description.name().localPart]
		descriptionClass.newInstance([document: document, originalNode: description])
	}

	void setId(String id) {
		checkParamNMId(id, 'id')
		this.id = id
	}
	
	/* (non-Javadoc)
	 * @see de.bund.bfr.gnuml.NMBase#getAttributeValues()
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
	List<String> getInvalidSettings(String prefix) {
		// if the ontology term is not registered in document, we cannot really check other values
		if(ontologyTerm && document && !(ontologyTerm in document.ontologyTerms))
			return [
				"$prefix Ontology term $ontologyTerm.id not registered in document; available: ${document.ontologyTerms*.id}"]
		def invalidSettings = []

		if(id && !isValidNMId(id))
			invalidSettings << "$prefix id $id is not a valid NMId"

		invalidSettings + super.getInvalidSettings(prefix)
	}

	OntologyTerm getOntologyTerm() {
		ontologyTerm
	}

	void setDocument(NuMLDocument document) {
		super.setDocument(document)
		if(ontologyTerm)
			document.addOntologyTerm(ontologyTerm)
	}

	void setOntologyTerm(OntologyTerm ontologyTerm) {
		if(ontologyTerm && document && !(ontologyTerm in document.ontologyTerms))
			document.addOntologyTerm(ontologyTerm)
		this.ontologyTerm = ontologyTerm
	}

	@Override
	void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode)

		if(originalNode.'@ontologyTerm')
			this.ontologyTerm = document.ontologyTerms.find { it.id == originalNode.'@ontologyTerm' } ?:
			new OntologyTerm(id: originalNode.'@ontologyTerm')
		else this.ontologyTerm = null
		this.id = originalNode.'@id'
	}
}

enum DataType {
	String("string") {
		def parseData(String value) {
			value
		}
	},
	XPath("xpath") {
		javax.xml.xpath.XPath xpath = XPathFactory.newInstance().newXPath()
		// we valid the xpath expression, but still return the string, because XPathExpression is not human-readable anymore
		def parseData(String value) { xpath.compile(value) ; value }
	},
	Float("float") {
		def parseData(String value) { java.lang.Float.valueOf(value) }
	},
	Double("double") {
		def parseData(String value) { java.lang.Double.valueOf(value) }
	},
	Integer("integer") {
		def parseData(String value) { java.lang.Integer.valueOf(value) }
	};

	java.lang.String numlName

	abstract Object parseData(String value);

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

@EqualsAndHashCode(callSuper = true)
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
			parseDataErrors << "Unable to parseData ${node.text()} of expected type $indexType in $name"
		}
	}

	@Override
	void writeData(BuilderSupport builder, Object data) {
		builder.atomicValue(data)
	}

	@Override
	void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode)

		valueType = DataType.byNUMLName(originalNode.'@valueType')
		name = originalNode.'@name'
	}
}

@EqualsAndHashCode(callSuper = true)
class CompositeDescription extends Description {
	@Required
	String name
	@Required
	DataType indexType

	Description description

	Object parseData(Node node) {
		def values = [:]
		node.each { child ->
			try {
				values[indexType.parseData(child.'@indexValue')] = description.parseData(child)
			} catch(e) {
				parseDataErrors << "Unable to parseData index value ${child.'@indexValue'?.text()} of expected type $indexType in $name"
			}
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
		description = Description.fromNuML(document, originalNode.children().first())
	}
}

@EqualsAndHashCode(callSuper = true)
class TupleDescription extends Description {
	String name

	List<Description> descriptions = []

	@Override
	Object parseData(Node node) {
		def children = node.children()
		if(descriptions.size() != children.size())
			parseDataErrors << "Expected ${descriptions.size()} children, but encountered ${children.size()}"

		def values = []
		children.eachWithIndex { child, index ->
			values << descriptions[index]?.parseData(child)
		}
		values
	}

	@Override
	void writeData(BuilderSupport builder, Object data) {
		data.eachWithIndex { value, index ->
			descriptions[index].writeData(builder, value)
		}
	}

	@Override
	void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode)

		name = originalNode.'@name'
		descriptions = originalNode.collect { Description.fromNuML(document, it) }
	}
}


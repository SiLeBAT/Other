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
package de.bund.bfr.gnuml;

import java.util.List;

import javax.xml.xpath.XPathFactory

abstract class Descriptor extends NMBase {	
	String ontologyTermRef
	
	String id
	
	protected parseErrors = []
	
	abstract Object parse(Node node)
	
	final static NodeMapping = [
		compositeDescription: CompositeDescriptor,
		tupleDescription: TupleDescriptor,
		atomicDescription: AtomicDescriptor
	]
	
	static Descriptor fromDescription(NuMLDocument document, Node description) {
		// We assume the correct namespace here, because the document is validated with the schema separately
		Class descriptorClass = NodeMapping[description.name().localPart]
		descriptorClass.newInstance([document: document, originalNode: description])
	}
	
	@Override	
	List<String> getInvalidSettings(String prefix) {
		def invalidSettings = []
		
		if(id && !isValidNMId(id))
			invalidSettings << "$prefix id $id is not a valid NMId"
			
		if(ontologyTerm && document && !(ontologyTerm in document.ontologyTerms))
			invalidSettings << "$prefix Ontology term not registered in document; available: ${document.ontologyTerms*.id}" 
			
		if(ontologyTermRef && !ontologyTerm)
			invalidSettings << "$prefix Unknown ontology ID ${ontologyTermRef}; available: ${document.ontologyTerms*.id}"
			
		invalidSettings + super.getInvalidSettings(prefix)
	}
	
	OntologyTerm getOntologyTerm() {
		ontologyTermRef ? document.ontologyTerms.find { it.id == ontologyTermRef } : null
	}
	
	void setOntologyTerm(OntologyTerm ontologyTerm) {
		this.ontologyTermRef = ontologyTerm?.id
		if(ontologyTerm && !(ontologyTerm in document.ontologyTerms))
			document.ontologyTerms << ontologyTerm
	}
	
	@Override
	public void setOriginalNode(Node originalNode) {		
		super.setOriginalNode(originalNode)
		
		this.ontologyTermRef = originalNode.'@ontologyTerm'
		this.id = originalNode.'@id'
	}
}

enum DataType {
	String("string") {
		def parse(String value) { value }
	}, 
	XPath("xpath") {
		javax.xml.xpath.XPath xpath = XPathFactory.newInstance().newXPath()
		def parse(String value) { xpath.compile(value) }
	}, 
    Float("float") {
		def parse(String value) { java.lang.Float.valueOf(value) }		
	}, 
    Double("double") {
		def parse(String value) { java.lang.Double.valueOf(value) }		
	}, 
    Integer("integer") {
		def parse(String value) { java.lang.Integer.valueOf(value) }		
	};
	
	java.lang.String numlName
	
	public abstract Object parse(String value);
	
	def DataType(java.lang.String numlName) {
		this.numlName = numlName
	}
	
	static DataType byNUMLName(String numlName) {
		def type = values().find { it.numlName == numlName }
		if(type == null)
			throw new NuMLException("Unknown data type $numlName")
		type
	}
}

class AtomicDescriptor extends Descriptor {	
	@Required
	String name
	@Required
	DataType indexType
	
	@Override
	public Object parse(Node node) {
		try {
			return indexType.parse(node.text())
		} catch(e) {
			parseErrors << "Unable to parse ${node.text()} of expected type $indexType in $name"
		}
	}
	
	@Override
	public void setOriginalNode(Node originalNode) {		
		super.setOriginalNode(originalNode)
		
		indexType = DataType.byNUMLName(originalNode.'@valueType')
		name = originalNode.'@name'
	}
}

class CompositeDescriptor extends Descriptor {
	@Required
	String name
	@Required
	DataType indexType
	
	Descriptor descriptor
	
	public Object parse(Node node) {
		def values = [:]
		node.each { child ->
			try {
				values[indexType.parse(child.'@indexValue')] = descriptor.parse(child)
			} catch(e) {
				parseErrors << "Unable to parse index value ${child.'@indexValue'?.text()} of expected type $indexType in $name"
			}
		}
		values
	}
	
	@Override
	public void setOriginalNode(Node originalNode) {		
		super.setOriginalNode(originalNode)
		
		name = originalNode.'@name'
		indexType = DataType.byNUMLName(originalNode.'@indexType')
		descriptor = Descriptor.fromDescription(document, originalNode.children().first())	
	}
}

class TupleDescriptor extends Descriptor {
	String name	
	
	List<Descriptor> descriptors = []
	
	@Override
	public Object parse(Node node) {
		def children = node.children()
		if(descriptors.size() != children.size())
			parseErrors << "Expected ${descriptors.size()} children, but encountered ${children.size()}"
			
		def values = []
		children.eachWithIndex { child, index ->
			values << descriptors[index]?.parse(child)
		}		
		values
	}
	
	@Override
	public void setOriginalNode(Node originalNode) {		
		super.setOriginalNode(originalNode)
		
		name = originalNode.'@name'
		descriptors = originalNode.collect { Descriptor.fromDescription(document, it) }
	}
}


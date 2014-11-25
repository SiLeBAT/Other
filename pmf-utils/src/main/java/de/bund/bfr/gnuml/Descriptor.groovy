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

import javax.xml.xpath.XPathFactory

abstract class Descriptor extends NMBase {	
	OntologyTerm ontologyTerm
	String id
	
	abstract Object parse(Node node)
	
	static NodeMapping = [
		compositeDescription: CompositeDescriptor,
		tupleDescription: TupleDescriptor,
		atomicDescription: AtomicDescriptor
	]
	
	static Descriptor fromDescription(NUMLDocument document, Node description) {
		//TODO: assert namespace
		Class descriptorClass = NodeMapping[description.name().localPart]
		descriptorClass.newInstance([document: document, originalNode: description])
	}
	
	@Override
	public void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode);
		
		def ontologyRef = originalNode.'@ontologyTerm'
		ontologyTerm = document.ontologyTerms.find { it.id == ontologyRef }
		id = originalNode.'@id'
	}
	
	protected Map<String, Object> collectPropertyValues() {
		def values = super.collectPropertyValues()
		['ontologyTerm', 'id'].each { attr ->
			values[attr] = this."$attr"
		}
		values
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
		values().find { it.numlName == numlName }
	}
}

class AtomicDescriptor extends Descriptor {	
	@Required
	String name
	@Required
	DataType indexType
	
	@Override
	public Object parse(Node node) {
		indexType.parse(node.text())
	}
	
	@Override
	public void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode);
		
		indexType = DataType.byNUMLName(originalNode.'@valueType')
		name = originalNode.'@name'
	}
	
	protected Map<String, Object> collectPropertyValues() {
		def values = super.collectPropertyValues()
		['name', 'indexType'].each { attr ->
			values[attr] = this."$attr"
		}
		values
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
			values[indexType.parse(child.'@indexValue')] = descriptor.parse(child)
		}
		values
	}
	
	@Override
	public void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode);
		
		name = originalNode.'@name'
		indexType = DataType.byNUMLName(originalNode.'@indexType')
		descriptor = Descriptor.fromDescription(document, originalNode.children().first())
	}
	
	protected Map<String, Object> collectPropertyValues() {
		def values = super.collectPropertyValues()
		['name', 'indexType', 'descriptor'].each { attr ->
			values[attr] = this."$attr"
		}
		values
	}
}

class TupleDescriptor extends Descriptor {
	String name	
	
	List<Descriptor> descriptors = []
	
	/* (non-Javadoc)
	 * @see de.bund.bfr.gnuml.Descriptor#parse(groovy.util.Node)
	 */
	@Override
	public Object parse(Node node) {
		def values = []
		node.eachWithIndex { child, index ->
			values << descriptors[index].parse(child)
		}
		values
	}
	
	@Override
	public void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode);
		
		name = originalNode.'@name'
		descriptors = originalNode.collect { Descriptor.fromDescription(document, it) }
	}
	
	protected Map<String, Object> collectPropertyValues() {
		def values = super.collectPropertyValues()
		['name', 'descriptors'].each { attr ->
			values[attr] = this."$attr"
		}
		values
	}
}


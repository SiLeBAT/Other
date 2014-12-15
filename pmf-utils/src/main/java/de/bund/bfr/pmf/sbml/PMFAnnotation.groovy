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
package de.bund.bfr.pmf.sbml

import groovy.transform.InheritConstructors

import java.awt.Image;
import java.text.MessageFormat
import java.util.List;

import javax.swing.tree.TreeNode

import org.sbml.jsbml.AbstractTreeNode
import org.sbml.jsbml.Annotation
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.xml.XMLNode
import org.sbml.jsbml.xml.XMLToken

import de.bund.bfr.numl.ConformityMessage;
import de.bund.bfr.pmf.PMFDocument;

/**
 * Adds non-rdf nodes to child list.
 */
@InheritConstructors
class PMFAnnotation extends Annotation implements SBMLReplacement {
	PMFAnnotation(Annotation annotation) {
		super(annotation)
		
		this.nonRDFannotation?.parent = this
	}
	
	void replace(annotation) {
		annotation.parent.annotation = this
	} 
	
	/* (non-Javadoc)
	 * @see org.sbml.jsbml.Annotation#hashCode()
	 */
	@Override
	public int hashCode() {
		31
	}
	
	@Override
	public boolean equals(Object object) {
		object?.class == this.class
	}
//	@Override
//	int getChildCount() {
//		(this.setHistory ? 1 : 0) + (this.setListOfCVTerms ? 1 : 0) + (this.setNonRDFannotation ? 1 : 0)
//	}	
//
//	@Override
//	TreeNode getChildAt(int childIndex) {
//		if (childIndex < 0) {
//			throw new IndexOutOfBoundsException(childIndex + " < 0");
//		}
//		def childNames = [
//			'history',
//			'listOfCVTerms',
//			'nonRDFannotation'
//		]
//		def setChildren = childNames.findAll { this."set${it.capitalize()}" }.collect { this."$it" }
//
//		if(setChildren[childIndex])
//			return setChildren[childIndex]
//
//		throw new IndexOutOfBoundsException(MessageFormat.format(
//		"Index {0,number,integer} >= {1,number,integer}",
//		setChildren, +Math.min(children.size(), 0)))
//	}
}
//
///**
// * Fixes {@link #equals(Object)} and {@link #hashCode()} for characters.
// */
//@InheritConstructors
//class PMFXMLNode extends XMLNode implements SBMLReplacement {
//	static List<String> importantProperties = [
//		'attributes',
//		'column',
//		'isEOF',
//		'isEndElement',
//		'isStartElement',
//		'isText',
//		'line',
//		'namespaces',
//		'triple',
//		// it is sufficient to add it to the general fields, because the property is a String (and not a StringBuilder)
//		'characters'
//	]
//	
//	PMFXMLNode(XMLNode orig) {
//		super(orig)
//		
//		(0..<this.childCount).each { index ->
//			getChild(index).parent = this
//		}
//	}
//	
//	XMLNode clone() {
//		new PMFXMLNode(this)
//	}
//
//	@Override
//	boolean equals(Object object) {
//		// Check if the given object is a pointer to precisely the same object:
//		if (this.is(object))
//			return true
//
//		// Check if the given object is of identical class and not null:
//		if ((object == null) || (!getClass().equals(object.getClass())))
//			return false
//
//		if(this.childCount != object.childCount)
//			return false
//
//		def unmatchedProperty = importantProperties.find { prop -> object."$prop" != this."$prop" }
//		if(unmatchedProperty)
//			return false
//
//		def unmatchedChild = (0..<this.childCount).find { index -> this.getChild(index) != object.getChild(index) }
//		unmatchedChild == null
//	}
//	
//	void replace(XMLNode node) {
//		def parent = node.parent
//		if(!parent)
//			throw new IllegalStateException('Cannot replace node')
//		if(parent instanceof Annotation)
//			parent.setNonRDFAnnotation(this)
//		else {
//			def index = (0..<parent.childCount).findIndexOf { index -> parent.getChildAt(index).is(node) }
//			parent.removeChild(index)
//			parent.insertChild(index, this)
//		}
//	} 
//
//	@Override
//	public int hashCode() {
//		def prime = 31
//		def fieldHash = importantProperties.inject(31) { hash, field -> 
//			hash * prime + this."$field"?.hashCode() ?: 0 
//		} 
//		
//		(0..<this.childCount).inject(fieldHash) { hash, index -> 
//			hash * prime + this.getChild(index).hashCode()
//		}
//	}
//}
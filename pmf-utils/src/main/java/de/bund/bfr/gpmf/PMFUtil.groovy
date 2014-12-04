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
package de.bund.bfr.gpmf

import org.sbml.jsbml.AbstractSBase
import org.sbml.jsbml.Annotation
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.SBase
import org.sbml.jsbml.xml.XMLNode
import org.sbml.jsbml.xml.XMLToken
import org.sbml.jsbml.xml.XMLTriple

import de.bund.bfr.gnuml.ConformityMessage
import de.bund.bfr.gnuml.NMBase
import de.bund.bfr.gnuml.NuMLDocument

/**
 * 
 */
class PMFUtil {
	static String PMF_NS = 'http://sourceforge.net/projects/microbialmodelingexchange/files/PMF-ML'
	static String COMP_NS = 'http://www.sbml.org/sbml/level3/version1/comp/version1'
	static String DC_NS = 'http://purl.org/dc/elements/1.1/'
	static String XLINK_NS = 'http://www.w3.org/1999/xlink'
	static String DCTERMS_NS = 'http://purl.org/dc/terms/'
	static String PMML_NS = 'http://www.dmg.org/PMML-4_2'

	static Map<String, List<String>> BaseAnnotations = [DC_NS: [
			'identifier',
			'source',
			'title',
			'description',
			'coverage',
			'type',
			'subject',
			'creator',
			'language',
			'rights',
			'description',
			'format'
		],
		DCTERMS_NS: [
			'coverage',
			'references',
			'created',
			'modified',
			'hasVersion'
		]],
		ModelAnnotations = BaseAnnotations + [PMML_NS: ['modelquality']], 
		DataSetAnnotations = BaseAnnotations
		
	static XMLNode getPMFAnnotation(AbstractSBase node, String annotationName) {
		if(!node.annotation)
			return null

		node.annotation.nonRDFannotation.find {
			it.triple.name == annotationName && it.triple.namespaceURI == PMF_NS
		}
	}
	
	static Node getPMFAnnotation(NMBase node, String annotationName) {
		if(!node.annotations)
			return null

		node.annotations.find {
			it.name.localPart == annotationName && it.name.namespaceURI == PMF_NS
		}
	}
	
	static void setPMFAnnotation(NMBase node, String annotationName, Node annotation) {
		node.annotations.remove(getPMFAnnotation(node, annotationName))
		annotation.name = new groovy.xml.QName(PMF_NS, annotationName, annotation.name.prefix)
		node.annotations.add(annotation)
	}
	
	static javax.xml.namespace.QName toJavaQName(XMLToken token) {
		 new javax.xml.namespace.QName(token.URI, token.name, token.prefix)
	}
	
	static javax.xml.namespace.QName toJavaQName(groovy.xml.QName qname) {
		 new javax.xml.namespace.QName(qname.namespaceURI, qname.localPart, qname.prefix)
	}
	
	static groovy.xml.QName toGroovyQName(XMLToken token) {
		 new groovy.xml.QName(token.URI, token.name, token.prefix)
	}
	
	static Node toGroovyNode(XMLNode node) {
		def attr = (0..<node.attributesLength).collectEntries { index ->
			[(new groovy.xml.QName(node.getAttrURI(index), node.getAttrName(index), node.getAttrPrefix(index))):
				node.getAttrValue(index)]
		}
		new Node(null, toGroovyQName(node), attr, node.characters)
	}
	
	static void setPMFAnnotation(SBase node, String annotationName, XMLNode annotationNode) {
		def annotation = node.annotation
		if(!annotation || !annotation.nonRDFannotation)
			node.setAnnotation(annotation = new Annotation(nonRDFannotation: new XMLNode()))
		addOrReplace(annotation.nonRDFannotation, annotationNode)
	}
	
	static void addOrReplace(XMLNode parent, XMLNode child) {			
		def oldChild = parent.find {
			it.triple.name == child.name && it.triple.namespaceURI == child.uri
		}
		if(oldChild)
			parent.removeChild(oldChild)	
		parent.addChild(child)
	}
	
	static XMLNode ensurePMFAnnotation(SBase node, String annotationName) {		
		XMLNode pmfAnnotation = PMFUtil.getPMFAnnotation(node, annotationName)
		if(!pmfAnnotation) 
			setPMFAnnotation(pmfAnnotation = new XMLNode(new XMLTriple(annotationName, PMFUtil.PMF_NS, null)))
		pmfAnnotation
	}
	
	static SBMLReplacements = [PMFModel, PMFCompartment, PMFSpecies, PMFParameter].collectEntries { [(it.superclass): it] }
	static SBMLDocument wrap(SBMLDocument doc) {
		traverse(doc, { node ->			
			def replaceNodeType = SBMLReplacements[node.class]
			if(replaceNodeType)
				replaceNodeType.newInstance(node).replace(node)
		})
		doc
	}
	
	static void traverse(SBMLDocument doc, Closure callback) {
		def nodeStack = new LinkedList<SBase>([doc])
		while(nodeStack) {
			def node = nodeStack.pop()
			callback(node)
			(0..<(node.childCount)).each { index -> nodeStack.push(node.getChildAt(index)) }
		}
	}
	
	static NuMLReplacements = [PMFResultComponent, PMFAtomicDescription].collectEntries { [(it.superclass): it] }
	static NuMLDocument wrap(NuMLDocument doc) {		
		def nodeStack = new LinkedList<NMBase>([doc])
		while(nodeStack) {
			def node = nodeStack.pop()
			def replaceNodeType = NuMLReplacements[node.class]
			if(replaceNodeType)
				replaceNodeType.newInstance(node).replace(node)
			node.children.each { nodeStack.push(it) }
		}
		doc
	}
	
	static List<ConformityMessage> getInvalidSettings(SBMLDocument document, String prefix, PMFDocument pmf = null) {
		List<ConformityMessage> invalidSettings = []
		traverse(document, { node ->
			if(node instanceof SBMLReplacement)
				invalidSettings.addAll(node.getInvalidSettings(document, prefix, pmf))
		})
		invalidSettings
	}
}

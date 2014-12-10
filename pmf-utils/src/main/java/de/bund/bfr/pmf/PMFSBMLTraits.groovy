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
package de.bund.bfr.pmf;

import javax.xml.namespace.QName

import org.apache.log4j.Level
import org.sbml.jsbml.ListOf
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.SBase
import org.sbml.jsbml.util.StringTools
import org.sbml.jsbml.xml.XMLNode
import org.sbml.jsbml.xml.XMLToken
import org.sbml.jsbml.xml.XMLTriple

import de.bund.bfr.numl.ConformityMessage


trait MetadataAnnotation extends SBMLReplacement {
	
	Map<QName, String> getQualifiedAnnotations() {
		annotationGNodes.collectEntries { annotation ->
			groovy.xml.QName name = annotation.name()
			[(new QName(name.namespaceURI, name.localPart, name.prefix)): annotation.value as String]
		}.findAll { it.value }
	}
	
	Map<String, String> getAnnotations() {
		qualifiedAnnotations.collectEntries { [(it.key.localPart): it.value] }
	}
	
	String getAnnotation(String localPart, String uri = null) {
		getAnnotationNode(localPart, uri)?.find { it.text }?.characters
	}
	
	void setAnnotation(String localPart, String uri, String value) {
		setAnnotation(localPart, uri, new XMLNode(value))
	}
	
	void setAnnotation(String localPart, String uri, XMLToken value) {
		XMLNode pmfMetaData = PMFUtil.ensurePMFAnnotation(this, 'metadata')
		def container = new XMLNode(new XMLTriple(localPart, uri, null))
		container.addChild(value)
		PMFUtil.addOrReplace(pmfMetaData, container)
	}	
	
	void setAnnotation(XMLToken value) {
		XMLNode pmfMetaData = PMFUtil.ensurePMFAnnotation(this, 'metadata')
		PMFUtil.addOrReplace(pmfMetaData, value)
	}
	
	List<Node> getAnnotationGNodes() {
		XMLNode pmfMetaData = PMFUtil.getPMFAnnotation(this, 'metadata')
		if(!pmfMetaData)
			return []
		pmfMetaData.children().collect { XMLToken child ->
			PMFUtil.toGroovyNode(child)
		}			
	}
	
	XMLNode getAnnotationNode(String localPart, String uri = null) {
		XMLNode pmfMetaData = PMFUtil.getPMFAnnotation(this, 'metadata')
		pmfMetaData?.getChildElement(localPart, uri ?: '*')
	}
	
	List<ConformityMessage> getInvalidSettings(SBMLDocument document, String prefix, PMFDocument pmf) {
		def pmfMetaData = PMFUtil.getPMFAnnotation(this, "metadata")
		if(!pmfMetaData)
			return [new ConformityMessage(level: Level.WARN,
				message: "$prefix: ${elementName}s should be annotated (Specification 11)")]
		def annotations = getQualifiedAnnotations()*.key
		def recommended = PMFUtil.BaseAnnotations.collect { ns, tags -> tags.collect { new QName(ns, it) } }
		
		def missing = annotations - recommended
		def superfluous = recommended - annotations
		missing.collect { annotationName ->
			new ConformityMessage(level: Level.WARN,
				message: "$prefix: Recommend annotation $annotationName of $elementName ${id} not present (Specification 11)")
		} + superfluous.collect { annotationName ->
			new ConformityMessage(level: Level.WARN,
				message: "$prefix: Unknown annotation $annotationName found in $elementName ${id}, might be an indicator for misspellings (Specification 11)")
		}
	}
}

trait SourceAnnotation extends SBMLReplacement {
	void setSource(URI source) {
		if (source == null)
			throw new NullPointerException("source must not be null");

		XMLNode pmfMetaData = PMFUtil.ensurePMFAnnotation(this, 'metadata')
		def dcSource = pmfMetaData.getChildElement('source', PMFUtil.DC_NS)
		if(!dcSource)
			pmfMetaData.addChild(
					dcSource = new XMLNode(new XMLTriple('source', PMFUtil.DC_NS, null)))
		dcSource.removeChildren()
		dcSource.addChild(new XMLNode(source.toString()))
	}

	URI getSource() {
		def pmfMetaData = PMFUtil.getPMFAnnotation(this, 'metadata')
		def dcSource = pmfMetaData?.getChildElement('source', PMFUtil.DC_NS)
		def uri = dcSource?.find { it.text }?.characters
		if(uri)
			try {
				return new URI(uri)
			} catch(e) {
			}
		null
	}
	
	List<ConformityMessage> getInvalidSettings(SBMLDocument document, String prefix, PMFDocument pmf) {
		if(!source)
			return [new ConformityMessage(level: Level.WARN,
				message: "$prefix: $elementName $id should contain PMF metadata annotation with source (Specification 6/7)")]
		[]
	}
}

trait SBMLReplacement {
	void initLevelAndVersion() {		
		if(getLevel() == -1)
			setLevel(3)
		if(getVersion() == -1)
			setVersion(1)
	}
	
	void replace(SBase sbmlElement) {
		this.setParent(null)
		SBase newParent = sbmlElement.parent
		if(newParent instanceof ListOf) {
			// bug SBML does not automatically unregister old elements
			newParent.unregisterChild(sbmlElement)
			newParent.set(newParent.indexOf(sbmlElement), this)
		}
		else
			newParent."$sbmlElement.elementName" = this
	}
	
	String getElementName() {
		StringTools.firstLetterLowerCase(getClass().superclass.simpleName)
	}
	
	abstract List<ConformityMessage> getInvalidSettings(SBMLDocument document, String prefix, PMFDocument pmf)
}
/*******************************************************************************
 * Copyright (c) 2015 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Department Biological Safety - BfR
 *******************************************************************************/
package de.bund.bfr.pmf.sbml;

import groovy.transform.EqualsAndHashCode
import groovy.transform.InheritConstructors
import groovy.transform.ToString

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.namespace.QName
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

import org.apache.log4j.Level
import org.dmg.pmml.PredictiveModelQuality
import org.sbml.jsbml.Model
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.xml.XMLAttributes
import org.sbml.jsbml.xml.XMLNode
import org.sbml.jsbml.xml.XMLToken
import org.sbml.jsbml.xml.XMLTriple
import org.xml.sax.InputSource

import de.bund.bfr.numl.ConformityMessage
import de.bund.bfr.pmf.PMFDocument
import de.bund.bfr.pmf.PMFUtil

/**
 * Wrapper for PMF model (Specification 5 - PMF namespace, Specification 11 � Annotation of metadata, 
 * Specification 12 � Annotation of uncertainties).
 */
@InheritConstructors
class PMFModel extends Model implements SBMLReplacement {
	{
		initLevelAndVersion()
	}
	
	PredictiveModelQuality getModelQuality() {
		def annotation = getAnnotationNode(PMFUtil.PMML_NS, 'modelquality')
		if(!annotation)
			return null
				
		def jaxbContext = JAXBContext.newInstance(PredictiveModelQuality.class);
		def unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.unmarshal(new InputSource(annotation.toXMLString()))
	}
	
	
	final static sbmlTemplate = """
		<sbml xmlns='http://www.sbml.org/sbml/level2/version4' level='2' version='1'>
			<model id='test'>
				<annotation>%s</annotation>
			</model>
		</sbml>"""
	
	void setModelQuality(PredictiveModelQuality quality) {
		if (quality == null)
			throw new NullPointerException("quality must not be null");
			
		JAXBContext jaxbContext = JAXBContext.newInstance(PredictiveModelQuality.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		def writer = new StringWriter().withWriter { writer ->
			jaxbMarshaller.marshal(quality, writer)
		}
		
		def sbmlMockup = String.format(sbmlTemplate, sbmlMetaData.toXMLString())
		def reader = new SBMLAdapter(validating: true)
		def qualityDoc = reader.parseText(sbmlMockup)
		setAnnotation(qualityDoc.model.annotation.nonRDFAnnotation.find { !it.text })
	}
	
	Map<String, String> getDataSources() {
		XMLNode pmfMetaData = PMFUtil.getPMFAnnotation(this)
		def dataSources = pmfMetaData?.getChildElement('dataSources', PMFUtil.PMF_NS)
		dataSources?.getChildElements('dataSource', PMFUtil.PMF_NS)?.collectEntries { source ->
			[source.getAttrValue('id'), source.getAttrValue('href', PMFUtil.XLINK_NS)]
		} ?: [:]
	}
	
	String getDataSource(String id) {
		this.dataSources[id]
	}
	
	void setDataSources(Map<String, String> dataSources) {
		XMLNode sourcesNode = PMFUtil.ensurePMFAnnotation(this, 'dataSources')
		sourcesNode.removeChildren()
		dataSources.each { id, path ->
			def dataSource = new XMLNode(new XMLTriple('dataSource', PMFUtil.PMF_NS, null), new XMLAttributes())
			dataSource.addAttr('id', id)
			dataSource.addAttr('href', path, PMFUtil.XLINK_NS)
			sourcesNode.addChild(dataSource)
		}
	}
	
	void setDataSource(String id, String path) {
		def dataSources = getDataSources()
		dataSources[id] = path
		setDataSources(dataSources)
	}
	
	List<ConformityMessage> getInvalidMetadata(SBMLDocument document, String prefix, PMFDocument pmf) {
		def pmfMetaData = PMFUtil.getPMFAnnotation(this)
		if(!pmfMetaData)
			return [
				new ConformityMessage(level: Level.WARN,
				message: "$prefix: ${elementName}s should be annotated (Specification 11)")
			]
		def annotations = getQualifiedAnnotations()*.key
		def recommended = PMFUtil.ModelAnnotations.collect { ns, tags ->
			tags.collect { new QName(ns, it) }
		}.flatten()
	
		def missing = recommended - annotations
		def superfluous = annotations - recommended
		missing.collect { annotationName ->
			new ConformityMessage(level: Level.INFO,
			message: "$prefix: Recommend annotation $annotationName of $elementName ${id} not present (Specification 11)")
		} + superfluous.collect { annotationName ->
			new ConformityMessage(level: Level.INFO,
			message: "$prefix: Unknown annotation $annotationName found in $elementName ${id}, might be an indicator for misspellings (Specification 11)")
		}
	}
	
	List<ConformityMessage> getInvalidSettings(SBMLDocument document, String prefix, PMFDocument pmf) {
		def messages = getInvalidMetadata(document, prefix, pmf)
		
		if(!getAnnotation('modelquality', PMFUtil.PMML_NS))
			messages << new ConformityMessage(level: Level.WARN,
				message: "$prefix: model quality should be annotated (Specification 12)")
			
		def dataSources = this.dataSources
		if(!dataSources) {
			if(pmf.dataSets)
				messages << new ConformityMessage(level: Level.WARN,
					message: "$prefix: model $model.id should reference dataSets with at least one pmf:dataSource element (Specification 13)")
		}
		else if(dataSources.find { it.key == null })
			messages << new ConformityMessage("$prefix: model $model.id contains $index pmf:dataSource without id (Specification 13)")
		else if(dataSources.find { it.value == null })
			messages << new ConformityMessage("$prefix: model $model.id contains $index pmf:dataSource without xlink:href (Specification 13)")
		messages		
	}
	
	Map<QName, String> getQualifiedAnnotations() {
		annotationGNodes.collectEntries { annotation ->
			groovy.xml.QName name = annotation.name()
			[(new QName(name.namespaceURI, name.localPart, name.prefix)): annotation.value as String]
		}.findAll { it.key.localPart && it.value }
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
		XMLNode pmfMetaData = PMFUtil.ensurePMFAnnotation(this)
		def container = new XMLNode(new XMLTriple(localPart, uri, null))
		container.addChild(value)
		PMFUtil.addOrReplace(pmfMetaData, container)
	}

	void setAnnotation(XMLToken value) {
		XMLNode pmfMetaData = PMFUtil.ensurePMFAnnotation(this)
		PMFUtil.addOrReplace(pmfMetaData, value)
	}

	List<Node> getAnnotationGNodes() {
		XMLNode pmfMetaData = PMFUtil.getPMFAnnotation(this)
		if(!pmfMetaData)
			return []
		pmfMetaData.children().collect { XMLToken child ->
			PMFUtil.toGroovyNode(child)
		}
	}

	XMLNode getAnnotationNode(String localPart, String uri = null) {
		XMLNode pmfMetaData = PMFUtil.getPMFAnnotation(this)
		pmfMetaData?.getChildElement(localPart, uri ?: '*')
	}
}

@EqualsAndHashCode
@ToString
class SourceValue {
	final String sourceId, xpath
		
	public SourceValue(String sourceId, String xpath) {
		this.sourceId = sourceId;
		this.xpath = xpath;
		// validate
		try {
			getXPathExpression()
		} catch(e) {
			throw new IllegalArgumentException("Invalid xpath", e)
		}
	}

	XPathExpression getXPathExpression() {
		javax.xml.xpath.XPath xpath = XPathFactory.newInstance().newXPath()
		xpath.compile(this.xpath)
	}
}
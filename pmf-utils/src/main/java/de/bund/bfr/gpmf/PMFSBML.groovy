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
package de.bund.bfr.gpmf;

import java.util.List;

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
import org.sbml.jsbml.Compartment
import org.sbml.jsbml.Constraint
import org.sbml.jsbml.Model
import org.sbml.jsbml.Parameter
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.SBase
import org.sbml.jsbml.Species
import org.sbml.jsbml.Unit
import org.sbml.jsbml.UnitDefinition
import org.sbml.jsbml.xml.XMLNode
import org.sbml.jsbml.xml.XMLToken
import org.sbml.jsbml.xml.XMLTriple
import org.xml.sax.InputSource

import de.bund.bfr.gnuml.ConformityMessage


/**
 * Wrapper for PMF compartments (Specification 6 – SBML component compartments).
 */
@InheritConstructors
class PMFCompartment extends Compartment implements SourceAnnotation, SBMLReplacement {
}

/**
 * Wrapper for PMF species (Specification 7 – SBML component species).
 */
@InheritConstructors
class PMFSpecies extends Species implements SourceAnnotation, SBMLReplacement {
}

@InheritConstructors
class PMFUnitDefinition extends UnitDefinition implements SBMLReplacement {
	void setTransformation(String transformation) {
		if (transformation == null)
			throw new NullPointerException("transformation must not be null");

		XMLNode pmfTransformation = PMFUtil.ensurePMFAnnotation(this, 'transformation')
		pmfTransformation.addAttr('name', transformation)
		pmfTransformation.addAttr('href', 'http://sourceforge.net/projects/microbialmodelingexchange/files/Units/PMF-ML_UnitTransformations.xlsx', PMFUtil.XLINK_NS)
	}

	String getTransformation() {
		def pmfTransformation = PMFUtil.getPMFAnnotation(this, 'transformation')
		pmfTransformation?.getAttrValue('name')
	}
	
	List<ConformityMessage> getInvalidSettings(SBMLDocument document, String prefix, PMFDocument pmf) {
		def messages = []
		def transformation = PMFUtil.getPMFAnnotation(this, 'transformation')
		if(!transformation && [id, name].any { it.contains('log') })
			messages << new ConformityMessage("$prefix: Log unit $id must contain PMF unit annotation (Specification 8)")
		else if(transformation) {
			if(!transformation.getAttrValue('name'))
				messages << new ConformityMessage("$prefix: PMF unit $id must contain name (Specification 8)")
			if(!transformation.getAttrValue('href', PMFUtil.XLINK_NS))
				messages << new ConformityMessage("$prefix: PMF unit $id must refer to PMF units (Specification 8)")
		}
		messages
	}
}

/**
 * Wrapper for PMF model (Specification 5 - PMF namespace, Specification 11 – Annotation of metadata, 
 * Specification 12 – Annotation of uncertainties).
 */
@InheritConstructors
class PMFModel extends Model implements SBMLReplacement, MetadataAnnotation {
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
		XMLNode pmfMetaData = PMFUtil.getPMFAnnotation(this, 'metadata')
		def dataSources = pmfMetaData?.getChildElement('dataSources', PMFUtil.PMF_NS)
		dataSources?.getChildElements('dataSource', PMFUtil.PMF_NS).collectEntries { source ->
			[source.getAttrValue('id'), source.getAttrValue('href', PMFUtil.XLINK_NS)]
		} ?: [:]
	}
	
	String getDataSource(String id) {
		this.dataSources[id]
	}
	
	void setDataSources(Map<String, String> dataSources) {
		XMLNode pmfMetaData = PMFUtil.ensurePMFAnnotation(this, 'metadata')
		def sourcesNode = new XMLNode(new XMLTriple('dataSources', PMFUtil.PMF_NS, null))
		dataSources.each { id, path ->
			def dataSource = new XMLNode(new XMLTriple('dataSource', PMFUtil.PMF_NS, null))
			dataSource.addAttr('id', id)
			dataSource.addAttr('href', path, PMFUtil.XLINK_NS)
			sourcesNode.addChild(dataSource)
		}
		PMFUtil.addOrReplace(pmfMetaData, sourcesNode)
	}
	
	void setDataSource(String id, String path) {
		def dataSources = getDataSources()
		dataSources[id] = path
		setDataSources(dataSources)
	}
	
	List<ConformityMessage> getInvalidSettings(SBMLDocument document, String prefix, PMFDocument pmf) {
		def messages = []
		
		if(!getAnnotation('modelquality', PMFUtil.PMML_NS))
			messages << new ConformityMessage(level: Level.WARN,
				message: "$name: model quality should be annotated (Specification 12)")
			
		def dataSources = this.dataSources
		if(!dataSources)
			messages << new ConformityMessage("$prefix: model $model.id must reference dataSets with at least one pmf:dataSource element (Specification 13)")
		else if(dataSources.find { it.key == null })
			messages << new ConformityMessage("$prefix: model $model.id contains $index pmf:dataSource without id (Specification 13)")
		else if(dataSources.find { it.value == null })
			messages << new ConformityMessage("$prefix: model $model.id contains $index pmf:dataSource without xlink:href (Specification 13)")
		messages		
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

@EqualsAndHashCode
@ToString
class DoubleRange {
	final double from, to
	final Unit unit

	DoubleRange(double from, double to, Unit unit = null) {
		this.from = from;
		this.to = to;
		this.unit = unit;
	}
}

@InheritConstructors
class PMFParameter extends Parameter implements SBMLReplacement {	
	List<SourceValue> getSourceValues() {
		def sourceValues = PMFUtil.getPMFAnnotation(this, 'sourceValues')
		sourceValues?.getChildElements('sourceValue', PMFUtil.PMF_NS).collect { source ->
			new SourceValue(source.getAttrValue('source'), source.getAttrValue('descriptor'))
		} ?: []
	}
	
	List<Object> getValueInstances(PMFDocument pmfDoc) {
		this.sourceValues.inject([]) { result, sourceValue ->
			def xlink = (this.SBMLDocument.model as PMFModel).getDataSource(sourceValue.sourceId)
			if(!xlink)
				throw new IllegalArgumentException("The provided SBML document did not declare data source ${sourceValue.sourceId}")
			def dataSet = pmfDoc.resolve(xlink, this.SBMLDocument)
			def input = pmfDoc.getInputStream(dataSet)
			result + sourceValue.XPathExpression.evaluate(new InputSource(input))
		}
	}
		
	void setSourceValues(List<SourceValue> sourceValues) {
		XMLNode sourcesNode = PMFUtil.ensurePMFAnnotation(this, 'sourceValues')
		sourcesNode.removeChildren()
		sourceValues.each { sourceId, xpath ->
			def sourceValue = new XMLNode(new XMLTriple('sourceValue', PMFUtil.PMF_NS, null))
			sourceValue.addAttr('source', id)
			sourceValue.addAttr('descriptor', path, PMFUtil.XLINK_NS)
			sourcesNode.addChild(sourceValue)
		}
	}
	
	void setSourceValue(SourceValue sourceValue) {
		setSourceValues([sourceValue])
	}
	
	void setSourceValue(String sourceId, String xpath) {
		setSourceValues([new SourceValue(sourceId, xpath)])
	}
	
	DoubleRange getRange() {
		def constraint = this.constraint
		// TODO:
	}
	
	Constraint getConstaint() {		
		PMFModel model = this.root.model
		model.listOfConstraints.find {
			// TODO:
		}
	}
	
//	final static rangeTemplate = """
//		<constraint>
//			<math xmlns="http://www.w3.org/1998/Math/MathML">
//				<apply>
//					<and/>
//					<apply>
//						<lt/>
//						<cn sbml:units="mole"> 1 </cn>
//						<ci> S1 </ci>
//					</apply>
//					<apply>
//						<lt/>
//						<ci> S1 </ci>
//						<cn sbml:units="mole"> 100 </cn>
//					</apply>
//				</apply>
//			</math>
//			<message xmlns="http://www.w3.org/1999/xhtml">
//			</message>
//		</constraint>"""
	
	void setRange(DoubleRange range) {
		PMFModel model = this.root.getChildElement('model')
		def oldConstraint = model.listOfConstraints.find { 
			// TODO:
		}
	}
	
	@Override
	List<ConformityMessage> getInvalidSettings(SBMLDocument document, String prefix, PMFDocument pmf) {
		def messages = []
		// TODO:		
		messages.addAll(['value', 'constant'].findAll { this."$it" == null }.collect {
			new ConformityMessage("$prefix: Parameter $id must contain $it (Specification 9)")
		})
		if(!this.constant) {
			try {
				if(!sourceValues)
					messages << new ConformityMessage("$prefix: parameter $id does not contain a source value descriptor (Specification 13)")
				else
					try {
						getValueInstances(pmf)
					} catch(e) {
						messages << new ConformityMessage("$prefix: $e.message")
					}
			} catch(e) {
				messages << new ConformityMessage("$prefix: invalid source value because of $e.message: $e.cause (Specification 13)")
			}
		}
		messages
	}
}

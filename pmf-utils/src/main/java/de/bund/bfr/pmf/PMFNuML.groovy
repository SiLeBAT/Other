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
package de.bund.bfr.pmf

import java.util.Map;

import javax.xml.namespace.QName;

import groovy.transform.InheritConstructors

import org.sbml.jsbml.Model
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.SBMLWriter
import org.sbml.jsbml.Unit

import de.bund.bfr.numl.AtomicDescription
import de.bund.bfr.numl.CompositeDescription
import de.bund.bfr.numl.ConformityMessage
import de.bund.bfr.numl.NuMLDocument
import de.bund.bfr.numl.ResultComponent
import de.bund.bfr.numl.TupleDescription

@InheritConstructors
class PMFResultComponent extends ResultComponent {
	final static sbmlTemplate = """
		<sbml xmlns='http://www.sbml.org/sbml/level3/version1/core' level='3' version='1'>
			%s
		</sbml>
	"""
	
	PMFResultComponent(ResultComponent rc) {
		// rather inefficient but versatile
		setParent(rc.parent)
		setOriginalNode(rc.originalNode)
	}
	
	{
		elementName = this.class.superclass.simpleName[0].toLowerCase() + this.class.superclass.simpleName[1..-1] 
	}
		
	void setSbml(Model sbml) {
		if (sbml == null)
			throw new NullPointerException("sbml must not be null");

		def doc = new SBMLDocument(sbml.version, sbml.model)
		doc.model = sbml
		def baos = new ByteArrayOutputStream().withStream { stream ->
			new SBMLWriter().write(doc, stream)
			stream
		}
		def bais = new ByteArrayInputStream(baos.toByteArray()).withStream { input ->
			def node = new XmlParser().parse(input)
			setAnnotation('model', PMFUtil.SBML_NS, node.model)
		}
	}
	
	Model getSbml() {
		sbmlReader?.document?.model
	}
	
	SBMLAdapter getSbmlReader() {		
		def sbmlMetaData = getAnnotationNode('model', PMFUtil.SBML_NS)
		if(!sbmlMetaData)
			return null
		def sbmlXml = new StringWriter()
		sbmlXml.withPrintWriter { writer ->
			new XmlNodePrinter(writer).print(sbmlMetaData)
		}
		def sbmlMockup = String.format(sbmlTemplate, sbmlXml.toString())
		def sbmlReader = new SBMLAdapter(validating: true)
		sbmlReader.parseText(sbmlMockup)
		sbmlReader
	}

	List<ConformityMessage> getInvalidSettings(String prefix) {
		List messages = []
		def sbml = this.sbml
		if(!sbml)
			messages << new ConformityMessage("$prefix: resultComponent $id must be annotated with pmf:sbml (Specification 13)")
		else {
			messages.addAll(sbmlReader?.parseMessages?.collect {
				it.message = "$prefix: resultComponent $id: pmf:sbml: $it.message"
			})
			
			if(sbml) {
				if(!sbml.listOfCompartments)
					messages << new ConformityMessage("$prefix/resultComponent[$id]/sbml:model must have at least one compartment (Specification 13)")
				if(!sbml.listOfSpecies)
					messages << new ConformityMessage("$prefix/resultComponent[$id]/sbml:model must have at least one species (Specification 13)")
				if(!sbml.listOfUnitDefinitions)
					messages << new ConformityMessage("$prefix/resultComponent[$id]/sbml:model must have at least one unit definition (Specification 13)")
			}
		}
	
		messages + super.getInvalidSettings(prefix)
	}
	
	void replace(ResultComponent rc) {
		NuMLDocument newParent = rc.parent
		def index = newParent.resultComponents.findIndexOf { it.is(rc) }
		newParent.resultComponents.set(index, this)
	}
	
	Map<QName, String> getQualifiedAnnotations() {
		PMFUtil.getPMFAnnotation(this, 'metadata')?.children().collectEntries { annotation ->
			[(PMFUtil.toJavaQName(annotation.name())): annotation.value as String]
		}.findAll { it.value }
	}
	
	Map<String, String> getAnnotations() {
		qualifiedAnnotations.collectEntries { [(it.key.localPart): it.value] }
	}
	
	String getAnnotation(String localPart, String uri = null) {
		getAnnotationNode(localPart, uri)?.value
	}
	
	Node getAnnotationNode(String localPart, String uri = null) {
		PMFUtil.getPMFAnnotation(this, 'metadata')?.getAt(new groovy.xml.QName(uri, localPart))[0]
	}
	
	void setAnnotation(String localPart, String uri, String value) {
		def oldAnnotation = getAnnotation(localPart, uri)
		if(oldAnnotation)
			PMFUtil.getPMFAnnotation(this, 'metadata')?.remove(oldAnnotation)
		PMFUtil.ensurePMFAnnotation(this, 'metadata').append(
			new Node(annotation, new groovy.xml.QName(localPart, uri), [:], value))
	}
	
	void setAnnotation(String localPart, String uri, Node value) {		
		def oldAnnotation = getAnnotation(localPart, uri)
		if(oldAnnotation)
			PMFUtil.getPMFAnnotation(this, 'metadata')?.remove(oldAnnotation)
		PMFUtil.ensurePMFAnnotation(this, 'metadata').append(value)
	}
}

@InheritConstructors
class PMFAtomicDescription extends AtomicDescription {
	PMFAtomicDescription(AtomicDescription atomicDescription) {
		// rather inefficient but versatile
		setParent(atomicDescription.parent)
		setOriginalNode(atomicDescription.originalNode)
	}
	
	{
		elementName = this.class.superclass.simpleName[0].toLowerCase() + this.class.superclass.simpleName[1..-1] 
	}
	
	/**
	 * Sets the unit to the specified value.
	 *
	 * @param unit the unit to set
	 */
	void setUnit(Unit unit) {
		if (unit == null)
			throw new NullPointerException("unit must not be null");

		this.unit = unit;
	}
	
	/**
	 * Returns the unit.
	 * 
	 * @return the unit
	 */
	Unit getUnit() {	
		def unitAnnotation = PMFUtil.getPMFAnnotation(this, 'unit')
		if(!unitAnnotation)
			return null
		
		PMFResultComponent rc = resultComponent
		if(!rc)
			return null
			
		rc.sbml?.listOfUnitDefinitions?.find { it.id == unitAnnotation.ref }
	}
			
	List<ConformityMessage> getInvalidSettings(String prefix) {
		def messages = []
		def unitAnnotation = PMFUtil.getPMFAnnotation(this, 'unit')
		PMFResultComponent rc = resultComponent
		def unitDefs = rc.sbml?.listOfUnitDefinitions ?: []
		if(!unitAnnotation)
			messages << new ConformityMessage("$prefix: atomic value $name must have pmf:unit annotation (Specification 13)")
		else if(!unitDefs.find { it.id == unitAnnotation.'@ref' })
			messages << new ConformityMessage("$prefix: atomic value $name unknown unit id '${unitAnnotation.'@ref'}' in pmf:unit annotation, known units ${unitDefs*.id} (Specification 13)")
		messages
	}
	
	void replace(AtomicDescription description) {
		def newParent = description.parent
		switch(newParent.class) {
		case ResultComponent:
			newParent.dimensionDescription = this
			break
		case CompositeDescription:		
			newParent.description = this
			break
		case TupleDescription:		
			def index = newParent.descriptions.findIndexOf { it.is(description) }
			newParent.descriptions.set(index, this)
			break
		}
	}
}

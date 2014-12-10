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

import groovy.transform.InheritConstructors

import javax.xml.namespace.QName

import org.apache.log4j.Level
import org.sbml.jsbml.Model
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.SBMLWriter
import org.sbml.jsbml.Unit

import de.bund.bfr.numl.CompositeDescription
import de.bund.bfr.numl.ConformityMessage
import de.bund.bfr.numl.NuMLDocument
import de.bund.bfr.numl.OntologyTerm
import de.bund.bfr.numl.ResultComponent
import de.bund.bfr.numl.TupleDescription

/**
 * Extends the NuML {@link ResultComponent} with convenience methods for PMF annotations and provides shortcuts to access the SBML annotations.
 */
class PMFResultComponent extends ResultComponent implements PMFNuMLMetadataContainer {
	
	PMFResultComponent(ResultComponent rc) {
		// rather inefficient but versatile
		setParent(rc.parent)
		setOriginalNode(rc.originalNode)
	}
	
	PMFResultComponent() {		
	}
	
	{
		elementName = this.class.superclass.simpleName[0].toLowerCase() + this.class.superclass.simpleName[1..-1] 
	}
	
	List<ConformityMessage> getInvalidSettings(String prefix) {
		def pmfMetaData = PMFUtil.getPMFAnnotation(this, "metadata")
		if(!pmfMetaData)
			return [new ConformityMessage(level: Level.WARN,
				message: "$prefix: ${elementName} ${id} should be annotated (Specification 11/13)")]
		def annotations = getQualifiedAnnotations()*.key
		def recommended = PMFUtil.BaseAnnotations.collect { ns, tags -> tags.collect { new QName(ns, it) } }
		
		def missing = annotations - recommended
		def superfluous = recommended - annotations
		missing.collect { annotationName ->
			new ConformityMessage(level: Level.WARN,
				message: "$prefix: Recommend annotation $annotationName of $elementName ${id} not present (Specification 11/13)")
		} + superfluous.collect { annotationName ->
			new ConformityMessage(level: Level.WARN,
				message: "$prefix: Unknown annotation $annotationName found in $elementName ${id}, might be an indicator for misspellings (Specification 11/13)")
		} + super.getInvalidSettings(prefix)
	}
	
	void replace(ResultComponent rc) {
		NuMLDocument newParent = rc.parent
		def index = newParent.resultComponents.findIndexOf { it.is(rc) }
		newParent.resultComponents.set(index, this)
	}	
}

/**
 * Extends the {@link OntologyTerm} to allow the annotation of unit, compartment, and species.
 */
class PMFOntologyTerm extends OntologyTerm implements PMFNuMLMetadataContainer {
	PMFOntologyTerm() {	
	}
	
	PMFOntologyTerm(OntologyTerm ontologyTerm) {		
		// rather inefficient but versatile
		setParent(ontologyTerm.parent)
		setOriginalNode(ontologyTerm.originalNode)
	}
	
	{
		elementName = this.class.superclass.simpleName[0].toLowerCase() + this.class.superclass.simpleName[1..-1] 
	}
	
	private transient SBMLAdapter sbmlReader
	private transient Model sbml
	
	final static sbmlTemplate = """
		<sbml xmlns='http://www.sbml.org/sbml/level3/version1/core' level='3' version='1'>
			%s
		</sbml>
	"""
	
	protected void setSbml(Model sbml) {
		if (sbml == null)
			throw new NullPointerException("sbml must not be null");

		def doc = new SBMLDocument(document?.level ?: 3, document?.version ?: 1)
		doc.model = sbml
		doc.addDeclaredNamespace('xmlns', PMFUtil.XLINK_NS)
		def xmlString = new SBMLAdapter().toString(doc)
		def node = new XmlParser().parseText(xmlString)
		['listOfUnitDefinitions', 'listOfCompartments', 'listOfSpecies'].each { elem ->
			setAnnotationNode(elem, PMFUtil.SBML_NS, node.model."$elem"[0])
		}
		this.sbml = sbml
		this.sbmlReader = null
	}
	
	protected Model getSbml() {
		this.sbml ?: getSbmlReader()?.document?.model
	}
	
	protected SBMLAdapter getSbmlReader() {
		if(this.sbmlReader)
			return sbmlReader
			
		def sbmlMetaData = getAnnotationNode('metadata', PMFUtil.PMF_NS)
		def sbmlXml = new StringWriter()
		if(sbmlMetaData) {
			sbmlMetaData = sbmlMetaData.clone()
			sbmlMetaData.name = new QName('model', PMFUtil.SBML_NS)
			
			sbmlXml.withPrintWriter { writer ->
				new XmlNodePrinter(writer).print(sbmlMetaData)
			}
		}
		def sbmlMockup = String.format(sbmlTemplate, sbmlXml.toString())
		
		sbmlReader = new SBMLAdapter(validating: true)
		sbmlReader.parseText(sbmlMockup)
		sbmlReader
	}
	
	/**
	 * Sets the unit to the specified value.
	 *
	 * @param unit the unit to set
	 */
	void setUnitDefinition(PMFUnitDefinition unit) {
		if (unit == null)
			throw new NullPointerException("unit must not be null");

		def sbml = getSbml() ?: new Model(document?.level ?: 3, document?.version ?: 1)
		sbml.listOfUnitDefinitions.clear()
		sbml.listOfUnitDefinitions.add(unit)
		this.setSbml(sbml)
	}
	
	/**
	 * Returns the unit.
	 *
	 * @return the unit
	 */
	Unit getUnit() {
		this.unitDefinition?.listOfUnits?.get(0)
	}
	
	/**
	 * Sets the unit to the specified value.
	 *
	 * @param unit the unit to set
	 */
	void setUnit(Unit unit) {
		if (unit == null)
			throw new NullPointerException("unit must not be null");

		def unitDef = new PMFUnitDefinition('predefined', document?.level ?: 3, document?.version ?: 1)
		unitDef.addUnit(unit)
		this.unitDefinition = unitDef
	}
	
	/**
	 * Returns the unit.
	 *
	 * @return the unit
	 */
	PMFUnitDefinition getUnitDefinition() {
		getSbml()?.listOfUnitDefinitions?.collect { new PMFUnitDefinition(it) }?.getAt(0)
	}
	
	/**
	 * Sets the compartment to the specified value.
	 *
	 * @param compartment the compartment to set
	 */
	void setCompartment(PMFCompartment compartment) {
		if (compartment == null)
			throw new NullPointerException("compartment must not be null");
			
		def sbml = getSbml() ?: new Model(document?.level ?: 3, document?.version ?: 1)
		sbml.listOfCompartments.clear()
		sbml.listOfCompartments.add(compartment)
		this.setSbml(sbml)
	}
	
	/**
	 * Returns the compartment.
	 * 
	 * @return the compartment
	 */
	PMFCompartment getCompartment() {	
		getSbml()?.listOfCompartments?.collect { new PMFCompartment(it) }?.getAt(0)
	}
	
	/**
	 * Sets the species to the specified value.
	 *
	 * @param species the species to set
	 */
	void setSpecies(PMFSpecies species) {
		if (species == null)
			throw new NullPointerException("species must not be null");
			
		def sbml = getSbml() ?: new Model(document?.level ?: 3, document?.version ?: 1)
		sbml.listOfSpecies.clear()
		sbml.listOfSpecies.add(species)
		this.setSbml(sbml)
	}
	
	/**
	 * Returns the species.
	 *
	 * @return the species
	 */
	PMFSpecies getSpecies() {
		getSbml()?.listOfSpecies?.collect { new PMFSpecies(it) }?.getAt(0)
	}
	
			
	List<ConformityMessage> getInvalidSettings(String prefix) {
		def messages = []
		
		if(unitDefinition)
			messages.addAll(unitDefinition.getInvalidSettings(getSbmlReader().document, "$prefix/unitDefinition", null))
		
		if(species)
			messages.addAll(species.getInvalidSettings(getSbmlReader().document, "$prefix/species", null))
		else if(unitDefinition?.isVariantOfSubstancePerVolume())		
			messages << new ConformityMessage(level: Level.WARN,
				message: "$prefix: ontology term ${id ?: term} seem to denote a concentration and should have a species annotation (Specification 13)")
			
		if((species == null) == (unitDefinition == null))
			messages << new ConformityMessage("$prefix: ontology term ${id ?: term} must have either a unitDefinition or a species annotation (Specification 13)")
			
		if(compartment)
			messages.addAll(compartment.getInvalidSettings(getSbmlReader().document, "$prefix/compartment", null))
			
		messages
	}
	
	void replace(OntologyTerm ontologyTerm) {
		def newParent = ontologyTerm.parent
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

trait PMFNuMLMetadataContainer {	
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
		PMFUtil.getPMFAnnotation(this, 'metadata')?.getAt(new groovy.xml.QName(uri, localPart))?.getAt(0)
	}
	
	void setAnnotation(String localPart, String uri, String value) {
		def oldAnnotation = getAnnotation(localPart, uri)
		if(oldAnnotation)
			PMFUtil.getPMFAnnotation(this, 'metadata')?.remove(oldAnnotation)
		if(value)
			PMFUtil.ensurePMFAnnotation(this, 'metadata').append(
				new Node(annotation, new groovy.xml.QName(localPart, uri), [:], value))
	}
	
	void setAnnotationNode(String localPart, String uri, Node value) {
		def oldAnnotation = getAnnotationNode(localPart, uri)
		if(oldAnnotation)
			PMFUtil.getPMFAnnotation(this, 'metadata')?.remove(oldAnnotation)
		if(value)
			PMFUtil.ensurePMFAnnotation(this, 'metadata').append(value)
	}
}

package de.bund.bfr.pmf.numl;

import org.apache.log4j.Level
import org.sbml.jsbml.Model
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.Unit

import de.bund.bfr.numl.ConformityMessage
import de.bund.bfr.numl.OntologyTerm
import de.bund.bfr.pmf.PMFUtil
import de.bund.bfr.pmf.sbml.PMFCompartment
import de.bund.bfr.pmf.sbml.PMFSpecies
import de.bund.bfr.pmf.sbml.PMFUnitDefinition
import de.bund.bfr.pmf.sbml.SBMLAdapter

/**
 * Extends the {@link OntologyTerm} to allow the annotation of unit, compartment, and species.
 */
class PMFOntologyTerm extends OntologyTerm implements AnnotatedNuML {
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
		<sbml xmlns='http://www.sbml.org/sbml/level3/version1/core' level='3' version='1' %s>
			%s
		</sbml>
	"""
	
	protected void setSbml(Model sbml) {
		if (sbml == null)
			throw new NullPointerException("sbml must not be null");

		def doc = new SBMLDocument(document?.level ?: 3, document?.version ?: 1)
		doc.model = sbml
		def xmlString = new SBMLAdapter().toString(doc)
		def node = new XmlParser().parseText(xmlString)
		def metadata = PMFUtil.ensurePMFAnnotation(this)
		['listOfUnitDefinitions', 'listOfCompartments', 'listOfSpecies'].each { elem -> 
			setAnnotationNode(elem, PMFUtil.SBML_NS, node.model."$elem"[0])
		}
		this.sbml = sbml
		this.sbmlReader = null
	}
	
	protected Model getSbml() {
		if(!this.sbml)
			this.sbml = getSbmlReader()?.document?.model
		this.sbml
	}
	
	protected SBMLAdapter getSbmlReader() {
		if(this.sbmlReader)
			return sbmlReader
			
		def sbmlMetaData = PMFUtil.getPMFAnnotation(this)
		def sbmlXml = new StringWriter()
		if(sbmlMetaData) {
			sbmlMetaData = sbmlMetaData.clone()
			PMFUtil.addStandardPrefixes(sbmlMetaData)
			sbmlMetaData.name = new groovy.xml.QName(PMFUtil.SBML_NS, 'model')
			
			sbmlXml.withPrintWriter { writer ->
				def printer = new XmlNodePrinter(new IndentPrinter(writer, '', false, false))
				printer.preserveWhitespace = true
				printer.print(sbmlMetaData)
			}
		}
		def sbmlMockup = String.format(sbmlTemplate, PMFUtil.standardPrefixesDeclarations, sbmlXml.toString())
		
		sbmlReader = new SBMLAdapter(validating: true)
		sbmlReader.parseText(sbmlMockup)
		sbmlReader
	}
	
	@Override
	Map<String, Object> getPropertyValues() {
		super.getPropertyValues(OntologyTerm.class.metaClass)
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
	
	@Override
	void setOriginalNode(Node originalNode) {
		super.setOriginalNode(originalNode)
		this.sbml = null
		this.sbmlReader = null
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
		
		def subPrefix = "$prefix/ontologyTerms/ontologyTerm/annotation"
		if(unitDefinition)
			messages.addAll(unitDefinition.getInvalidSettings(getSbmlReader().document, "$subPrefix", null))
		
		if(species)
			messages.addAll(species.getInvalidSettings(getSbmlReader().document, "$subPrefix", null))
		else if(unitDefinition?.isVariantOfSubstancePerVolume())		
			messages << new ConformityMessage(level: Level.WARN,
				message: "$subPrefix: ontology term ${id ?: term} seem to denote a concentration and should have a species annotation (Specification 13)")
			
		if(!species && !unitDefinition)
			messages << new ConformityMessage("$prefix: ontology term ${id ?: term} must have a unitDefinition or a species annotation (Specification 13)")
			
		if(compartment)
			messages.addAll(compartment.getInvalidSettings(getSbmlReader().document, "$subPrefix/compartment", null))
			
		messages
	}
	
	void replace(OntologyTerm ontologyTerm) {
		def index = this.document.ontologyTerms.findIndexOf { it.is(ontologyTerm) }
		this.document.ontologyTerms.set(index, this)
		this.document.resultComponents.each { rc ->
			PMFUtil.traverse(rc.dimensionDescription) { desc ->
				if(ontologyTerm.is(desc.ontologyTerm))
					desc.ontologyTerm = this
				desc
			}
		}
	}
}
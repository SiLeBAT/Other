package de.bund.bfr.pmf.sbml;

import groovy.transform.InheritConstructors

import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.UnitDefinition
import org.sbml.jsbml.xml.XMLNode

import de.bund.bfr.numl.ConformityMessage
import de.bund.bfr.pmf.PMFDocument
import de.bund.bfr.pmf.PMFUtil

@InheritConstructors
class PMFUnitDefinition extends UnitDefinition implements SBMLReplacement {
	{
		initLevelAndVersion()
	}
	
	void setTransformation(String transformation) {
		if (transformation == null)
			throw new NullPointerException("transformation must not be null");

		XMLNode pmfTransformation = PMFUtil.ensurePMFAnnotation(this, 'transformation')
		pmfTransformation.addAttr('name', transformation)
		pmfTransformation.addAttr('href', 'http://sourceforge.net/projects/microbialmodelingexchange/files/Units/PMF-ML_UnitTransformations.xlsx', PMFUtil.XLINK_NS)
	}
	
	@Override
	UnitDefinition clone() {
		new PMFUnitDefinition(this)
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
				messages << new ConformityMessage("$prefix: Transformation of PMF unit $id must contain name (Specification 8)")
			if(!transformation.getAttrValue('href', PMFUtil.XLINK_NS))
				messages << new ConformityMessage("$prefix: Transformation of PMF unit $id must refer to PMF units (Specification 8)")
		}
		messages
	}
}
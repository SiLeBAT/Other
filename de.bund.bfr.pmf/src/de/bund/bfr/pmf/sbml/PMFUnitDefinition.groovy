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
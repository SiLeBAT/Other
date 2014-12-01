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



import org.apache.log4j.Level
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.ext.comp.CompModelPlugin
import org.sbml.jsbml.ext.comp.CompSBMLDocumentPlugin

import de.bund.bfr.gnuml.ConformityMessage

/**
 * 
 */
enum ValidationRule {
	Num_Models() {
		def validate(PMFDocument document, List<ConformityMessage> messages) {
			if((document.models*.value*.model - [null]).size() != 1)
				messages << new ConformityMessage('Each PMF-ML document describes exactly one model (Specification 2)')
		}
	},
	SBML_v3() {
		def validate(PMFDocument document, List<ConformityMessage> messages) {
			document.models.each { name, SBMLDocument modelDoc ->
				if(modelDoc.level != 3)
					messages << new ConformityMessage("$name: To encode food related models SBML Level 3 has to be applied (Specification 3)")
			}
		}
	},
	Hierarchical_Model_Composition() {
		def validate(PMFDocument document, List<ConformityMessage> messages) {
			document.models.each { name, SBMLDocument modelDoc ->
				CompModelPlugin compModel = modelDoc.model.getExtension(ValidationRule.COMP_NS)
				CompSBMLDocumentPlugin compDoc = modelDoc.getExtension(ValidationRule.COMP_NS)
				if((compDoc == null) != (compModel == null)) {
					messages << new ConformityMessage("$name: Comp extension loaded unevenly (specification 3)")
					return
				}

				compModel?.listOfSubmodels.each { submodel ->
					if(!compDoc.listOfModelDefinitions.find { it.id == submodel.modelRef })
						messages << new ConformityMessage("$name: Referenced submodel ${submodel.modelRef} not found (Specification 4)")
				}
			}
		}
	},
	SBML_Component_Compartments() {
		def validate(PMFDocument document, List<ConformityMessage> messages) {
			document.models.each { name, SBMLDocument modelDoc ->
				if(!modelDoc.model.listOfCompartments)
					messages << new ConformityMessage("$name: Model must contain compartment (food matrix) (Specification 6)")
				modelDoc.model.listOfCompartments.each { compartment ->
					def pmfMetaData = PMFUtil.getPMFAnnotation(compartment, "metadata")
					def dcSource = pmfMetaData?.getChildElement('source', DC_NS)
					if(!pmfMetaData || !dcSource)
						messages << new ConformityMessage(level: Level.WARN,
							message: "$name: Compartment $compartment.id should contain PMF metadata annotation with source (Specification 6)")
					else {
						def uri = dcSource.find { it.text }.characters
						try {
							new URI(uri)
						} catch(e) {
							messages << new ConformityMessage("$name: Compartment $compartment.id has invalid source descriptor $uri (Specification 6)")
						}
					}
				}
			}
		}
	};
	//	NuML_Units('All atomic NuML descriptors must reference an SBML/PMF unit') {
	//		def validate(PMFDocument document, List<ConformityMessage> messages) {
	//
	//			document*.experiments*.value*.resultComponents*.
	//		}
	//	}


	static COMP_NS = 'http://www.sbml.org/sbml/level3/version1/comp/version1'
	
	static DC_NS = 'http://purl.org/dc/elements/1.1/'

	abstract validate(PMFDocument document, List<ConformityMessage> messages)
}

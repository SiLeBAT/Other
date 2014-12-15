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



import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

import org.apache.log4j.Level
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.ext.comp.CompModelPlugin
import org.sbml.jsbml.ext.comp.CompSBMLDocumentPlugin
import org.xml.sax.InputSource

import de.bund.bfr.numl.AtomicDescription
import de.bund.bfr.numl.ConformityMessage
import de.bund.bfr.numl.NuMLDocument

/**
 * 
 */
enum ValidationRule {
	Num_Models() {
		def validate(PMFDocument document, List<ConformityMessage> messages) {
			document.models.each { name, SBMLDocument modelDoc ->
				if(!modelDoc.model)
					messages << new ConformityMessage("$name: Each PMF model document describes exactly one model (Specification 2)")
			}
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
				CompModelPlugin compModel = modelDoc.model.getExtension(PMFUtil.COMP_NS)
				CompSBMLDocumentPlugin compDoc = modelDoc.getExtension(PMFUtil.COMP_NS)
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
	SBML_Constraints() {
		def validate(PMFDocument document, List<ConformityMessage> messages) {
			document.models.each { name, SBMLDocument modelDoc ->
				modelDoc.model.listOfParameters.findAll { it.constant == "false" }.each { parameter ->
					if(!modelDoc.model.listOfConstraints.find { contraint ->
						".*<ci>\\s*$parameter.id\\s*</ci>.*" ==~ contraint.messageString
					})
						messages << new ConformityMessage(level: Level.WARN,
							message: "$name: Variable parameter $parameter.id should be bound by constraints (Specification 10)")
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



	abstract validate(PMFDocument document, List<ConformityMessage> messages)
}

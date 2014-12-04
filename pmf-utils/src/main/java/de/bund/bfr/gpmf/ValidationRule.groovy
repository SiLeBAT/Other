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



import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

import org.apache.log4j.Level
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.ext.comp.CompModelPlugin
import org.sbml.jsbml.ext.comp.CompSBMLDocumentPlugin
import org.xml.sax.InputSource

import de.bund.bfr.gnuml.AtomicDescription
import de.bund.bfr.gnuml.ConformityMessage
import de.bund.bfr.gnuml.NuMLDocument

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
	SBML_Parameters() {
		def validate(PMFDocument document, List<ConformityMessage> messages) {
			document.models.each { name, SBMLDocument modelDoc ->
				modelDoc.model.listOfParameters.each { parameter ->
					messages.addAll(['value', 'constant'].findAll { !parameter."$it" }.collect { 
						new ConformityMessage("$name: Parameter $parameter.id must contain $it (Specification 9)") 
					})
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
	},
	SBML_Annotation_of_metadata() {
		def validate(PMFDocument document, List<ConformityMessage> messages) {
			[model: model, dataSet: resultComponent].each { fileType, annotationElem ->
				document."${fileType}s".each { name, doc ->
					def docMetaData = PMFUtil.getPMFAnnotation(doc, "metadata")					
					if(docMetaData)
						messages << new ConformityMessage(level: Level.WARN,
							message: "$name: Instead of annotating the document, it is recommended to annotate the $annotationElem (Specification 11/13)")
					
					doc."$annotationElem".each { elem ->
						def elemMetaData = PMFUtil.getPMFAnnotation(elem, "metadata")
						if(!elemMetaData)
							messages << new ConformityMessage(level: Level.WARN,
								message: "$name: ${annotationElem}s should be annotated (Specification 11/13)")
						// check if we know all the meta data elements
						if(docMetaData || elemMetaData) {
							def metaData = docMetaData + elemMetaData
							// all expected elements present?
							PMFUtil.BaseAnnotations.each { nameSpace, annotationName ->							
								def annotation = metaData.getChildElement(annotationName, nameSpace)
								if(!annotation && metaData.getChildElement(annotationName))
									messages << new ConformityMessage(
										"$name: Annotation $annotationName of $annotationElem ${elem.id} is not of expected namespace $nameSpace (Specification 11/13)")
								else if(!annotation)
									messages << new ConformityMessage(level: Level.WARN,
										message: "$name: Recommend annotation $annotationName of $annotationElem ${elem.id} not present (Specification 11/13)")
							}
						}
					}
				}
			}
		}
	},
	SBML_Annotation_of_Uncertainty() {
		def validate(PMFDocument document, List<ConformityMessage> messages) {
			document.models.each { name, SBMLDocument modelDoc ->
				def docMetaData = PMFUtil.getPMFAnnotation(modelDoc, "metadata")
				if(!docMetaData?.getChildElement('modelquality', PMFUtil.PMML_NS))
					messages << new ConformityMessage(level: Level.WARN,
						message: "$name: model quality should be annotated (Specification 12)")
			}
		}
	},
	NuML_Integration() {
		def sbmlTemplate = """
			<sbml xmlns='http://www.sbml.org/sbml/level2/version4' level='2' version='1'>
				<model id='test'>
					%s
				</model>
			</sbml>
		"""
		
		def validate(PMFDocument document, List<ConformityMessage> messages) {
			document.dataSets.each { name, NuMLDocument modelDoc ->
				// we already validated the common annotations in the NuML, now we just look at the specific NuML annotations 				
				modelDoc.resultComponents.each { rc ->
					def unitDefs = validateSbmlSubDoc(rc, messages)					
					validateUnits(rc.dimensionDescription, rc.id, unitDefs, messages)
				}
			}
			// check if references of SBML models to NuML datasets is correct
			if(document.dataSets)
				document.models.each { name, SBMLDocument doc ->
					doc.model.each { model ->
						def modelMetaData = PMFUtil.getPMFAnnotation(model, 'metadata')
						def dataSourcesElem = modelMetaData.getChildElements('dataSources', PMFUtil.PMF_NS)
						if(dataSourcesElem?.size() != 1)
							messages << new ConformityMessage("$name: model $model.id must reference dataSets with exactly one pmf:dataSources element (Specification 13)")
						def dataSources = dataSourcesElem[0]?.getChildElements('dataSource', PMFUtil.PMF_NS)
						if(!dataSources)
							messages << new ConformityMessage("$name: model $model.id must reference dataSets with at least one pmf:dataSource element (Specification 13)")
						
						Map<String, NuMLDocument> sources = [:]
						dataSources?.eachWithIndex { dataSource, index ->
							def id = dataSource.getAttrValue('id')
							if(!dataSources)
								messages << new ConformityMessage("$name: model $model.id: the $index pmf:dataSource element contains no id (Specification 13)")
							def fileName = dataSource.getAttrValue('href', PMFUtil.XLINK_NS)
							if(!fileName)
								messages << new ConformityMessage("$name: model $model.id: the $index pmf:dataSource element contains no xlink:href (Specification 13)")
							if(id && fileName) {
								def dataSet = document.dataSets[fileName]
								if(!dataSet)
									messages << new ConformityMessage("$name: model $model.id: pmf:dataSource $id refers to unknown file $fileName, available ${document.dataSets.keySet()} (Specification 13)")
								else
									sources[id] = dataSet
							}
						}
						
						model.listOfParameters.each { parameter ->  
							def sourceAnnotation = PMFUtil.getPMFAnnotation(parameter, 'sourceValues')
							if(!sourceAnnotation)
								messages << new ConformityMessage("$name: model $model.id must reference dataSets with at least one pmf:dataSource element (Specification 13)")
							else {
								def sourceId = sourceAnnotation.getAttrValue('source')
								def source = sources[sourceId]
								if(!sourceId)
									messages << new ConformityMessage("$name: model $model.id: parameter $parameter.id does not refer to a source (Specification 13)")
								else if(!source)
									messages << new ConformityMessage("$name: model $model.id: parameter $parameter.id refers to unknown source $sourceId (Specification 13)")
								
								def descriptor = sourceAnnotation.getAttrValue('descriptor')
								def descriptorXPath
								if(!descriptor)
									messages << new ConformityMessage("$name: model $model.id: parameter $parameter.id does not contain a source value descriptor (Specification 13)")
								else {
									javax.xml.xpath.XPath xpath = XPathFactory.newInstance().newXPath()
									try {
										descriptorXPath = xpath.compile(descriptor)
									} catch(e) {
										messages << new ConformityMessage("$name: model $model.id: parameter $parameter.id contains invalid source value descriptor (Specification 13): $e.message")
									}
								}
								
								if(source && descriptorXPath) {
									def inputStream = document.documentStreamFactories[source]()
									try {
										NodeList nodes = descriptorXPath.evaluate(new InputSource(inputStream), XPathConstants.NODESET)
										if(!nodes)
											messages << new ConformityMessage("$name: model $model.id: the evaluation of the descriptor of parameter $parameter.id yields no result (Specification 13): $e.message")
									} catch(e) {
										messages << new ConformityMessage("$name: model $model.id: the evaluation of the descriptor of parameter $parameter.id yields an error (Specification 13): $e.message")
									}
								}
							}
						}						
					}
				}
		}
		
		def validateSbmlSubDoc(rc, messages) {
			def unitDefs = []
			def sbmlMetaData = PMFUtil.getPMFAnnotation(rc, 'sbml')
			if(!sbmlMetaData)
				messages << new ConformityMessage("$name: resultComponent $rc.id must be annotated with pmf:sbml (Specification 13)")
			else {
				def sbmlMockup = String.format(sbmlTemplate, sbmlMetaData.toXMLString())
				def reader = new SBMLAdapter(validating: true)
				def sbmlSubDoc = reader.parseText(sbmlMockup)
				
				messages.addAll(reader.parseMessages.collect {
					it.message = "$name: resultComponent $rc.id: pmf:sbml: $it.message"
				})
				
				if(sbmlSubDoc) {
					if(!sbmlSubDoc.model.listOfCompartments)
						messages << new ConformityMessage("$name: resultComponent $rc.id: pmf:sbml must have at least one compartment (Specification 13)")
					if(!sbmlSubDoc.model.listOfSpecies)
						messages << new ConformityMessage("$name: resultComponent $rc.id: pmf:sbml must have at least one species (Specification 13)")
					if(!(unitDefs = sbmlSubDoc.model.listOfUnitDefinitions))
						messages << new ConformityMessage("$name: resultComponent $rc.id: pmf:sbml must have at least one unit definition (Specification 13)")
				}
			}
			unitDefs
		}
		
		def validateUnits(descriptor, path, unitDefs, messages) {
			if(descriptor instanceof AtomicDescription) {
				def unitRef = PMFUtil.getPMFAnnotation(descriptor, 'unit')
				if(!unitRef)
					messages << new ConformityMessage("$name: resultComponent $rc.id: atomic value must have pmf:unit annotation (Specification 13)")
				else if(!unitDefs.find { it.id == unitRef.ref })
					messages << new ConformityMessage("$name: resultComponent $rc.id: unknown unit id $unitRef.ref in pmf:unit annotation, known units $unitDefs (Specification 13)")
			}
			descriptor.subDescriptors.each { 
				validateUnits(it, "$path/$descriptor.name[$descriptor.id]", unitDefs, messages)
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

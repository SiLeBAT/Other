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
package de.bund.bfr.pmf

import java.nio.file.Files
import java.nio.file.Path

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.sbml.jsbml.ASTNode
import org.sbml.jsbml.AssignmentRule
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.Unit

import de.bund.bfr.numl.AtomicDescription
import de.bund.bfr.numl.CompositeDescription
import de.bund.bfr.numl.DataType
import de.bund.bfr.numl.Description
import de.bund.bfr.numl.NuMLDocument
import de.bund.bfr.numl.TupleDescription
import de.bund.bfr.pmf.numl.PMFOntologyTerm
import de.bund.bfr.pmf.numl.PMFResultComponent
import de.bund.bfr.pmf.sbml.DoubleRange
import de.bund.bfr.pmf.sbml.PMFCompartment
import de.bund.bfr.pmf.sbml.PMFModel
import de.bund.bfr.pmf.sbml.PMFParameter
import de.bund.bfr.pmf.sbml.PMFSpecies
import de.bund.bfr.pmf.sbml.PMFUnitDefinition
import de.bund.bfr.pmf.sbml.SBMLAdapter

/**
 * 
 */
class PMFWriterTestBase {
	
	PMFCompartment matrix
	PMFSpecies salmonelle, staphylococcus
	Unit seconds
	PMFOntologyTerm time, salConcentration, staphConcentration
	PMFUnitDefinition logPU, logPUh
	AssignmentRule salmonellaGrowth
	PMFParameter timeParameter
	Description timeDescription, salDescription
	Path finalFile
	
	@Before
	void setup() {
		seconds = new Unit(Unit.Kind.SECOND, 3, 1)
		logPU = new PMFUnitDefinition(level: 3, version: 1, id: "pmf_log10_cfu_g", name: "log10(cfu/g)", transformation: 'log10')
		logPU.addUnit(new Unit(Unit.Kind.ITEM, 3, 1))
		logPU.addUnit(new Unit(Unit.Kind.GRAM, -1d, 3, 1))
		logPUh = new PMFUnitDefinition(level: 3, version: 1, id: "pmf_log10_cfu_g_h", name: "log10(cfu/g)/h", transformation: 'log10')
		logPUh.addUnit(new Unit(Unit.Kind.ITEM, 3, 1))
		logPUh.addUnit(new Unit(Unit.Kind.GRAM, -1d, 3, 1))
		logPUh.addUnit(new Unit(3600, 1, Unit.Kind.SECOND, -1d, 3, 1))
		
		matrix = new PMFCompartment(id: 'culture_broth', name: 'culture broth',
			source: new URI('http://identifiers.org/ncim/C0452849'))
		salmonelle = new PMFSpecies(id: 'salmonella_spp', name: 'salmonella spp',
			source: new URI('http://identifiers.org/ncim/C0036111'), compartment: matrix.id, units: logPU.id)
		staphylococcus = new PMFSpecies(id: 'staphylococcus_aureus', name: 'staphylococcus aureus',
			source: new URI('http://identifiers.org/ncim/C0036111'), compartment: matrix.id, units: logPU.id)
		
		time = new PMFOntologyTerm(term: 'time', sourceTermId: 'SBO:0000345',
			ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'), unit: seconds)
		salConcentration = new PMFOntologyTerm(term: 'concentration', sourceTermId: 'SBO:0000196',
			ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'), unitDefinition: logPU.clone(), species: salmonelle)
		staphConcentration = new PMFOntologyTerm(term: 'concentration', sourceTermId: 'SBO:0000196',
			ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'), unitDefinition: logPU.clone(), species: staphylococcus)
		
		timeParameter = new PMFParameter(id: 'time', constant: false)
		timeParameter.setUnits(seconds)
				
		salmonellaGrowth = new AssignmentRule(salmonelle,
			ASTNode.parseFormula("Y0+YMax-ln(e^Y0 + (e^Y0-e^YMax)*e^(-mu_max * lambda * time))"))
		def doc = new SBMLDocument(3, 1)
		doc.createModel().listOfRules.add(salmonellaGrowth)
		def xmlString = new SBMLAdapter().toString(doc)
		salmonellaGrowth = new SBMLAdapter().parseText(xmlString).model.listOfRules.get(0)
		
		finalFile = Files.createTempFile('pmfTest', null)
	}
	
	@After
	void after() {
		Files.deleteIfExists(finalFile)
	}
	
	NuMLDocument createPMFNuml(boolean valid = true) {
		timeDescription = new CompositeDescription(name: 'Time', indexType: DataType.Integer, ontologyTerm: time.clone(), description:
			new TupleDescription(descriptions: [
				salDescription = new AtomicDescription(name: 'salmonelle', ontologyTerm: salConcentration.clone(), valueType: DataType.Double),
				new AtomicDescription(name: 'staphylococcus', ontologyTerm: staphConcentration.clone(), valueType: DataType.Double)
				]))
		def resultComponent = new PMFResultComponent(id: 'experiment1', dimensionDescription: timeDescription)
		resultComponent.dimension = [
			(0g): [0.11d, 0.12d],
			(3600g): [0.13d, 0.11d],
			(7200g): [0.14d, 0.10d],
			(10800g): [0.15d, 0.11d],
		]
		resultComponent.setAnnotation('author', PMFUtil.DC_NS, 'testCase')
		
		if(!valid) {
			// manipulate some values to make it invalid
			resultComponent.dimension[3600g][1] = 'a'
			salDescription.name = null
		}
		
		new NuMLDocument(resultComponents: [resultComponent])
	}
	
	SBMLDocument createPMFSBML(boolean valid = true) {
		def model = new PMFModel(3, 1)
		model.listOfCompartments.add(matrix.clone())
		model.listOfSpecies.add(salmonelle.clone())
		model.listOfUnitDefinitions.add(logPU.clone())
		model.listOfUnitDefinitions.add(logPUh.clone())
		def paramNames = ['lambda', 'mu_max', 'Ymax', 'Y0']
		def initialValues = [42.15624402963166, 0.0335496792126201, 19.81060444197425, 6.147902198294101]
		def units = ['dimensionless', logPUh, logPU, logPU]
		paramNames.eachWithIndex { constant, index ->
			def param = new PMFParameter(id: constant, constant: true, value: initialValues[index])
			param.setUnits(units[index]) 
			model.listOfParameters.add(param)
			
		}
		model.listOfParameters.add(timeParameter)
		model.listOfRules.add(salmonellaGrowth.clone())
		model.setAnnotation('author', PMFUtil.DC_NS, 'testCase')
		
		timeParameter.range = new DoubleRange(0, 18000)
		Assert.assertEquals(new DoubleRange(0, 18000), timeParameter.range)
		if(!valid) {
			model.listOfSpecies.get(0).units = null
			model.listOfUnitDefinitions.get(0).annotation.nonRDFannotation = null
		}
		
		def sbml = new SBMLDocument(3, 1)
		sbml.model = model
		sbml
	}
}

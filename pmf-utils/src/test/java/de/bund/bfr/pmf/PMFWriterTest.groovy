
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

import static org.junit.Assert.*

import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipFile

import org.junit.After
import org.junit.Before
import org.junit.Test
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

/**
 * Test {@link PMFWriter}.
 */
class PMFWriterTest {
	PMFCompartment matrix
	PMFSpecies salmonelle, staphylococcus
	Unit seconds
	PMFOntologyTerm time, salConcentration, staphConcentration
	PMFUnitDefinition logPU
	AssignmentRule salmonellaGrowth
	PMFParameter timeParameter
	Description timeDescription, salDescription
	Path finalFile = Files.createTempFile('pmfTest', null)
	
	@Before
	void setup() {		
		seconds = new Unit(Unit.Kind.SECOND, 3, 1)
		logPU = new PMFUnitDefinition(level: 3, version: 1, id: "pmf_log10_cfu_g", name: "log10(cfu/g)", transformation: 'log10')
		logPU.addUnit(new Unit(Unit.Kind.ITEM, 3, 1))
		logPU.addUnit(new Unit(Unit.Kind.GRAM, -1d, 3, 1))
		
		matrix = new PMFCompartment(id: 'culture_broth', name: 'culture broth',
			source: new URI('http://identifiers.org/ncim/C0452849'))
		salmonelle = new PMFSpecies(id: 'salmonella_spp', name: 'salmonella spp',
			source: new URI('http://identifiers.org/ncim/C0036111'), compartment: matrix.id, units: logPU)
		staphylococcus = new PMFSpecies(id: 'staphylococcus_aureus', name: 'staphylococcus aureus',
			source: new URI('http://identifiers.org/ncim/C0036111'), compartment: matrix.id, units: logPU)
		
		time = new PMFOntologyTerm(term: 'time', sourceTermId: 'SBO:0000345',
			ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'), unit: seconds)
		salConcentration = new PMFOntologyTerm(term: 'concentration', sourceTermId: 'SBO:0000196',
			ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'), species: salmonelle)
		staphConcentration = new PMFOntologyTerm(term: 'concentration', sourceTermId: 'SBO:0000196',
			ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'), species: staphylococcus)
		
		salmonellaGrowth = new AssignmentRule(salmonelle, 
			ASTNode.parseFormula("Y0+YMax-ln(e^Y0 + (e^Y0-e^YMax)*e^(-mu_max * lambda * time))"))
		timeParameter = new PMFParameter(id: 'time', constant: false, units: seconds)
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
			(0): [0.11d, 0.12d],
			(3600): [0.13d, 0.11d],
			(7200): [0.14d, 0.10d],
			(10800): [0.15d, 0.11d],
		]
		resultComponent.setAnnotation('author', PMFUtil.DC_NS, 'testCase')
		
		if(!valid) {
			// manipulate some values to make it invalid
			resultComponent.dimension[3600][1] = 'a'
			salDescription.name = null
		}
		
		new NuMLDocument(resultComponents: [resultComponent])
	}
	
	SBMLDocument createPMFSBML(boolean valid = true) {
		def model = new PMFModel(3, 1)
		model.listOfCompartments.add(matrix.clone())
		model.listOfSpecies.add(salmonelle.clone())
		model.listOfUnitDefinitions.add(logPU.clone())
		[lambda: 42.15624402963166, mu_max: 0.0335496792126201, Ymax: 19.81060444197425, Y0: 6.147902198294101].each { constant, value ->
			model.listOfParameters.add(new PMFParameter(id: constant, constant: true, value: value))
		}
		model.listOfParameters.add(timeParameter)
		model.listOfRules.add(salmonellaGrowth)
		model.setAnnotation('author', PMFUtil.DC_NS, 'testCase')
		
		if(!valid) {
			model.listOfSpecies.get(0).units = null
			model.listOfUnitDefinitions.get(0).annotation.nonRDFannotation = null
		}
		
		def sbml = new SBMLDocument(3, 1)
		sbml.model = model
		sbml
	}
	
	@Test
	void shouldGenerateDatasetOnlyPMF() {
		def dataset = createPMFNuml()
		def doc = new PMFDocument(dataSets: ['concentrations.xml': dataset])

		new PMFWriter().write(doc, finalFile)
		assertNotEquals(0, Files.size(finalFile))
		def zipFile = new ZipFile(finalFile.toFile())
		assertEquals(1, zipFile.size())
		assertNotNull(zipFile.getEntry('concentrations.xml'))
		zipFile.close()
	}
	
	@Test
	void shouldGenerateModelOnlyPMF() {
		def model = createPMFSBML()		
		def doc = new PMFDocument(models: ['salModel.xml': model])

		new PMFWriter().write(doc, finalFile)
		assertNotEquals(0, Files.size(finalFile))
		def zipFile = new ZipFile(finalFile.toFile())
		assertEquals(1, zipFile.size())
		assertNotNull(zipFile.getEntry('salModel.xml'))
		zipFile.close()
	}
	
	@Test
	void shouldGeneratePMFFromValidParts() {
		def dataset = createPMFNuml()
		def model = createPMFSBML()
		PMFModel pmfModel = model.model
		pmfModel.setDataSource('data', 'concentrations.xml')
		timeParameter.setSourceValue('data', timeDescription)
		def doc = new PMFDocument(dataSets: ['concentrations.xml': dataset], models: ['salModel.xml': model])
		
		new PMFWriter().write(doc, finalFile)
		assertNotEquals(0, Files.size(finalFile))
		def zipFile = new ZipFile(finalFile.toFile())
		assertEquals(2, zipFile.size())
		assertNotNull(zipFile.getEntry('salModel.xml'))
		assertNotNull(zipFile.getEntry('concentrations.xml'))
		zipFile.close()
	}
	
	@Test
	void shouldNotGeneratePMFFromInvalidDataset() {	
		def dataset = createPMFNuml(false)
		def model = createPMFSBML()
		PMFModel pmfModel = model.model
		pmfModel.setDataSource('data', 'concentrations.xml')
		timeParameter.setSourceValue('data', timeDescription)
		def doc = new PMFDocument(dataSets: ['concentrations.xml': dataset], models: ['salModel.xml': model])
		
		try {
			new PMFWriter().write(doc, finalFile)
			fail('Should not succeed')
		} catch(PMFException e) {
			assertEquals(2, e.errors.size())
		}
	}
	
	@Test
	void shouldNotGeneratePMFFromInvalidModel() {	
		def dataset = createPMFNuml()
		def model = createPMFSBML(false)
		PMFModel pmfModel = model.model
		pmfModel.setDataSource('data', 'concentrations.xml')
		timeParameter.setSourceValue('data', timeDescription)
		def doc = new PMFDocument(dataSets: ['concentrations.xml': dataset], models: ['salModel.xml': model])
		
		try {
			new PMFWriter().write(doc, finalFile)
			fail('Should not succeed')
		} catch(PMFException e) {
		println e.errors
			assertEquals(2, e.errors.size())
		}
		assertEquals(0, Files.size(finalFile))
	}
}

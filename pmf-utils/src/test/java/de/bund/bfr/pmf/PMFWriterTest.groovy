
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

import org.junit.Before;
import org.junit.Test
import org.sbml.jsbml.Unit

import de.bund.bfr.numl.AtomicDescription
import de.bund.bfr.numl.CompositeDescription
import de.bund.bfr.numl.DataType
import de.bund.bfr.numl.NuMLDocument
import de.bund.bfr.numl.TupleDescription

/**
 * Test {@link PMFWriter}.
 */
class PMFWriterTest {
	PMFCompartment matrix
	PMFSpecies salmonelle, licentria
	PMFOntologyTerm time, salConcentration, licConcentration
	
	@Before
	void setup() {		
		matrix = new PMFCompartment(id: 'culture_broth', name: 'culture broth',
			source: new URI('http://identifiers.org/ncim/C0452849'))
		salmonelle = new PMFSpecies(id: 'salmonella_spp', name: 'salmonella spp',
			source: new URI('http://identifiers.org/ncim/C0036111'), compartment: matrix.id)
		// TODO:
		licentria = new PMFSpecies(id: 'licentria_spp', name: 'licentria spp',
			source: new URI('http://identifiers.org/ncim/C0036111'), compartment: matrix.id)
		
		time = new PMFOntologyTerm(term: 'time', sourceTermId: 'SBO:0000345',
			ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'), unit: new Unit(Unit.Kind.SECOND, 3, 1))
		def logPU = new PMFUnitDefinition(level: 3, version: 1, id: "pmf_log10_cfu_g", name: "log10(cfu/g)", transformation: 'log10')
		logPU.addUnit(new Unit(Unit.Kind.ITEM, 3, 1))
		logPU.addUnit(new Unit(Unit.Kind.GRAM, -1d, 3, 1))
		salConcentration = new PMFOntologyTerm(term: 'concentration', sourceTermId: 'SBO:0000196',
			ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'), unitDefinition: logPU, species: salmonelle)
		licConcentration = new PMFOntologyTerm(term: 'concentration', sourceTermId: 'SBO:0000196',
			ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'), unitDefinition: logPU, species: licentria)
	}
	
	NuMLDocument createPMFNuml(boolean valid = true) {		
		def description = new CompositeDescription(name: 'Time', indexType: DataType.Integer, ontologyTerm: time.clone(), description:
			new TupleDescription(descriptions: [
				new AtomicDescription(name: 'salmonelle', ontologyTerm: salConcentration.clone(), valueType: DataType.Double),
				new AtomicDescription(name: 'licentria', ontologyTerm: licConcentration.clone(), valueType: DataType.Double)
				]))
		def resultComponent = new PMFResultComponent(id: 'experiment1', dimensionDescription: description)
		resultComponent.dimension = [
			(0): [0.11d, 0.12d],
			(1): [0.13d, 0.11d],
			(2): [0.14d, 0.10d],
			(3): [0.15d, 0.11d],
		]
		
		new NuMLDocument(resultComponents: [resultComponent])
	}
	
	@Test
	void shouldGenerateDatasetOnlyPMF() {
		def dataset = createPMFNuml()
		def doc = new PMFDocument(dataSets: ['salCons.xml': dataset])

		def finalFile = Files.createTempFile('pmfTest', null)
		new PMFWriter().write(doc, finalFile)
		assertNotEquals(0, Files.size(finalFile))
		Files.delete(finalFile)
	}
	
	@Test
	void shouldGenerateModelOnlyPMF() {
		
	}
	
	@Test
	void shouldGeneratePMFFromValidParts() {
		
	}
	
	@Test
	void shouldNotGeneratePMFFromInvalidParts() {
		
	}
}
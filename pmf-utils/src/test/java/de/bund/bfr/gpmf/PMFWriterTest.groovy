
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

import java.nio.file.Files

import org.junit.Test

import de.bund.bfr.gnuml.AtomicDescription
import de.bund.bfr.gnuml.CompositeDescription
import de.bund.bfr.gnuml.DataType
import de.bund.bfr.gnuml.NuMLDocument
import de.bund.bfr.gnuml.TupleDescription

import static org.junit.Assert.*

/**
 * Test {@link PMFWriter}.
 */
class PMFWriterTest {
	@Test
	void shouldGenerateDatasetOnlyPMF() {
		def time = new PMFOntologyTerm(term: 'time', sourceTermId: 'SBO:0000345', 
			ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'))
		def matrix = new PMFCompartment(id: 'culture_broth', name: 'culture broth', 
			source: new URI('http://identifiers.org/ncim/C0452849'))
		def salmonelle = new PMFSpecies(id: 'salmonella_spp', name: 'salmonella spp',
			source: new URI('http://identifiers.org/ncim/C0036111'), compartment: matrix)
		def salConcentration = new PMFOntologyTerm(term: 'concentration', sourceTermId: 'SBO:0000196', 
			ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'),
			species: salmonelle)
		
		def description = new CompositeDescription(name: 'Time', indexType: DataType.Integer, ontologyTerm: time, description:
			new TupleDescription(descriptions: [
				new AtomicDescription(name: 'concentration', ontologyTerm: salConcentration, valueType: DataType.Double)
				]))
		def resultComponent = new PMFResultComponent(id: 'exp1', dimensionDescription: description)
		resultComponent.dimension = [
			(0): [0.11d, 0.12d],
			(1): [0.13d, 0.11d],
			(2): [0.14d, 0.10d],
			(3): [0.15d, 0.11d],
		]
		
		def dataset = new NuMLDocument(ontologyTerms: [time, salConcentration], resultComponents: [resultComponent])
		def doc = new PMFDocument(datasets: ['salCons.xml': dataset])

		def finalFile = Files.createTempFile('pmfTest', null)
		new PMFWriter().write(doc, finalFile)
		assertNotEquals(0, Files.size(finalFile))
		Files.delete(finalFile)
	}
}

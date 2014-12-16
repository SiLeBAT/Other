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
package de.bund.bfr.numl;

import static org.junit.Assert.*

import java.nio.file.Files

import org.junit.Test

/**
 * 
 */
class NuMLWriterReaderTest {
	@Test
	void shouldReadWriteRead() throws Exception {
		String resourceFile =
			NuMLWriterReaderTest.class.getResource("/numl/TimeConcentration.xml").toURI().toString();
		NuMLDocument doc = new NuMLReader().read(resourceFile)
		
		def writtenXml = new NuMLWriter().toString(doc)		
		
		NuMLDocument doc2 = new NuMLReader().parseText(writtenXml)
		
		assertEquals(doc, doc2)
	}
	
	@Test
	void shouldWriteRead() {
		def time = new OntologyTerm(term: 'time', sourceTermId: 'SBO:0000345', ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'))
		def mass = new OntologyTerm(term: 'mass', sourceTermId: 'SBO:0000345', ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'))
		def concentration = new OntologyTerm(term: 'concentration', sourceTermId: 'SBO:0000196', ontologyURI: new URI('http://www.ebi.ac.uk/sbo/'))
		
		def description = new CompositeDescription(name: 'Time', indexType: DataType.Integer, ontologyTerm: time, description:
			new TupleDescription(descriptions: [
				new AtomicDescription(name: 'mass', ontologyTerm: mass, valueType: DataType.Double),
				new AtomicDescription(name: 'concentration', ontologyTerm: concentration, valueType: DataType.Double)
				]))
		def resultComponent = new ResultComponent(id: 'exp1', dimensionDescription: description)
		resultComponent.dimension = [
			(0g): [0.11d, 0.12d],
			(1g): [0.13d, 0.11d],
			(2g): [0.14d, 0.10d],
			(3g): [0.15d, 0.11d],
		]
		
		def doc = new NuMLDocument(resultComponents: [resultComponent])
		def finalFile = Files.createTempFile('pmfTest', null)
		new NuMLWriter().write(doc, finalFile)
		assertNotEquals(0, Files.size(finalFile))
		def doc2 = new NuMLReader().read(finalFile)
		Files.deleteIfExists(finalFile)
		assertEquals(doc, doc2)
	}
}

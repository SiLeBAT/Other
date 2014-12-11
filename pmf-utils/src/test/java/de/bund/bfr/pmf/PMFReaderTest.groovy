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

import org.apache.log4j.Level
import org.junit.Test

/**
 * 
 */
class PMFReaderTest {
	@Test
	void testInvalidNuMLFileset() {
		def reader = new PMFReader(validating: true)
		def doc = reader.readFileSet(
			PMFReaderTest.getResource('/pmf/InvalidNuMLModel.xml'), 
			PMFReaderTest.getResource('/pmf/InvalidNuMLData.xml'))
				
		// despite some errors in the NuML document, we expect a parsed result
		assertNotNull(doc)
		assertEquals(1, doc.models.size())
		assertEquals(1, doc.dataSets.size())
		
		// however, we also expect several errors
		assertEquals(34, reader.parseMessages.size())
	}
	
	@Test
	void testInvalidNuMLZip() {
		def reader = new PMFReader(validating: true)
		def doc = reader.read(PMFReaderTest.getResource('/pmf/InvalidNuML.pmf'))
				
		// despite some errors in the NuML document, we expect a parsed result
		assertNotNull(doc)
		assertEquals(1, doc.models.size())
		assertEquals(1, doc.dataSets.size())
		
		// however, we also expect several errors
		assertEquals(34, reader.parseMessages.size())
	}
	
	@Test
	void shouldSuccessfullyParseValidFileSet() {
		def dataFile = PMFReaderTest.getResource('/pmf/ValidData.xml')
		def modelFile = PMFReaderTest.getResource('/pmf/ValidModel.xml')
		def reader = new PMFReader(validating: true)
		def doc = reader.readFileSet(dataFile, modelFile)
				
		assertNotNull(doc)
		assertEquals(1, doc.models.size())
		assertEquals(1, doc.dataSets.size())
		
		assertEquals(0, reader.getParseMessages(Level.ERROR).size())
		
		// check parsed values for correctness
		def dimension = doc.dataSets[dataFile as String].resultComponents[0].dimension
		assertEquals(4, dimension.size())
		assertNotNull(dimension[73.018d])
		assertEquals(2.1, dimension[73.018d][1], 0.0001)
	}
	
	@Test
	void shouldSuccessfullyParseValidPmf() {
		def file = PMFReaderTest.getResource('/pmf/Valid.pmf')
		def reader = new PMFReader(validating: true)
		def doc = reader.read(file)
				
		assertNotNull(doc)
		assertEquals(1, doc.models.size())
		assertEquals(1, doc.dataSets.size())
		
		assertEquals(0, reader.getParseMessages(Level.ERROR).size())
		
		// check parsed values for correctness
		def dimension = doc.dataSets*.value.first().resultComponents[0].dimension
		assertEquals(4, dimension.size())
		assertNotNull(dimension[73.018d])
		assertEquals(2.1, dimension[73.018d][1], 0.0001)
	}
	
	@Test
	void shouldSuccessfullyParseValidZip() {
		def file = PMFReaderTest.getResource('/pmf/Valid.zip')
		def reader = new PMFReader(validating: true)
		def doc = reader.read(file)
				
		assertNotNull(doc)
		assertEquals(1, doc.models.size())
		assertEquals(1, doc.dataSets.size())
		
		assertEquals(0, reader.getParseMessages(Level.ERROR).size())
		
		// check parsed values for correctness
		def dimension = doc.dataSets*.value.first().resultComponents[0].dimension
		assertEquals(4, dimension.size())
		assertNotNull(dimension[73.018d])
		assertEquals(2.1, dimension[73.018d][1], 0.0001)
	}
	
	@Test
	void shouldReplaceSBMLElements() {
		def dataFile = PMFReaderTest.getResource('/pmf/ValidData.xml')
		def modelFile = PMFReaderTest.getResource('/pmf/ValidModel.xml')
		def reader = new PMFReader(validating: true)
		def doc = reader.readFileSet(dataFile, modelFile)
				
		assertNotNull(doc)
		assertEquals(1, doc.models.size())
		assertEquals(1, doc.dataSets.size())
		
		assertEquals(0, reader.getParseMessages(Level.ERROR).size())
		
		// check parsed values for correctness
		def model = doc.models[modelFile as String].model
		assertEquals(PMFModel, model.class)
		assertEquals(PMFCompartment, model.listOfCompartments[0].class)
		assertEquals(PMFSpecies, model.listOfSpecies[0].class)
		assertEquals([PMFParameter] * 5, model.listOfParameters*.class)
	}
	
	@Test
	void shouldReplaceNuMLElements() {
		def dataFile = PMFReaderTest.getResource('/pmf/ValidData.xml')
		def modelFile = PMFReaderTest.getResource('/pmf/ValidModel.xml')
		def reader = new PMFReader(validating: true)
		def doc = reader.readFileSet(dataFile, modelFile)
				
		assertNotNull(doc)
		assertEquals(1, doc.models.size())
		assertEquals(1, doc.dataSets.size())
		
		assertEquals(0, reader.getParseMessages(Level.ERROR).size())
		
		// check parsed values for correctness
		def dataset = doc.dataSets[dataFile as String]
		def rc = dataset.resultComponents[0]
		assertEquals(PMFResultComponent, rc.class)
		assertEquals([PMFOntologyTerm] * 3, dataset.ontologyTerms*.class)
	}
}
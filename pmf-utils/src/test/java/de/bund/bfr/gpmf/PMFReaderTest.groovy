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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
class PMFReaderTest {
	@Test
	void testInvalidNuML() {
		def reader = new PMFReader(validating: true)
		def doc = reader.readFileSet(
			PMFReaderTest.getResource('/gpmf/InvalidNuMLModel.xml'), 
			PMFReaderTest.getResource('/gpmf/InvalidNuMLData.xml'))
				
		// despite some errors in the NuML document, we expect a parsed result
		assertNotNull(doc)
		assertEquals(1, doc.models.size())
		assertEquals(1, doc.experiments.size())
		
		// however, we also expect several errors
		assertEquals(4, reader.parseMessages.size())
	}
	
	@Test
	void testValidFileSet() {
		def dataFile = PMFReaderTest.getResource('/gpmf/ValidData.xml')
		def modelFile = PMFReaderTest.getResource('/gpmf/ValidModel.xml')
		def reader = new PMFReader(validating: true)
		def doc = reader.readFileSet(dataFile, modelFile)
				
		assertNotNull(doc)
		assertEquals(1, doc.models.size())
		assertEquals(1, doc.experiments.size())
		
		assertEquals(0, reader.parseMessages.size())
		
		// check parsed values for correctness
		def dimension = doc.experiments[dataFile].resultComponents[0].dimension
		assertEquals(4, dimension.size())
		assertNotNull(dimension['t3'])
		assertEquals(103.965, dimension['t3'][1], 0.01)
	}
}

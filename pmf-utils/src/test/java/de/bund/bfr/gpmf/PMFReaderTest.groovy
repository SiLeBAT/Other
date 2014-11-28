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
		def doc = new PMFReader().readFileSet(
			PMFReader.getResource('/gpmf/InvalidNuMLModel.xml'), 
			PMFReader.getResource('/gpmf/InvalidNuMLData.xml'))
		
		assertEquals(1, doc.models.size())
		assertEquals(1, doc.experiments.size())
	}
}

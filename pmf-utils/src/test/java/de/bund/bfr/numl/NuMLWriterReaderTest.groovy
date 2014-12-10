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

import static org.junit.Assert.*;

import org.junit.Test;

import de.bund.bfr.numl.NuMLDocument;
import de.bund.bfr.numl.NuMLReader;
import de.bund.bfr.numl.NuMLWriter;

/**
 * 
 */
class NuMLWriterReaderTest {
	@Test
	void testReadWriteRead() throws Exception {
		String resourceFile =
			NuMLReaderTest.class.getResource("/numl/TimeConcentration.xml").toURI().toString();
		NuMLDocument doc = new NuMLReader().read(resourceFile)
		
		def writtenXml = new NuMLWriter().toString(doc)		
		
		NuMLDocument doc2 = new NuMLReader().parseText(writtenXml)
		
		assertEquals(doc, doc2)
	}
}

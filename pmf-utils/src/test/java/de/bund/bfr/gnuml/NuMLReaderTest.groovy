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
package de.bund.bfr.gnuml;

import static org.junit.Assert.*

import org.junit.Test

public class NuMLReaderTest {
	
	@Test
	public void shouldParseValidTimeConcentration() throws URISyntaxException {
		String resourceFile =
			NuMLReaderTest.class.getResource("/gnuml/TimeConcentration.xml").toURI().toString();
		NuMLDocument doc = new NuMLReader().read(resourceFile)
		assertNotNull(doc);
		assertEquals(doc.resultComponents.size(), 1)
		assertEquals(doc.resultComponents[0].dimension.size(), 2)
		assertNotNull(doc.resultComponents[0].dimension[0d])
		assertEquals(doc.resultComponents[0].dimension[0d].size(), 3)
	}
	
	@Test
	public void shouldNotParseInvalidTimeConcentration() throws URISyntaxException {
		String resourceFile =
			NuMLReaderTest.class.getResource("/gnuml/InvalidTimeConcentration.xml").toURI().toString();
		def parser = new NuMLReader(lenient: true)
		def doc = parser.read(resourceFile)
		assertNotNull(doc);
		assertEquals(doc.invalidSettings.size(), 3)
		assertEquals(parser.parseMessages.size(), 8)
	}

}

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

import java.net.URISyntaxException;

import org.junit.Test;
import static org.junit.Assert.*;

public class NuMLReaderTest {
	
	@Test
	public void shouldParseValidTimeConcentration() throws URISyntaxException {
		String resourceFile =
			NuMLReaderTest.class.getResource("/numl/TimeConcentration.xml").toURI().toString();
		NuMLDocument doc = new NuMLReader().read(resourceFile)
		assertNotNull(doc);
		assertEquals(1, doc.resultComponents.size())
		assertEquals(2, doc.resultComponents[0].dimension.size())
		assertNotNull(doc.resultComponents[0].dimension[0d])
		assertEquals(3, doc.resultComponents[0].dimension[0d].size())
	}
	
	@Test
	public void shouldNotParseInvalidTimeConcentration() throws URISyntaxException {
		String resourceFile =
			NuMLReaderTest.class.getResource("/numl/InvalidTimeConcentration.xml").toURI().toString();
		def parser = new NuMLReader(validating: true)
		def doc = parser.read(resourceFile)
		assertNotNull(doc);
		assertEquals(4, doc.invalidSettings.size())
		assertEquals(9, parser.parseMessages.size())
	}

}

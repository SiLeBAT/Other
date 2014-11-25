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

import static org.junit.Assert.*;

import java.net.URISyntaxException;

import org.junit.Test;

public class TimeConcentrationReaderTest {

	@Test
	public void shouldParseFile() throws URISyntaxException {
		String resourceFile =
			TimeConcentrationReaderTest.class.getResource("/TimeConcentration.xml").toURI().toString();
		NUMLDocument doc = new NUMLReader().parse(resourceFile);
		assertNotNull(doc);
		assertEquals(doc.resultComponents.size(), 1)
		assertEquals(doc.resultComponents[0].dimensions.size(), 2)
		assertNotNull(doc.resultComponents[0].dimensions[0d])
		assertEquals(doc.resultComponents[0].dimensions[0d].size(), 3)
	}

}

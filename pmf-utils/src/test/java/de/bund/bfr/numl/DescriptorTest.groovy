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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 */
public class DescriptionTest {
	
	@Test
	void shouldReturnInvalidSettings() {
		def root = new CompositeDescription(name: 'TimeSeries', description:
			new TupleDescription(descriptions: [
				new AtomicDescription(name: 'time', valueType: DataType.Double),
				new AtomicDescription(name: 'concentration')
				]))
		// no indexType for root and concentration
		assertEquals(2, root.invalidSettings.size())
	}
	
	/**
	 * A rather soft test for debugging.
	 */
	@Test
	void shouldPrettyPrintNesting() {
		def root = new CompositeDescription(name: 'TimeSeries', indexType: DataType.Double, description:
			new TupleDescription(descriptions: [
				new AtomicDescription(name: 'time'),
				new AtomicDescription(name: 'concentration')
				]))
		assertNotNull(root.toString())
		println root
	}
	
}

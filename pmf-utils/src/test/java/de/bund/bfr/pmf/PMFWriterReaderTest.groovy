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
package de.bund.bfr.pmf;

import static org.junit.Assert.*

import org.junit.Test

import de.bund.bfr.numl.NuMLDocument

/**
 * 
 */
class PMFWriterReaderTest extends PMFWriterTestBase {
	@Test
	void shouldReadWriteRead() throws Exception {
		def resourceFile = PMFWriterReaderTest.getResource('/pmf/Valid.zip')
		PMFDocument doc = new PMFReader().read(resourceFile)
		
		def writtenZip = new PMFWriter().toBytes(doc)		
		
		PMFDocument doc2 = new PMFReader().read(writtenZip)
		
		assertEquals(doc, doc2)
	}
	
	@Test
	void shouldWriteRead() {
		def dataset = createPMFNuml()
		def model = createPMFSBML()
		PMFModel pmfModel = model.model
		pmfModel.setDataSource('data', 'concentrations.xml')
		timeParameter.setSourceValue('data', timeDescription)
		def doc = new PMFDocument(dataSets: ['concentrations.xml': dataset], models: ['salModel.xml': model])
		
		new PMFWriter().write(doc, finalFile)
		PMFDocument doc2 = new PMFReader().read(finalFile)
		assertEquals(doc, doc2)
	}
	
}

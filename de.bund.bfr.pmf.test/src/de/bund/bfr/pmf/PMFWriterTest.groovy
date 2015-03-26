
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

import java.nio.file.Files
import java.util.zip.ZipFile

import org.junit.Test
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import de.bund.bfr.pmf.sbml.PMFModel

/**
 * Test {@link PMFWriter}.
 */
class PMFWriterTest extends PMFWriterTestBase {
	
	@Test
	void shouldGenerateDatasetOnlyPMF() {
		def dataset = createPMFNuml()
		def doc = new PMFDocument(dataSets: ['concentrations.xml': dataset])

		new PMFWriter().write(doc, finalFile)
		assertThat(0, not(Files.size(finalFile)))
		def zipFile = new ZipFile(finalFile.toFile())
		assertEquals(1, zipFile.size())
		assertNotNull(zipFile.getEntry('concentrations.xml'))
		zipFile.close()
	}
	
	@Test
	void shouldGenerateModelOnlyPMF() {
		def model = createPMFSBML()		
		def doc = new PMFDocument(models: ['salModel.xml': model])

		new PMFWriter().write(doc, finalFile)
		assertThat(0, not(Files.size(finalFile)))
		def zipFile = new ZipFile(finalFile.toFile())
		assertEquals(1, zipFile.size())
		assertNotNull(zipFile.getEntry('salModel.xml'))
		zipFile.close()
	}
	
	@Test
	void shouldGeneratePMFFromValidParts() {
		def dataset = createPMFNuml()
		def model = createPMFSBML()
		PMFModel pmfModel = model.model
		pmfModel.setDataSource('data', 'concentrations.xml')
		timeParameter.setSourceValue('data', timeDescription)
		def doc = new PMFDocument(dataSets: ['concentrations.xml': dataset], models: ['salModel.xml': model])
		
		new PMFWriter().write(doc, finalFile)
		assertThat(0, not(Files.size(finalFile)))
		def zipFile = new ZipFile(finalFile.toFile())
		assertEquals(2, zipFile.size())
		assertNotNull(zipFile.getEntry('salModel.xml'))
		assertNotNull(zipFile.getEntry('concentrations.xml'))
		zipFile.close()
	}
	
	@Test
	void shouldNotGeneratePMFFromInvalidDataset() {	
		def dataset = createPMFNuml(false)
		def model = createPMFSBML()
		PMFModel pmfModel = model.model
		pmfModel.setDataSource('data', 'concentrations.xml')
		timeParameter.setSourceValue('data', timeDescription)
		def doc = new PMFDocument(dataSets: ['concentrations.xml': dataset], models: ['salModel.xml': model])
		
		try {
			new PMFWriter().write(doc, finalFile)
			fail('Should not succeed')
		} catch(PMFException e) {
			assertThat(0, not(e.errors.size()))
		}
	}
	
	@Test
	void shouldNotGeneratePMFFromInvalidModel() {	
		def dataset = createPMFNuml()
		def model = createPMFSBML(false)
		PMFModel pmfModel = model.model
		pmfModel.setDataSource('data', 'concentrations.xml')
		timeParameter.setSourceValue('data', timeDescription)
		def doc = new PMFDocument(dataSets: ['concentrations.xml': dataset], models: ['salModel.xml': model])
		
		try {
			new PMFWriter().write(doc, finalFile)
			fail('Should not succeed')
		} catch(PMFException e) {
			assertEquals(2, e.errors.size())
		}
		assertEquals(0, Files.size(finalFile))
	}
}

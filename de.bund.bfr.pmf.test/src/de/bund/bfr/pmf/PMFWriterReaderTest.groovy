/*******************************************************************************
 * Copyright (c) 2015 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Department Biological Safety - BfR
 *******************************************************************************/
package de.bund.bfr.pmf;

import static org.junit.Assert.*

import org.junit.Test
import org.sbml.jsbml.Annotation
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.SBase
import org.sbml.jsbml.xml.XMLNode

import de.bund.bfr.pmf.sbml.PMFModel

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
		
		exchangeExtraNamespaces(doc, doc2)
		assertEquals(doc, doc2)
	}
	
	@Test
	void shouldReadWriteReadAsString() throws Exception {
		def resourceFile = PMFWriterReaderTest.getResource('/pmf/Valid.zip')
		PMFDocument doc = new PMFReader().read(resourceFile)
		
		def namedStrings = new PMFWriter().toStrings(doc)		
		
		PMFDocument doc2 = new PMFReader().readFileSet(namedStrings)
		
		exchangeExtraNamespaces(doc, doc2)
		assertEquals(doc, doc2)
	}
	
	void exchangeExtraNamespaces(SBase base1, SBase base2) {		
		base1.declaredNamespaces.each { prefix, uri ->
			if(!base2.declaredNamespaces.containsKey(prefix))
				base2.addDeclaredNamespace(prefix, uri)
		}
		base2.declaredNamespaces.each { prefix, uri ->
			if(!base1.declaredNamespaces.containsKey(prefix))
				base1.addDeclaredNamespace(prefix, uri)
		}
	}
	
	void exchangeExtraNamespaces(PMFDocument doc, PMFDocument doc2) {
		doc2.models.each { name, model2 ->
			def model1 = doc.models[name]
			if(model1) {
				exchangeExtraNamespaces(model1, model2)
				model1.model.listOfConstraints.eachWithIndex { c, index ->
					exchangeExtraNamespaces(c, model2.model.listOfConstraints[index])
				}
				model1.SBMLDocumentAttributes.each { key, value ->
					if(!model2.SBMLDocumentAttributes.containsKey(key))
						model2.SBMLDocumentAttributes[key] = value
				}
				model2.SBMLDocumentAttributes.each { key, value ->
					if(!model1.SBMLDocumentAttributes.containsKey(key))
						model1.SBMLDocumentAttributes[key] = value
				}
			}
		}
	}
	
	@Test
	void shouldWriteRead() {
		def dataset = createPMFNuml()
		SBMLDocument model = createPMFSBML()
		PMFModel pmfModel = model.model
		pmfModel.setDataSource('data', 'concentrations.xml')
		timeParameter.setSourceValue('data', timeDescription)
		def doc = new PMFDocument(dataSets: ['concentrations.xml': dataset], models: ['salModel.xml': model])
		
		removeWhitespaceChildren(doc)
		def namedStrings = new PMFWriter().toStrings(doc)
		PMFDocument doc2 = new PMFReader().readFileSet(namedStrings)
		removeWhitespaceChildren(doc2)
		exchangeExtraNamespaces(doc, doc2)
		assertEquals(doc, doc2)
		assertEquals(timeParameter.range, doc2.models['salModel.xml'].model.listOfParameters.find { it.id == 'time' }?.range)
	}
	
	void removeWhitespaceChildren(PMFDocument doc) {
		doc.models.each { name, model ->
			PMFUtil.traverse(model, { node ->
				if(node instanceof Annotation) 
					removeWhitespaceChildren(node.nonRDFannotation)
				node
			})
		}
	}
	
	void removeWhitespaceChildren(XMLNode node) {
		def index = node.childCount - 1
		while(index >= 0)
			removeWhitespaceChildren(node.getChildAt(index--))
		
		if((node.text && node.characters.trim().empty) ||
			(!node.text && node.attributes.length == 0 && node.childCount == 0)) {
			def parentIndex = (0..<node.parent.childCount).find { node.parent.getChildAt(it).is (node) }
			node.parent.removeChild(parentIndex)
		}
	}
}

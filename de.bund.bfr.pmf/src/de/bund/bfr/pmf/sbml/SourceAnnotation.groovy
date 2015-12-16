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
package de.bund.bfr.pmf.sbml;

import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.xml.XMLNode
import org.sbml.jsbml.xml.XMLTriple

import de.bund.bfr.numl.ConformityMessage
import de.bund.bfr.pmf.PMFDocument
import de.bund.bfr.pmf.PMFUtil
import org.apache.log4j.Level

trait SourceAnnotation extends SBMLReplacement {
	void setSource(URI source) {
		if (source == null)
			throw new NullPointerException("source must not be null");

		XMLNode pmfMetaData = PMFUtil.ensurePMFAnnotation(this)
		def dcSource = pmfMetaData.getChildElement('source', PMFUtil.DC_NS)
		if(!dcSource)
			pmfMetaData.addChild(
					dcSource = new XMLNode(new XMLTriple('source', PMFUtil.DC_NS, null)))
		dcSource.removeChildren()
		dcSource.addChild(new XMLNode(source.toString()))
	}

	URI getSource() {
		def pmfMetaData = PMFUtil.getPMFAnnotation(this)
		def dcSource = pmfMetaData?.getChildElement('source', PMFUtil.DC_NS)
		def uri = dcSource?.find { it.text }?.characters
		if(uri)
			try {
				return new URI(uri)
			} catch(e) {
			}
		null
	}

	List<ConformityMessage> getInvalidSettings(SBMLDocument document, String prefix, PMFDocument pmf) {
		if(!source)
			return [
				new ConformityMessage(level: Level.WARN,
				message: "$prefix: $elementName $id should contain PMF metadata annotation with source (Specification 6/7)")
			]
		[]
	}
}


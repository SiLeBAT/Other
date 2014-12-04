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

import groovy.transform.InheritConstructors

import org.apache.log4j.Level
import org.sbml.jsbml.Model
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.SBMLWriter
import org.sbml.jsbml.Unit

import de.bund.bfr.gnuml.AtomicDescription
import de.bund.bfr.gnuml.CompositeDescription
import de.bund.bfr.gnuml.ConformityMessage
import de.bund.bfr.gnuml.NuMLDocument
import de.bund.bfr.gnuml.ResultComponent
import de.bund.bfr.gnuml.TupleDescription

@InheritConstructors
class PMFResultComponent extends ResultComponent {
	final static sbmlTemplate = """
		<sbml xmlns='http://www.sbml.org/sbml/level2/version4' level='2' version='1'>
			<model id='test'>
				%s
			</model>
		</sbml>
	"""
	
	PMFResultComponent(ResultComponent rc) {
		// rather inefficient but versatile
		setParent(rc.parent)
		setOriginalNode(rc.originalNode)
	}
		
	void setSbml(Model sbml) {
		if (sbml == null)
			throw new NullPointerException("sbml must not be null");

		def doc = new SBMLDocument(sbml.version, sbml.model)
		doc.model = sbml
		def baos = new ByteArrayOutputStream().withStream { stream ->
			new SBMLWriter().write(doc, stream)
			stream
		}
		def bais = new ByteArrayInputStream(baos.toByteArray()).withStream { input ->
			def node = new XmlParser().parse(input)
			PMFUtil.setPMFAnnotation(this, 'sbml', node.model)
		}
	}
	
	Model getSbml() {
		sbmlReader?.document?.model
	}
	
	SBMLAdapter getSbmlReader() {		
		def sbmlMetaData = PMFUtil.getPMFAnnotation(this, 'sbml')
		if(!sbmlMetaData)
			return null
		def sbmlMockup = String.format(sbmlTemplate, sbmlMetaData.toXMLString())
		def sbmlReader = new SBMLAdapter(validating: true)
		sbmlReader.parseText(sbmlMockup)
		sbmlReader
	}

	@Override
	List<ConformityMessage> getInvalidSettings() {
		sbmlReader?.getParseMessages(Level.ERROR) + super.getInvalidSettings()
	}
	
	void replace(ResultComponent rc) {
		NuMLDocument newParent = rc.parent
		def index = newParent.resultComponents.findIndexOf { it.is(rc) }
		newParent.resultComponents.set(index, this)
	}
}

@InheritConstructors
class PMFAtomicDescription extends AtomicDescription {
	PMFAtomicDescription(AtomicDescription atomicDescription) {
		// rather inefficient but versatile
		setParent(atomicDescription.parent)
		setOriginalNode(atomicDescription.originalNode)
	}
	
	/**
	 * Sets the unit to the specified value.
	 *
	 * @param unit the unit to set
	 */
	void setUnit(Unit unit) {
		if (unit == null)
			throw new NullPointerException("unit must not be null");

		this.unit = unit;
	}
	
	/**
	 * Returns the unit.
	 * 
	 * @return the unit
	 */
	Unit getUnit() {	
		def unitAnnotation = PMFUtil.getPMFAnnotation(this, 'unit')
		if(!unitAnnotation)
			return null
		
		PMFResultComponent rc = resultComponent
		if(!rc)
			return null
			
		rc.sbml.listOfUnitDefinitions.find { it.id == unitAnnotation.ref }
	}
	
	void replace(AtomicDescription description) {
		def newParent = description.parent
		switch(newParent.class) {
		case ResultComponent:
			newParent.dimensionDescription = this
			break
		case CompositeDescription:		
			newParent.description = this
			break
		case TupleDescription:		
			def index = newParent.descriptions.findIndexOf { it.is(description) }
			newParent.descriptions.set(index, this)
			break
		}
	}
}

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
package de.bund.bfr.pmf.numl;

import groovy.xml.QName

import org.apache.log4j.Level

import de.bund.bfr.numl.ConformityMessage
import de.bund.bfr.numl.NuMLDocument
import de.bund.bfr.numl.ResultComponent
import de.bund.bfr.pmf.PMFUtil

/**
 * Extends the NuML {@link ResultComponent} with convenience methods for PMF annotations and provides shortcuts to access the SBML annotations.
 */
class PMFResultComponent extends ResultComponent implements AnnotatedNuML {
	
	PMFResultComponent(ResultComponent rc) {
		// rather inefficient but versatile
		setParent(rc.parent)
		setOriginalNode(rc.originalNode)
	}
	
	PMFResultComponent() {		
	}
	
	{
		elementName = this.class.superclass.simpleName[0].toLowerCase() + this.class.superclass.simpleName[1..-1] 
	}
	
	List<ConformityMessage> getInvalidSettings(String prefix) {
		def messages = super.getInvalidSettings(prefix)
		
		def pmfMetaData = PMFUtil.getPMFAnnotation(this)
		if(!pmfMetaData)
			return [new ConformityMessage(level: Level.WARN,
				message: "$prefix: ${elementName} ${id} should be annotated (Specification 11/13)")] + messages
		def annotations = getQualifiedAnnotations()*.key
		def recommended = PMFUtil.DataSetAnnotations.collect { ns, tags -> tags.collect { new javax.xml.namespace.QName(ns, it) } }.flatten()
		
		def missing = recommended - annotations
		def superfluous = annotations - recommended
		missing.collect { annotationName ->
			new ConformityMessage(level: Level.INFO,
				message: "$prefix: Recommend annotation $annotationName of $elementName ${id} not present (Specification 11/13)")
		} + superfluous.collect { annotationName ->
			new ConformityMessage(level: Level.INFO,
				message: "$prefix: Unknown annotation $annotationName found in $elementName ${id}, might be an indicator for misspellings (Specification 11/13)")
		} + messages
	}
	
	void replace(ResultComponent rc) {
		NuMLDocument newParent = rc.parent
		def index = newParent.resultComponents.findIndexOf { it.is(rc) }
		newParent.resultComponents.set(index, this)
	}	
	
}

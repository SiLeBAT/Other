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

import org.sbml.jsbml.ListOf
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.SBase

import de.bund.bfr.numl.ConformityMessage
import de.bund.bfr.pmf.PMFDocument

import org.sbml.jsbml.util.StringTools

trait SBMLReplacement {
	void initLevelAndVersion() {
		if(getLevel() == -1)
			setLevel(3)
		if(getVersion() == -1)
			setVersion(1)
	}

	void replace(SBase sbmlElement) {
		this.setParent(null)
		SBase newParent = sbmlElement.parent
		if(newParent instanceof ListOf) {
			// bug SBML does not automatically unregister old elements
			newParent.unregisterChild(sbmlElement)
			newParent.set(newParent.indexOf(sbmlElement), this)
		}
		else
			newParent."$sbmlElement.elementName" = this
	}

	String getElementName() {
		StringTools.firstLetterLowerCase(getClass().superclass.simpleName)
	}

	List<ConformityMessage> getInvalidSettings(SBMLDocument document, String prefix, PMFDocument pmf) { 
		[]		
	}
}


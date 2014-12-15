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
package de.bund.bfr.pmf.sbml

import groovy.transform.InheritConstructors;

import java.util.List;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;

import de.bund.bfr.numl.ConformityMessage;
import de.bund.bfr.pmf.PMFDocument;


/**
 * Wrapper for PMF species (Specification 7 � SBML component species).
 */
@InheritConstructors
class PMFSpecies extends Species implements SourceAnnotation, SBMLReplacement {
	{
		initLevelAndVersion()
		this.hasOnlySubstanceUnits = false
	}
	
	List<ConformityMessage> getInvalidSettings(SBMLDocument document, String prefix, PMFDocument pmf) {
		def messages = SourceAnnotation.super.getInvalidSettings(document, prefix, pmf)
		
		if(!this.substanceUnits)
			messages << new ConformityMessage("$prefix: species $id must have substanceUnits")
		
		messages
	}
}
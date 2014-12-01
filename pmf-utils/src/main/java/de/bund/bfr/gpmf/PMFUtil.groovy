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

import org.sbml.jsbml.AbstractSBase;
import org.sbml.jsbml.xml.XMLNode

import de.bund.bfr.gnuml.ConformityMessage;

/**
 * 
 */
class PMFUtil {
	static PMF_NS = 'http://sourceforge.net/projects/microbialmodelingexchange/files/PMF-ML'
	
	static XMLNode getPMFAnnotation(AbstractSBase node, String annotationName) {
		if(!node.annotation)
			return null
			
		node.annotation.nonRDFannotation.find { 
			it.triple.name == annotationName && it.triple.namespaceURI == PMF_NS
		}
	}
}

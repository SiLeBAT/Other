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

import groovy.transform.InheritConstructors

import org.sbml.jsbml.Compartment
import org.sbml.jsbml.Unit.Kind

/**
 * Wrapper for PMF compartments (Specification 6 � SBML component compartments).
 */
@InheritConstructors
class PMFCompartment extends Compartment implements SourceAnnotation, SBMLReplacement {
	{
		initLevelAndVersion()
		this.spatialDimensions = 3
		setUnits(Kind.DIMENSIONLESS)
	}
}
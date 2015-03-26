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
package de.bund.bfr.numl

import groovy.util.Node

/**
 * 
 */
class NodeUtil {
	/**
	 * Compares the two nodes and their children for equal name and attributes.
	 */
	static boolean isEqual(Node n1, Node n2) {
		if(n1 == null)
			return n2 == null
		if(n2 == null)
			return false
		
		def nodes1 = n1.depthFirst()
		def nodes2 = n2.depthFirst()
		
		def size = nodes1.size()
		if(size != nodes2.size() || n1.localText() != n2.localText())
			return false
						
		def mismatch = (0..<size).find { index ->
			nodes1[index].name() != nodes2[index].name() || nodes1[index].attributes() != nodes2[index].attributes()
		}
		!mismatch
	}
}

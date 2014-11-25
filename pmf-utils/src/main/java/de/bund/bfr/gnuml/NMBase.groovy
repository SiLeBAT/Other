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
package de.bund.bfr.gnuml

import java.lang.reflect.Modifier;

/**
 * 
 */
class NMBase {
	String metaId
	
	List<Node> annotations = []
	
	List<Node> notes = []
	
	Node originalNode
	
	NUMLDocument document
	
	/**
	 * Sets the originalNode to the specified value.
	 *
	 * @param originalNode the originalNode to set
	 */
	public void setOriginalNode(Node originalNode) {
		if (originalNode == null)
			throw new NullPointerException("originalNode must not be null");

		this.originalNode = originalNode;
		this.metaId = originalNode.'@metaid'
		this.notes = originalNode.notes*.first()
		this.annotations = originalNode.annotations*.first()
	}
	
	public List<String> getInvalidSettings() {
		def errors = [:]
		Class clazz = this.class
		while (clazz != NMBase.class) { 
			clazz.properties.each { property ->
				property.accessible = true
				if(property.annotations.find { it instanceof Required } && property.get(this) == null)
					errors << "Value $property.name not set in $this"
			}
			clazz = clazz.superclass
		} 
	}
	
	protected Map<String, Object> collectPropertyValues() {
		def values = new LinkedHashMap()
		['metaId', 'notes', 'annotations'].each { attr ->
			values[attr] = this."$attr"
		}
		values
	}
	
	public String toString() {		
		def properties = collectPropertyValues()
		def reversedNonNull = properties.collect { it }.grep { it.value }.reverse()
		"${this.class.simpleName} ${reversedNonNull}"
	}
}

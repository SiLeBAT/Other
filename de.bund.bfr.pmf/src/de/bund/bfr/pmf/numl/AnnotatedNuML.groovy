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
package de.bund.bfr.pmf.numl

import groovy.xml.QName
import de.bund.bfr.pmf.PMFUtil

/**
 * 
 */
trait AnnotatedNuML {	
	Map<javax.xml.namespace.QName, String> getQualifiedAnnotations() {
		PMFUtil.getPMFAnnotation(this)?.children().collectEntries { annotation ->
			[(PMFUtil.toJavaQName(annotation.name())): annotation.value as String]
		}.findAll { it.value }
	}
	
	Map<String, String> getAnnotations() {
		qualifiedAnnotations?.collectEntries { [(it.key.localPart): it.value] }
	}
	
	String getAnnotation(String localPart, String uri = null) {
		getAnnotationNode(localPart, uri)?.value
	}
	
	Node getAnnotationNode(String localPart, String uri = null) {
		PMFUtil.getPMFAnnotation(this)?.getAt(new groovy.xml.QName(uri, localPart))?.getAt(0)
	}
	
	void setAnnotation(String localPart, String uri, String value) {
		def oldAnnotation = getAnnotation(localPart, uri)
		if(oldAnnotation)
			PMFUtil.getPMFAnnotation(this)?.remove(oldAnnotation)
		if(value)
			new Node(PMFUtil.ensurePMFAnnotation(this), new groovy.xml.QName(uri, localPart), [:], new NodeList([value]))
	}
	
	void setAnnotationNode(String localPart, String uri, Node value) {
		def oldAnnotation = getAnnotationNode(localPart, uri)
		if(oldAnnotation)
			PMFUtil.getPMFAnnotation(this)?.remove(oldAnnotation)
		if(value)
			PMFUtil.ensurePMFAnnotation(this).append(value)
	}
}

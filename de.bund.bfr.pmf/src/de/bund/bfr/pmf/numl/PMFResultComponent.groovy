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

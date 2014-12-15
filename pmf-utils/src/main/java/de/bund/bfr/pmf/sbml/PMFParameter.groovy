package de.bund.bfr.pmf.sbml;

import groovy.transform.EqualsAndHashCode
import groovy.transform.InheritConstructors
import groovy.transform.ToString

import org.apache.log4j.Level
import org.sbml.jsbml.Constraint
import org.sbml.jsbml.Parameter
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.Unit
import org.sbml.jsbml.xml.XMLAttributes
import org.sbml.jsbml.xml.XMLNode
import org.sbml.jsbml.xml.XMLTriple
import org.xml.sax.InputSource

import de.bund.bfr.numl.ConformityMessage
import de.bund.bfr.numl.Description
import de.bund.bfr.pmf.PMFDocument
import de.bund.bfr.pmf.PMFUtil

@InheritConstructors
class PMFParameter extends Parameter implements SBMLReplacement {	
	{
		initLevelAndVersion()
	}
	
	List<SourceValue> getSourceValues() {
		def sourceValues = PMFUtil.getPMFAnnotation(this, 'sourceValues')
		sourceValues?.getChildElements('sourceValue', PMFUtil.PMF_NS).collect { source ->
			new SourceValue(source.getAttrValue('source'), source.getAttrValue('descriptor'))
		} ?: []
	}
	
	List<Object> getValueInstances(PMFDocument pmfDoc) {
		this.sourceValues.inject([]) { result, sourceValue ->
			def xlink = (this.SBMLDocument.model as PMFModel).getDataSource(sourceValue.sourceId)
			if(!xlink)
				throw new IllegalArgumentException("The provided SBML document did not declare data source ${sourceValue.sourceId}")
			def dataSet = pmfDoc.resolve(xlink, this.SBMLDocument)
			def input = pmfDoc.getInputStream(dataSet)
			result + sourceValue.XPathExpression.evaluate(new InputSource(input))
		}
	}
		
	void setSourceValues(List<SourceValue> sourceValues) {
		XMLNode sourcesNode = PMFUtil.ensurePMFAnnotation(this, 'sourceValues')
		sourcesNode.removeChildren()
		sourceValues.each { sourceValue ->
			def valueNode = new XMLNode(new XMLTriple('sourceValue', PMFUtil.PMF_NS, null), new XMLAttributes())
			valueNode.addAttr('source', sourceValue.sourceId)
			valueNode.addAttr('descriptor', sourceValue.xpath, PMFUtil.XLINK_NS)
			sourcesNode.addChild(valueNode)
		}
	}
	
	void setSourceValue(SourceValue sourceValue) {
		setSourceValues([sourceValue])
	}
	
	void setSourceValue(String sourceId, String xpath) {
		setSourceValues([new SourceValue(sourceId, xpath)])
	}
	
	void setSourceValue(String sourceId, Description description) {
		setSourceValues([new SourceValue(sourceId, description.XPath)])
	}
	
	DoubleRange getRange() {
		def constraint = this.constraint
		// TODO:
	}
	
	Constraint getConstaint() {		
		PMFModel model = this.root.model
		model.listOfConstraints.find {
			// TODO:
		}
	}
	
//	final static rangeTemplate = """
//		<constraint>
//			<math xmlns="http://www.w3.org/1998/Math/MathML">
//				<apply>
//					<and/>
//					<apply>
//						<lt/>
//						<cn sbml:units="mole"> 1 </cn>
//						<ci> S1 </ci>
//					</apply>
//					<apply>
//						<lt/>
//						<ci> S1 </ci>
//						<cn sbml:units="mole"> 100 </cn>
//					</apply>
//				</apply>
//			</math>
//			<message xmlns="http://www.w3.org/1999/xhtml">
//			</message>
//		</constraint>"""
	
	void setRange(DoubleRange range) {
		PMFModel model = this.root.getChildElement('model')
		def oldConstraint = model.listOfConstraints.find { 
			// TODO:
		}
	}
	
	@Override
	List<ConformityMessage> getInvalidSettings(SBMLDocument document, String prefix, PMFDocument pmf) {
		def messages = []
		// TODO:		
		messages.addAll(['value', 'constant'].findAll { this."$it" == null }.collect {
			new ConformityMessage("$prefix: Parameter $id must contain $it (Specification 9)")
		})
		if(!this.unitsID)
			messages << new ConformityMessage(level: Level.WARN, 
				message: "$prefix: parameter $id does specify a unit, which is strongly encouraged (Specification 9, use dimensionless for unit-less parameters)")
		if(!this.constant) {
			try {
				if(!sourceValues) {
					if(pmf.dataSets)
						messages << new ConformityMessage(level: Level.WARN, 
							message: "$prefix: parameter $id does not contain a source value descriptor (Specification 13)")
				}
				else
					try {
						getValueInstances(pmf)
					} catch(e) {
						messages << new ConformityMessage("$prefix: $e.message")
					}
			} catch(e) {
				messages << new ConformityMessage("$prefix: invalid source value because of $e.message: $e.cause (Specification 13)")
			}
		}
		messages
	}
}

@EqualsAndHashCode
@ToString
class DoubleRange {
	final double from, to
	final Unit unit

	DoubleRange(double from, double to, Unit unit = null) {
		this.from = from;
		this.to = to;
		this.unit = unit;
	}
}
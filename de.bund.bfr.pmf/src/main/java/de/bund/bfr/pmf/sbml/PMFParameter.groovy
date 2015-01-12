package de.bund.bfr.pmf.sbml;

import groovy.transform.EqualsAndHashCode
import groovy.transform.InheritConstructors
import groovy.transform.ToString

import org.apache.log4j.Level
import org.sbml.jsbml.ASTNode
import org.sbml.jsbml.ASTNode.Type;
import org.sbml.jsbml.Constraint
import org.sbml.jsbml.Parameter
import org.sbml.jsbml.SBMLDocument
import org.sbml.jsbml.Unit
import org.sbml.jsbml.UnitDefinition;
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
		def constraint = this.rangeConstraint
		if(!constraint)
			return null
			
		double lower = Double.NEGATIVE_INFINITY, higher = Double.POSITIVE_INFINITY
		ASTNode math = constraint.math
		PMFUtil.traverse(math, { ASTNode node ->
			if(node.relational) {
				int varPos = (0..<(node.childCount)).find { node.getChildAt(it).variable }
				if(varPos == -1)
					return
					
				boolean isLess = node.type == Type.RELATIONAL_LEQ
				String unitRef = node.getChildAt(1 - varPos).units
				if(unitRef != this.units)
					throw new UnsupportedOperationException('Cannot transform units')
				double number = node.getChildAt(1 - varPos).getReal()
				if((varPos == 1) == isLess) 
					lower = number
				else higher = number
			}
			node
		})
		new DoubleRange(lower, higher)
	}
	
	Constraint getRangeConstraint() {		
		PMFModel model = this.getModel()
		model?.listOfConstraints?.find { constraint ->
			def matches = false
			PMFUtil.traverse(constraint.math, { ASTNode node ->
				if(node.type == Type.NAME && node.getVariable() == this) 
					matches = true
				node
			})
			matches
		}
	}
		
	void setRange(DoubleRange range) {
		PMFModel model = this.getModel()
		if(model == null)
			throw new IllegalStateException('Can only set the range after parameter has been added to model')
		
		def oldConstraint = this.rangeConstraint
		if(oldConstraint)
			model.listOfConstraints.remove(model.listOfConstraints.findIndex { it.is(oldConstraint) })
			
		def newConstraint = new Constraint()
		def id = new ASTNode(type: Type.NAME, name: this.id)
		def unit = this.units
		def lowerPart = range.from != Double.NEGATIVE_INFINITY ? newAST(Type.RELATIONAL_GEQ, id, range.from) : null
		def upperPart = range.to != Double.POSITIVE_INFINITY ? newAST(Type.RELATIONAL_LEQ, id, range.to) : null
		if(!lowerPart && !upperPart)
			return
			
		if(lowerPart && upperPart) {
			newConstraint.math = newAST(Type.LOGICAL_AND, lowerPart, upperPart)
		} else
			newConstraint.math = lowerPart ?: upperPart
			
		model.listOfConstraints.add(newConstraint)
	}
	
	private newAST(Type type, Object... parameters) {
		def node = new ASTNode(type)
		parameters.each { parameter ->
			def child = parameter instanceof Number ? 
				new ASTNode(value: parameter.doubleValue(), units: this.unitsID) : parameter
			node.addChild(child)
		}
		node
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
						messages << new ConformityMessage("$prefix: $e")
					}
			} catch(e) {
				messages << new ConformityMessage("$prefix: invalid source value because of $e.message: $e.cause (Specification 13)")
			}
			
			if(!this.rangeConstraint) {
				messages << new ConformityMessage(level: Level.WARN,
					message: "$prefix: parameter $id does not contain a range constraint (Specification 11)")
			} else {			
				def invalidRange = new DoubleRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
				if(invalidRange == this.range)
					messages << new ConformityMessage(
						message: "$prefix: could not extract a lower or upper bound in the constraint of parameter $id (Specification 11)")
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
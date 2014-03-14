package de.bund.bfr.knime.pmm.core.math;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.lsmp.djep.djep.DJep;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import de.bund.bfr.knime.pmm.core.common.Unit;

public class Converter {

	private static Converter instance = null;

	private DJep parser;
	private Map<String, Node> formulaMap;

	private Converter() {
		parser = MathUtilities.createParser();
		parser.addVariable("x", 0.0);
		formulaMap = new LinkedHashMap<String, Node>();
	}

	public static Converter getInstance() {
		if (instance == null) {
			instance = new Converter();
		}

		return instance;
	}

	public Double convert(Double value, Unit from, Unit to)
			throws ConvertException {
		if (from == null || to == null || value == null) {
			return null;
		}

		if (!EcoreUtil.equals(from.getQuantityType(), to.getQuantityType())) {
			throw new ConvertException();
		}

		if (from.getName().equals(to.getName())) {
			return value;
		}

		Node fromFormula = formulaMap.get(from.getConvertFrom());
		Node toFormula = formulaMap.get(to.getConvertTo());

		if (fromFormula == null) {
			try {
				fromFormula = parser.parse(from.getConvertFrom());
				formulaMap.put(from.getConvertFrom(), fromFormula);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		if (toFormula == null) {
			try {
				toFormula = parser.parse(to.getConvertTo());
				formulaMap.put(to.getConvertTo(), toFormula);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		try {
			double mem;

			parser.setVarValue("x", value);
			mem = (Double) parser.evaluate(fromFormula);
			parser.setVarValue("x", mem);

			return (Double) parser.evaluate(toFormula);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}

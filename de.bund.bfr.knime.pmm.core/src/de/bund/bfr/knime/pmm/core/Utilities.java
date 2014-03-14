/*******************************************************************************
 * PMM-Lab © 2012, Federal Institute for Risk Assessment (BfR), Germany
 * 
 * PMM-Lab is a set of KNIME-Nodes and KNIME workflows running within the KNIME software plattform (http://www.knime.org.).
 * 
 * PMM-Lab © 2012, Federal Institute for Risk Assessment (BfR), Germany
 * Contact: armin.weiser@bfr.bund.de or matthias.filter@bfr.bund.de 
 * 
 * Developers and contributors to the PMM-Lab project are 
 * Joergen Brandt (BfR)
 * Armin A. Weiser (BfR)
 * Matthias Filter (BfR)
 * Alexander Falenski (BfR)
 * Christian Thoens (BfR)
 * Annemarie Kaesbohrer (BfR)
 * Bernd Appel (BfR)
 * 
 * PMM-Lab is a project under development. Contributions are welcome.
 * 
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
 ******************************************************************************/
package de.bund.bfr.knime.pmm.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import de.bund.bfr.knime.pmm.core.common.QuantityType;
import de.bund.bfr.knime.pmm.core.common.Unit;
import de.bund.bfr.knime.pmm.core.data.Condition;
import de.bund.bfr.knime.pmm.core.data.ConditionParameter;
import de.bund.bfr.knime.pmm.core.data.Matrix;
import de.bund.bfr.knime.pmm.core.data.Organism;
import de.bund.bfr.knime.pmm.core.data.TimeSeries;
import de.bund.bfr.knime.pmm.core.models.Model;
import de.bund.bfr.knime.pmm.core.models.ModelFormula;
import de.bund.bfr.knime.pmm.core.models.Parameter;
import de.bund.bfr.knime.pmm.core.models.ParameterValue;
import de.bund.bfr.knime.pmm.core.models.PrimaryModel;

public class Utilities {

	private static Random random = null;

	public static final String DATA = "Data";
	public static final String MODEL = "Model";
	public static final String DEPVAR = "DepVar";
	public static final String PARAM = "Param";
	public static final String IDENTIFIER = "Identifier";

	public static final String TIME = "Time";
	public static final String CONCENTRATION = "Concentration";

	public static final String ORGANISM = "Organism";
	public static final String ORGANISM_DETAILS = "Organism Details";
	public static final String MATRIX = "Matrix";
	public static final String MATRIX_DETAILS = "Matrix Details";
	public static final String COMMENT = "Comment";

	public static final String SSE = "SSE";
	public static final String MSE = "MSE";
	public static final String RMSE = "RMSE";
	public static final String R2 = "R2";
	public static final String AIC = "AIC";
	public static final String SSE_LOCAL = "SSE (Local)";
	public static final String MSE_LOCAL = "MSE (Local)";
	public static final String RMSE_LOCAL = "RMSE (Local)";
	public static final String R2_LOCAL = "R2 (Local)";
	public static final String AIC_LOCAL = "AIC (Local)";

	private Utilities() {
	}

	public static String createId(String s) {
		return s.replaceAll("\\W", "");
	}

	public static void setId(EObject obj) {
		String className = obj.eClass().getName();

		if (obj instanceof Organism) {
			Organism organism = (Organism) obj;

			organism.setId(createId(className + ":" + organism.getName()));
		} else if (obj instanceof Matrix) {
			Matrix matrix = (Matrix) obj;

			matrix.setId(createId(className + ":" + matrix.getName()));
		} else if (obj instanceof QuantityType) {
			QuantityType quantity = (QuantityType) obj;

			quantity.setId(createId(className + ":" + quantity.getName()));
		} else if (obj instanceof Unit) {
			Unit unit = (Unit) obj;

			unit.setId(createId(className + ":" + unit.getName() + "("
					+ unit.getQuantityType().getName() + ")"));
		} else if (obj instanceof ConditionParameter) {
			ConditionParameter param = (ConditionParameter) obj;

			param.setId(createId(className + ":" + param.getName()));
		} else if (obj instanceof ModelFormula) {
			ModelFormula formula = (ModelFormula) obj;

			formula.setId(createId(className + ":" + formula.getName()));
		} else if (obj instanceof TimeSeries) {
			TimeSeries series = (TimeSeries) obj;

			series.setId(createId(className + ":" + series.getName()));
		}
	}

	public static String getRandomId() {
		if (random == null) {
			random = new Random();
		}

		return random.nextLong() + "";
	}

	public static String getName(String attr, String transform) {
		if (transform == null || transform.equals("")) {
			return attr;
		} else {
			return transform + "(" + attr + ")";
		}
	}

	public static String getNameWithUnit(String attr, Unit unit) {
		if (unit != null) {
			return attr + " [" + unit.getName() + "]";
		} else {
			return attr;
		}
	}

	public static String getNameWithUnit(String attr, Unit unit,
			String transform) {
		if (transform == null || transform.equals("")) {
			return getNameWithUnit(attr, unit);
		} else if (unit != null) {
			return getName(attr, transform) + " [" + transform + "("
					+ unit.getName() + ")]";
		} else {
			return getName(attr, transform);
		}
	}

	public static String getErrorName(String param) {
		return param + ": SE";
	}

	public static String getTName(String param) {
		return param + ": t";
	}

	public static String getPName(String param) {
		return param + ": Pr > |t|";
	}

	public static List<String> getNames(List<QuantityType> types) {
		List<String> names = new ArrayList<String>();

		for (QuantityType type : types) {
			names.add(type.getName());
		}

		return names;
	}

	public static List<String> getConditions(List<?> data) {
		Set<String> conditions = new LinkedHashSet<String>();

		for (Object obj : data) {
			TimeSeries series;

			if (obj instanceof TimeSeries) {
				series = (TimeSeries) obj;
			} else if (obj instanceof PrimaryModel) {
				series = ((PrimaryModel) obj).getData();
			} else {
				continue;
			}

			for (Condition condition : series.getConditions()) {
				conditions.add(condition.getParameter().getName());
			}
		}

		return new ArrayList<String>(conditions);
	}

	public static Map<String, Unit> getConditionUnits(List<?> data) {
		Map<String, Unit> units = new LinkedHashMap<String, Unit>();

		for (Object obj : data) {
			TimeSeries series;

			if (obj instanceof TimeSeries) {
				series = (TimeSeries) obj;
			} else if (obj instanceof PrimaryModel) {
				series = ((PrimaryModel) obj).getData();
			} else {
				continue;
			}

			for (Condition condition : series.getConditions()) {
				units.put(condition.getParameter().getName(),
						condition.getUnit());
			}
		}

		return units;
	}

	public static List<String> getParameters(List<? extends Model> models) {
		Set<String> params = new LinkedHashSet<String>();

		for (Model model : models) {
			params.addAll(model.getParamValues().keySet());
		}

		return new ArrayList<String>(params);
	}

	public static boolean isOutOfRange(List<Parameter> params,
			Map<String, ParameterValue> paramValues) {
		for (Parameter param : params) {
			Double value = paramValues.get(param.getName()).getValue();

			if (value != null) {
				Double min = param.getMin();
				Double max = param.getMax();

				if ((min != null && value < min)
						|| (max != null && value > max)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean covarianceMatrixMissing(List<Parameter> params,
			Map<String, ParameterValue> paramValues) {
		for (Parameter param : params) {
			if (paramValues.get(param.getName()).getCorrelations().isEmpty()) {
				return true;
			}
		}

		return false;
	}

	public static void removeNullValues(List<Double> targetValues,
			Map<String, List<Double>> argumentValues) {
		for (int i = 0; i < targetValues.size(); i++) {
			boolean remove = false;

			if (targetValues.get(i) == null) {
				remove = true;
				continue;
			}

			if (!remove) {
				for (List<Double> value : argumentValues.values()) {
					if (value.get(i) == null) {
						remove = true;
						break;
					}
				}
			}

			if (remove) {
				targetValues.remove(i);

				for (List<Double> value : argumentValues.values()) {
					value.remove(i);
				}

				i--;
			}
		}
	}

}

package de.bund.bfr.knime.pmm.views.fittedparameterview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bund.bfr.knime.pmm.core.CombineUtilities;
import de.bund.bfr.knime.pmm.core.Utilities;
import de.bund.bfr.knime.pmm.core.common.Unit;
import de.bund.bfr.knime.pmm.core.data.Condition;
import de.bund.bfr.knime.pmm.core.models.Parameter;
import de.bund.bfr.knime.pmm.core.models.PrimaryModel;
import de.bund.bfr.knime.pmm.core.models.PrimaryModelFormula;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.views.chart.Plotable;

public class FittedParameterViewReader {

	private List<String> ids;
	private List<Integer> colorCounts;
	private Map<String, List<String>> stringColumns;
	private Map<String, List<Double>> conditions;
	private Map<String, List<Double>> conditionMinValues;
	private Map<String, List<Double>> conditionMaxValues;
	private Map<String, List<String>> conditionUnits;
	private Set<String> standardVisibleColumns;

	private Map<String, Plotable> plotables;
	private Map<String, String> shortLegend;
	private Map<String, String> longLegend;

	public FittedParameterViewReader(PmmPortObject input) {
		List<PrimaryModel> models = input
				.getData(new ArrayList<PrimaryModel>());
		Set<String> idSet = new LinkedHashSet<String>();
		List<String> allConditions = Utilities.getConditions(models);
		Map<String, List<PrimaryModel>> modelsById = new LinkedHashMap<String, List<PrimaryModel>>();

		for (PrimaryModel model : models) {
			CombineUtilities.applyAssignmentsAndConversion(model);

			String id = model.getModelFormula().getId();

			if (!modelsById.containsKey(id)) {
				modelsById.put(id, new ArrayList<PrimaryModel>());
			}

			modelsById.get(id).add(model);
		}

		ids = new ArrayList<String>();
		colorCounts = new ArrayList<Integer>();
		plotables = new LinkedHashMap<String, Plotable>();
		shortLegend = new LinkedHashMap<String, String>();
		longLegend = new LinkedHashMap<String, String>();
		stringColumns = new LinkedHashMap<String, List<String>>();
		stringColumns.put(Utilities.PARAM, new ArrayList<String>());
		stringColumns.put(Utilities.MODEL, new ArrayList<String>());
		standardVisibleColumns = new LinkedHashSet<String>(Arrays.asList(
				Utilities.PARAM, Utilities.MODEL));
		conditions = new LinkedHashMap<String, List<Double>>();
		conditionMinValues = new LinkedHashMap<String, List<Double>>();
		conditionMaxValues = new LinkedHashMap<String, List<Double>>();
		conditionUnits = new LinkedHashMap<String, List<String>>();

		for (String cond : allConditions) {
			conditions.put(cond, null);
			conditionMinValues.put(cond, new ArrayList<Double>());
			conditionMaxValues.put(cond, new ArrayList<Double>());
			conditionUnits.put(cond, new ArrayList<String>());
		}

		for (String formulaId : modelsById.keySet()) {
			PrimaryModelFormula modelFormula = modelsById.get(formulaId).get(0)
					.getModelFormula();
			Map<String, Unit> units = Utilities.getConditionUnits(modelsById
					.get(formulaId));
			Map<String, List<Double>> arguments = new LinkedHashMap<String, List<Double>>();
			Map<String, List<Double>> condValueLists = new LinkedHashMap<String, List<Double>>();
			Map<String, List<Double>> paramValueLists = new LinkedHashMap<String, List<Double>>();

			for (String cond : units.keySet()) {
				arguments.put(cond, new ArrayList<Double>(Arrays.asList(0.0)));
				condValueLists.put(cond, new ArrayList<Double>());
			}

			for (Parameter param : modelFormula.getParams()) {
				paramValueLists.put(param.getName(), new ArrayList<Double>());
			}

			for (PrimaryModel model : modelsById.get(formulaId)) {
				for (Condition condition : model.getData().getConditions()) {
					condValueLists.get(condition.getParameter().getName()).add(
							condition.getValue());
				}
			}

			for (PrimaryModel model : modelsById.get(formulaId)) {
				for (Parameter param : modelFormula.getParams()) {
					String paramName = param.getName();

					paramValueLists.get(paramName).add(
							model.getParamValues().get(paramName).getValue());
				}
			}

			for (String cond : allConditions) {
				Double min = Double.POSITIVE_INFINITY;
				Double max = Double.NEGATIVE_INFINITY;
				String unit = null;

				for (PrimaryModel model : modelsById.get(formulaId)) {
					for (Condition condition : model.getData().getConditions()) {
						if (condition.getParameter().getName().equals(cond)) {
							min = Math.min(min, condition.getValue());
							max = Math.max(max, condition.getValue());
							unit = condition.getUnit().getName();
						}
					}
				}

				if (min.isInfinite()) {
					min = null;
				}

				if (max.isInfinite()) {
					max = null;
				}

				conditionMinValues.get(cond).add(min);
				conditionMaxValues.get(cond).add(max);
				conditionUnits.get(cond).add(unit);
			}

			for (Parameter param : modelFormula.getParams()) {
				String id = param.getName() + formulaId;

				if (!idSet.add(id)) {
					continue;
				}

				ids.add(id);
				stringColumns.get(Utilities.PARAM).add(param.getName());
				stringColumns.get(Utilities.MODEL).add(modelFormula.getName());
				shortLegend.put(id, param.getName());
				longLegend.put(id,
						param.getName() + " (" + modelFormula.getName() + ")");

				Plotable plotable = new Plotable(Plotable.DATASET_STRICT);

				plotable.setFunctionValue(param.getName());
				plotable.setFunctionArguments(arguments);
				plotable.addValueList(param.getName(),
						paramValueLists.get(param.getName()));
				plotable.setUnits(units);

				for (String cond : condValueLists.keySet()) {
					plotable.addValueList(cond, condValueLists.get(cond));
				}

				colorCounts.add(plotable.getNumberOfCombinations());
				plotables.put(id, plotable);
			}
		}
	}

	public List<String> getIds() {
		return ids;
	}

	public List<Integer> getColorCounts() {
		return colorCounts;
	}

	public Map<String, List<String>> getStringColumns() {
		return stringColumns;
	}

	public Map<String, List<Double>> getConditions() {
		return conditions;
	}

	public Map<String, List<Double>> getConditionMinValues() {
		return conditionMinValues;
	}

	public Map<String, List<Double>> getConditionMaxValues() {
		return conditionMaxValues;
	}

	public Map<String, List<String>> getConditionUnits() {
		return conditionUnits;
	}

	public Set<String> getStandardVisibleColumns() {
		return standardVisibleColumns;
	}

	public Map<String, Plotable> getPlotables() {
		return plotables;
	}

	public Map<String, String> getShortLegend() {
		return shortLegend;
	}

	public Map<String, String> getLongLegend() {
		return longLegend;
	}
}

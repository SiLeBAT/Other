package de.bund.bfr.knime.pmm.views.secondarymodelview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import de.bund.bfr.knime.pmm.core.models.ParameterValue;
import de.bund.bfr.knime.pmm.core.models.PrimaryModel;
import de.bund.bfr.knime.pmm.core.models.SecondaryModel;
import de.bund.bfr.knime.pmm.core.models.Variable;
import de.bund.bfr.knime.pmm.core.models.VariableRange;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.views.chart.ChartConstants;
import de.bund.bfr.knime.pmm.views.chart.Plotable;

public class SecondaryModelViewReader {

	private List<String> ids;
	private List<Integer> colorCounts;
	private List<String> formulas;
	private List<Map<String, ParameterValue>> parameterData;
	private Map<String, List<String>> stringColumns;
	private Map<String, List<Double>> doubleColumns;
	private Map<String, List<Double>> conditions;
	private Map<String, List<Double>> conditionMinValues;
	private Map<String, List<Double>> conditionMaxValues;
	private Map<String, List<String>> conditionUnits;
	private Set<String> standardVisibleColumns;
	private Set<String> filterableStringColumns;

	private Map<String, Plotable> plotables;
	private Map<String, String> shortLegend;
	private Map<String, String> longLegend;

	public SecondaryModelViewReader(PmmPortObject input) {
		List<SecondaryModel> dataModels = input
				.getData(new ArrayList<SecondaryModel>());
		Set<String> idSet = new LinkedHashSet<String>();
		Set<String> allConditions = new LinkedHashSet<String>();

		for (SecondaryModel dataModel : dataModels) {
			CombineUtilities.applyAssignmentsAndConversion(dataModel);
			allConditions.addAll(Utilities.getConditions(dataModel.getData()));
		}

		ids = new ArrayList<String>();
		colorCounts = new ArrayList<Integer>();
		plotables = new LinkedHashMap<String, Plotable>();
		shortLegend = new LinkedHashMap<String, String>();
		longLegend = new LinkedHashMap<String, String>();
		formulas = new ArrayList<String>();
		parameterData = new ArrayList<Map<String, ParameterValue>>();
		stringColumns = new LinkedHashMap<String, List<String>>();
		stringColumns.put(Utilities.DEPVAR, new ArrayList<String>());
		stringColumns.put(Utilities.MODEL, new ArrayList<String>());
		stringColumns.put(ChartConstants.STATUS, new ArrayList<String>());
		doubleColumns = new LinkedHashMap<String, List<Double>>();
		doubleColumns.put(Utilities.SSE, new ArrayList<Double>());
		doubleColumns.put(Utilities.MSE, new ArrayList<Double>());
		doubleColumns.put(Utilities.RMSE, new ArrayList<Double>());
		doubleColumns.put(Utilities.R2, new ArrayList<Double>());
		doubleColumns.put(Utilities.AIC, new ArrayList<Double>());
		filterableStringColumns = new LinkedHashSet<String>(
				Arrays.asList(ChartConstants.STATUS));
		standardVisibleColumns = new LinkedHashSet<String>(Arrays.asList(
				Utilities.DEPVAR, Utilities.MODEL, ChartConstants.STATUS));
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

		for (SecondaryModel model : dataModels) {
			Variable depVar = model.getModelFormula().getDepVar();
			PrimaryModel primModel = model.getData().get(0);
			String id = depVar.getName() + primModel.getModelFormula().getId();

			if (!idSet.add(id)) {
				continue;
			}

			ids.add(id);
			formulas.add(model.getModelFormula().getFormula());
			parameterData.add(model.getParamValues().map());
			stringColumns.get(Utilities.DEPVAR).add(
					depVar.getName() + " ("
							+ primModel.getModelFormula().getName() + ")");
			stringColumns.get(Utilities.MODEL).add(
					model.getModelFormula().getName());
			doubleColumns.get(Utilities.SSE).add(model.getSse());
			doubleColumns.get(Utilities.MSE).add(model.getMse());
			doubleColumns.get(Utilities.RMSE).add(model.getRmse());
			doubleColumns.get(Utilities.R2).add(model.getR2());
			doubleColumns.get(Utilities.AIC).add(model.getAic());
			shortLegend.put(id, depVar.getName());
			longLegend.put(id, depVar.getName() + " ("
					+ primModel.getModelFormula().getName() + ")");

			Plotable plotable = new Plotable(Plotable.BOTH_STRICT);
			Map<String, List<Double>> arguments = new LinkedHashMap<String, List<Double>>();
			Map<String, Double> minArg = new LinkedHashMap<String, Double>();
			Map<String, Double> maxArg = new LinkedHashMap<String, Double>();
			Map<String, Double> constants = new LinkedHashMap<String, Double>();
			Map<String, Map<String, Double>> covariances = new LinkedHashMap<String, Map<String, Double>>();

			for (Variable indep : model.getModelFormula().getIndepVars()) {
				VariableRange indepRange = model.getVariableRanges().get(
						indep.getName());

				arguments.put(indep.getName(),
						new ArrayList<Double>(Arrays.asList(0.0)));
				minArg.put(indep.getName(), indepRange.getMin());
				maxArg.put(indep.getName(), indepRange.getMax());
			}

			for (Parameter param : model.getModelFormula().getParams()) {
				ParameterValue paramValue = model.getParamValues().get(
						param.getName());
				Map<String, Double> cov = new LinkedHashMap<String, Double>();

				for (Parameter param2 : model.getModelFormula().getParams()) {
					cov.put(param2.getName(),
							paramValue.getCorrelations().get(param2.getName()));
				}

				constants.put(param.getName(), paramValue.getValue());
				covariances.put(param.getName(), cov);
			}

			List<Double> depVarData = new ArrayList<Double>();
			Map<String, List<Double>> condData = new LinkedHashMap<String, List<Double>>();
			List<String> conditions = Utilities.getConditions(model.getData());

			for (String cond : conditions) {
				condData.put(cond, new ArrayList<Double>());
			}

			for (PrimaryModel data : model.getData()) {
				for (Parameter param : data.getModelFormula().getParams()) {
					if (param.getName().equals(depVar.getName())) {
						ParameterValue paramValue = data.getParamValues().get(
								param.getName());

						depVarData.add(paramValue.getValue());
						break;
					}
				}

				for (String condName : conditions) {
					Double value = null;

					for (Condition cond : data.getData().getConditions()) {
						if (cond.getParameter().getName().equals(condName)) {
							value = cond.getValue();
							break;
						}
					}

					condData.get(condName).add(value);
				}
			}

			for (int i = 0; i < depVarData.size(); i++) {
				if (depVarData.get(i) == null) {
					depVarData.remove(i);

					for (List<Double> values : condData.values()) {
						values.remove(i);
					}
				}
			}

			Map<String, Unit> units = new LinkedHashMap<String, Unit>();

			units.putAll(Utilities.getConditionUnits(model.getData()));

			plotable.setFunction(model.getModelFormula().getFormula());
			plotable.setFunctionValue(depVar.getName());
			plotable.setFunctionArguments(arguments);
			plotable.setMinArguments(minArg);
			plotable.setMaxArguments(maxArg);
			plotable.setFunctionParameters(constants);
			plotable.setCovariances(covariances);
			plotable.setDegreesOfFreedom(model.getDegreesOfFreedom());
			plotable.addValueList(depVar.getName(), depVarData);
			plotable.setUnits(units);

			for (String cond : conditions) {
				plotable.addValueList(cond, condData.get(cond));
			}

			for (String cond : allConditions) {
				Double min = null;
				Double max = null;
				String unit = null;

				if (condData.containsKey(cond)) {
					List<Double> nonNullValues = new ArrayList<Double>(
							condData.get(cond));

					nonNullValues.removeAll(Arrays.asList((Double) null));

					if (!nonNullValues.isEmpty()) {
						min = Collections.min(nonNullValues);
						max = Collections.max(nonNullValues);

						if (units.containsKey(cond)) {
							unit = units.get(cond).getName();
						}
					}
				}

				conditionMinValues.get(cond).add(min);
				conditionMaxValues.get(cond).add(max);
				conditionUnits.get(cond).add(unit);
			}

			colorCounts.add(plotable.getNumberOfCombinations());

			if (!plotable.isPlotable()) {
				stringColumns.get(ChartConstants.STATUS).add(
						ChartConstants.FAILED);
			} else if (Utilities.isOutOfRange(model.getModelFormula()
					.getParams(), model.getParamValues().map())) {
				stringColumns.get(ChartConstants.STATUS).add(
						ChartConstants.OUT_OF_LIMITS);
			} else if (Utilities.covarianceMatrixMissing(model
					.getModelFormula().getParams(), model.getParamValues()
					.map())) {
				stringColumns.get(ChartConstants.STATUS).add(
						ChartConstants.NO_COVARIANCE);
			} else {
				stringColumns.get(ChartConstants.STATUS).add(ChartConstants.OK);
			}

			plotables.put(id, plotable);
		}
	}

	public List<String> getIds() {
		return ids;
	}

	public List<Integer> getColorCounts() {
		return colorCounts;
	}

	public List<String> getFormulas() {
		return formulas;
	}

	public List<Map<String, ParameterValue>> getParameterData() {
		return parameterData;
	}

	public Map<String, List<String>> getStringColumns() {
		return stringColumns;
	}

	public Map<String, List<Double>> getDoubleColumns() {
		return doubleColumns;
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

	public Set<String> getFilterableStringColumns() {
		return filterableStringColumns;
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

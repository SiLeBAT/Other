package de.bund.bfr.knime.pmm.views.predictorview;

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
import de.bund.bfr.knime.pmm.core.data.TimeSeries;
import de.bund.bfr.knime.pmm.core.models.Model;
import de.bund.bfr.knime.pmm.core.models.Parameter;
import de.bund.bfr.knime.pmm.core.models.ParameterValue;
import de.bund.bfr.knime.pmm.core.models.PrimaryModel;
import de.bund.bfr.knime.pmm.core.models.SecondaryModel;
import de.bund.bfr.knime.pmm.core.models.TertiaryModel;
import de.bund.bfr.knime.pmm.core.models.Variable;
import de.bund.bfr.knime.pmm.core.models.VariableRange;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.core.port.PmmPortObjectSpec;
import de.bund.bfr.knime.pmm.views.chart.ChartConstants;
import de.bund.bfr.knime.pmm.views.chart.Plotable;

public class PredictorViewReader {

	private List<String> ids;
	private List<String> formulas;
	private List<Map<String, ParameterValue>> parameterData;
	private Map<String, List<String>> stringColumns;
	private Map<String, List<Double>> doubleColumns;
	private Map<String, List<Double>> conditionValues;
	private Map<String, List<Double>> conditionMinValues;
	private Map<String, List<Double>> conditionMaxValues;
	private Map<String, List<String>> conditionUnits;
	private Set<String> filterableColumns;
	private Set<String> standardVisibleColumns;

	private Map<String, Plotable> plotables;
	private Map<String, String> shortLegend;
	private Map<String, String> longLegend;
	private Map<String, String> shortIds;

	public PredictorViewReader(PmmPortObject input) {
		List<? extends Model> models = null;
		boolean tertiary = true;

		if (input.getType().equals(PmmPortObjectSpec.PRIMARY_MODEL_TYPE)) {
			models = input.getData(new ArrayList<PrimaryModel>());
			tertiary = false;

			for (Model model : models) {
				CombineUtilities
						.applyAssignmentsAndConversion((PrimaryModel) model);
			}
		} else if (input.getType().equals(
				PmmPortObjectSpec.SECONDARY_MODEL_TYPE)) {
			models = CombineUtilities.combine(input
					.getData(new ArrayList<SecondaryModel>()));
		} else if (input.getType()
				.equals(PmmPortObjectSpec.TERTIARY_MODEL_TYPE)) {
			models = input.getData(new ArrayList<TertiaryModel>());
		}

		Set<String> allConditions = new LinkedHashSet<String>();
		Set<String> idSet = new LinkedHashSet<String>();

		if (!tertiary) {
			allConditions.addAll(Utilities.getConditions(models));
		} else {
			for (Model dataModel : models) {
				allConditions.addAll(Utilities
						.getConditions(((TertiaryModel) dataModel).getData()));
			}
		}

		ids = new ArrayList<String>();
		plotables = new LinkedHashMap<String, Plotable>();
		shortLegend = new LinkedHashMap<String, String>();
		longLegend = new LinkedHashMap<String, String>();
		shortIds = new LinkedHashMap<String, String>();
		formulas = new ArrayList<String>();
		parameterData = new ArrayList<Map<String, ParameterValue>>();
		doubleColumns = new LinkedHashMap<String, List<Double>>();
		doubleColumns.put(Utilities.SSE, new ArrayList<Double>());
		doubleColumns.put(Utilities.MSE, new ArrayList<Double>());
		doubleColumns.put(Utilities.RMSE, new ArrayList<Double>());
		doubleColumns.put(Utilities.R2, new ArrayList<Double>());
		doubleColumns.put(Utilities.AIC, new ArrayList<Double>());
		stringColumns = new LinkedHashMap<String, List<String>>();
		stringColumns.put(Utilities.IDENTIFIER, new ArrayList<String>());
		stringColumns.put(Utilities.MODEL, new ArrayList<String>());
		stringColumns.put(ChartConstants.STATUS, new ArrayList<String>());
		filterableColumns = new LinkedHashSet<String>(
				Arrays.asList(ChartConstants.STATUS));
		standardVisibleColumns = new LinkedHashSet<String>(Arrays.asList(
				Utilities.IDENTIFIER, Utilities.MODEL, ChartConstants.STATUS));
		conditionValues = null;
		conditionMinValues = null;
		conditionMaxValues = null;
		conditionUnits = new LinkedHashMap<String, List<String>>();

		if (!tertiary) {
			conditionValues = new LinkedHashMap<String, List<Double>>();

			for (String cond : allConditions) {
				conditionValues.put(cond, new ArrayList<Double>());
				conditionUnits.put(cond, new ArrayList<String>());
			}
		} else {
			conditionMinValues = new LinkedHashMap<String, List<Double>>();
			conditionMaxValues = new LinkedHashMap<String, List<Double>>();

			for (String cond : allConditions) {
				conditionMinValues.put(cond, new ArrayList<Double>());
				conditionMaxValues.put(cond, new ArrayList<Double>());
				conditionUnits.put(cond, new ArrayList<String>());
			}
		}

		int index = 1;

		for (Model model : models) {
			String id = model.getId();

			if (!idSet.add(id)) {
				continue;
			}

			ids.add(id);

			String depVar = model.getModelFormula().getDepVar().getName();
			Plotable plotable = null;
			Map<String, Double> parameters = new LinkedHashMap<String, Double>();
			Map<String, List<Double>> variables = new LinkedHashMap<String, List<Double>>();
			Map<String, Double> varMin = new LinkedHashMap<String, Double>();
			Map<String, Double> varMax = new LinkedHashMap<String, Double>();
			Map<String, Map<String, Double>> covariances = new LinkedHashMap<String, Map<String, Double>>();
			Map<String, Unit> units = new LinkedHashMap<String, Unit>();
			List<Variable> indeps = null;

			units.put(model.getModelFormula().getDepVar().getName(), model
					.getModelFormula().getDepVar().getUnit());

			if (model instanceof PrimaryModel) {
				indeps = Arrays.asList(((PrimaryModel) model).getModelFormula()
						.getIndepVar());
			} else if (model instanceof TertiaryModel) {
				indeps = ((TertiaryModel) model).getModelFormula()
						.getIndepVars();
			}

			for (Variable indep : indeps) {
				VariableRange indepRange = model.getVariableRanges().get(
						indep.getName());

				variables.put(indep.getName(),
						new ArrayList<Double>(Arrays.asList(0.0)));
				varMin.put(indep.getName(), indepRange.getMin());
				varMax.put(indep.getName(), indepRange.getMax());
				units.put(indep.getName(), indep.getUnit());
			}

			for (Parameter param : model.getModelFormula().getParams()) {
				String name = param.getName();
				ParameterValue paramValue = model.getParamValues().get(name);
				Map<String, Double> cov = new LinkedHashMap<String, Double>();

				for (Parameter param2 : model.getModelFormula().getParams()) {
					cov.put(param2.getName(),
							paramValue.getCorrelations().get(param2.getName()));
				}

				covariances.put(name, cov);
				parameters.put(name, paramValue.getValue());
			}

			formulas.add(model.getModelFormula().getFormula());
			parameterData.add(model.getParamValues().map());
			stringColumns.get(Utilities.IDENTIFIER).add(index + "");
			stringColumns.get(Utilities.MODEL).add(
					model.getModelFormula().getName());
			doubleColumns.get(Utilities.SSE).add(model.getSse());
			doubleColumns.get(Utilities.MSE).add(model.getMse());
			doubleColumns.get(Utilities.RMSE).add(model.getRmse());
			doubleColumns.get(Utilities.R2).add(model.getR2());
			doubleColumns.get(Utilities.AIC).add(model.getAic());

			shortLegend.put(id, index + "");
			longLegend.put(id, index + "");
			shortIds.put(id, index + "");
			index++;

			if (!tertiary) {
				for (String cond : allConditions) {
					Double value = null;
					String unit = null;

					for (Condition condition : ((PrimaryModel) model).getData()
							.getConditions()) {
						if (cond.equals(condition.getParameter().getName())) {
							value = condition.getValue();
							unit = condition.getUnit().getName();
							break;
						}
					}

					conditionValues.get(cond).add(value);
					conditionUnits.get(cond).add(unit);
				}
			} else {
				for (String cond : allConditions) {
					double minValue = Double.POSITIVE_INFINITY;
					double maxValue = Double.NEGATIVE_INFINITY;
					String unit = null;

					for (TimeSeries data : ((TertiaryModel) model).getData()) {
						for (Condition condition : data.getConditions()) {
							if (cond.equals(condition.getParameter().getName())) {
								if (condition.getValue() != null) {
									minValue = Math.min(minValue,
											condition.getValue());
									maxValue = Math.max(maxValue,
											condition.getValue());
								}

								unit = condition.getUnit().getName();
								break;
							}
						}
					}

					conditionMinValues.get(cond).add(minValue);
					conditionMaxValues.get(cond).add(maxValue);
					conditionUnits.get(cond).add(unit);
				}
			}

			plotable = new Plotable(Plotable.FUNCTION_SAMPLE);
			plotable.setFunction(model.getModelFormula().getFormula());
			plotable.setFunctionParameters(parameters);
			plotable.setFunctionArguments(variables);
			plotable.setMinArguments(varMin);
			plotable.setMaxArguments(varMax);
			plotable.setFunctionValue(depVar);
			plotable.setCovariances(covariances);
			plotable.setDegreesOfFreedom(model.getDegreesOfFreedom());
			plotable.setUnits(units);

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

	public Map<String, List<Double>> getConditionValues() {
		return conditionValues;
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

	public Set<String> getFilterableColumns() {
		return filterableColumns;
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

	public Map<String, String> getShortIds() {
		return shortIds;
	}
}

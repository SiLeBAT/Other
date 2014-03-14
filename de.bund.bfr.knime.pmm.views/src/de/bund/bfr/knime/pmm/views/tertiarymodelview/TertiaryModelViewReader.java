package de.bund.bfr.knime.pmm.views.tertiarymodelview;

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
import de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint;
import de.bund.bfr.knime.pmm.core.models.Parameter;
import de.bund.bfr.knime.pmm.core.models.ParameterValue;
import de.bund.bfr.knime.pmm.core.models.SecondaryModel;
import de.bund.bfr.knime.pmm.core.models.TertiaryModel;
import de.bund.bfr.knime.pmm.core.models.Variable;
import de.bund.bfr.knime.pmm.core.models.VariableRange;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.core.port.PmmPortObjectSpec;
import de.bund.bfr.knime.pmm.views.chart.ChartConstants;
import de.bund.bfr.knime.pmm.views.chart.Plotable;

public class TertiaryModelViewReader {

	private List<String> ids;
	private List<TimeSeries> data;
	private List<String> formulas;
	private List<Map<String, ParameterValue>> parameterData;
	private Map<String, List<String>> stringColumns;
	private Map<String, List<Double>> doubleColumns;
	private Map<String, List<Double>> conditionValues;
	private Map<String, List<String>> conditionUnits;
	private Set<String> standardVisibleColumns;
	private Set<String> filterableStringColumns;

	private Map<String, Plotable> plotables;
	private Map<String, String> shortLegend;
	private Map<String, String> longLegend;

	public TertiaryModelViewReader(PmmPortObject input) {
		List<TertiaryModel> dataModels = null;

		if (input.getType().equals(PmmPortObjectSpec.SECONDARY_MODEL_TYPE)) {
			dataModels = CombineUtilities.combine(input
					.getData(new ArrayList<SecondaryModel>()));
		} else if (input.getType()
				.equals(PmmPortObjectSpec.TERTIARY_MODEL_TYPE)) {
			dataModels = input.getData(new ArrayList<TertiaryModel>());
		}

		Set<String> allConditions = new LinkedHashSet<String>();
		Set<String> idSet = new LinkedHashSet<String>();

		for (TertiaryModel dataModel : dataModels) {
			allConditions.addAll(Utilities.getConditions(dataModel.getData()));
		}

		ids = new ArrayList<String>();
		plotables = new LinkedHashMap<String, Plotable>();
		shortLegend = new LinkedHashMap<String, String>();
		longLegend = new LinkedHashMap<String, String>();
		formulas = new ArrayList<String>();
		parameterData = new ArrayList<Map<String, ParameterValue>>();
		data = null;
		doubleColumns = new LinkedHashMap<String, List<Double>>();
		doubleColumns.put(Utilities.SSE, new ArrayList<Double>());
		doubleColumns.put(Utilities.MSE, new ArrayList<Double>());
		doubleColumns.put(Utilities.RMSE, new ArrayList<Double>());
		doubleColumns.put(Utilities.R2, new ArrayList<Double>());
		doubleColumns.put(Utilities.AIC, new ArrayList<Double>());
		conditionValues = null;
		conditionUnits = null;
		data = new ArrayList<TimeSeries>();
		stringColumns = new LinkedHashMap<String, List<String>>();
		stringColumns.put(Utilities.MODEL, new ArrayList<String>());
		stringColumns.put(ChartConstants.STATUS, new ArrayList<String>());
		stringColumns.put(Utilities.DATA, new ArrayList<String>());
		stringColumns.put(Utilities.ORGANISM, new ArrayList<String>());
		stringColumns.put(Utilities.ORGANISM_DETAILS, new ArrayList<String>());
		stringColumns.put(Utilities.MATRIX, new ArrayList<String>());
		stringColumns.put(Utilities.MATRIX_DETAILS, new ArrayList<String>());
		standardVisibleColumns = new LinkedHashSet<String>(Arrays.asList(
				Utilities.MODEL, ChartConstants.STATUS, Utilities.DATA,
				Utilities.ORGANISM, Utilities.MATRIX));
		filterableStringColumns = new LinkedHashSet<String>(Arrays.asList(
				Utilities.MODEL, ChartConstants.STATUS, Utilities.DATA));
		conditionValues = new LinkedHashMap<String, List<Double>>();
		conditionUnits = new LinkedHashMap<String, List<String>>();

		for (String cond : allConditions) {
			conditionValues.put(cond, new ArrayList<Double>());
			conditionUnits.put(cond, new ArrayList<String>());
		}

		for (TertiaryModel model : dataModels) {
			String modelId = model.getId() + "";

			for (TimeSeries series : model.getData()) {
				String id = modelId + series.getId();

				if (!idSet.add(id)) {
					continue;
				}

				ids.add(id);

				String depVar = model.getModelFormula().getDepVar().getName();
				Plotable plotable = null;
				Map<String, Double> parameters = new LinkedHashMap<String, Double>();
				Map<String, Double> constants = new LinkedHashMap<String, Double>();
				Map<String, List<Double>> variables = new LinkedHashMap<String, List<Double>>();
				Map<String, Double> varMin = new LinkedHashMap<String, Double>();
				Map<String, Double> varMax = new LinkedHashMap<String, Double>();
				Map<String, Map<String, Double>> covariances = new LinkedHashMap<String, Map<String, Double>>();
				Unit timeUnit = null;

				for (Variable indep : model.getModelFormula().getIndepVars()) {
					if (indep.getName().equals(Utilities.TIME)) {
						VariableRange indepRange = model.getVariableRanges()
								.get(indep.getName());

						variables.put(indep.getName(), new ArrayList<Double>(
								Arrays.asList(0.0)));
						varMin.put(indep.getName(), indepRange.getMin());
						varMax.put(indep.getName(), indepRange.getMax());
						timeUnit = indep.getUnit();
					} else {
						for (Condition condition : series.getConditions()) {
							if (indep.getName().equals(
									condition.getParameter().getName())) {
								constants.put(indep.getName(),
										condition.getValue());
							}
						}
					}
				}

				for (Parameter param : model.getModelFormula().getParams()) {
					String name = param.getName();
					ParameterValue paramValue = model.getParamValues()
							.get(name);
					Map<String, Double> cov = new LinkedHashMap<String, Double>();

					for (Parameter param2 : model.getModelFormula().getParams()) {
						cov.put(param2.getName(), paramValue.getCorrelations()
								.get(param2.getName()));
					}

					covariances.put(name, cov);
					parameters.put(name, paramValue.getValue());
				}

				formulas.add(model.getModelFormula().getFormula());
				parameterData.add(model.getParamValues().map());
				stringColumns.get(Utilities.MODEL).add(
						model.getModelFormula().getName());
				doubleColumns.get(Utilities.SSE).add(model.getSse());
				doubleColumns.get(Utilities.MSE).add(model.getMse());
				doubleColumns.get(Utilities.RMSE).add(model.getRmse());
				doubleColumns.get(Utilities.R2).add(model.getR2());
				doubleColumns.get(Utilities.AIC).add(model.getAic());

				List<Double> timeList = new ArrayList<Double>();
				List<Double> logcList = new ArrayList<Double>();
				String organism = null;
				String organismDetails = null;
				String matrix = null;
				String matrixDetails = null;

				for (TimeSeriesPoint p : series.getPoints()) {
					timeList.add(p.getTime());
					logcList.add(p.getConcentration());
				}

				if (series.getOrganism() != null) {
					organism = series.getOrganism().getName();
					organismDetails = series.getOrganism().getDescription();
				}

				if (series.getMatrix() != null) {
					matrix = series.getMatrix().getName();
					matrixDetails = series.getMatrix().getDescription();
				}

				plotable = new Plotable(Plotable.BOTH);
				plotable.addValueList(Utilities.TIME, timeList);
				plotable.addValueList(Utilities.CONCENTRATION, logcList);

				shortLegend.put(id, model.getModelFormula().getName() + " ("
						+ series.getName() + ")");
				longLegend.put(id, model.getModelFormula().getName() + " ("
						+ series.getName() + ")");
				stringColumns.get(Utilities.DATA).add(series.getName());
				stringColumns.get(Utilities.ORGANISM).add(organism);
				stringColumns.get(Utilities.ORGANISM_DETAILS).add(
						organismDetails);
				stringColumns.get(Utilities.MATRIX).add(matrix);
				stringColumns.get(Utilities.MATRIX_DETAILS).add(matrixDetails);
				data.add(series);

				for (String cond : allConditions) {
					Double value = null;
					String unit = null;

					for (Condition condition : series.getConditions()) {
						if (cond.equals(condition.getParameter().getName())) {
							value = condition.getValue();
							unit = condition.getUnit().getName();
							break;
						}
					}

					conditionValues.get(cond).add(value);
					conditionUnits.get(cond).add(unit);
				}

				Map<String, Unit> units = new LinkedHashMap<String, Unit>();

				units.put(Utilities.TIME, timeUnit);
				units.put(Utilities.CONCENTRATION, model.getModelFormula()
						.getDepVar().getUnit());

				plotable.setFunction(model.getModelFormula().getFormula());
				plotable.setFunctionParameters(parameters);
				plotable.setFunctionConstants(constants);
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
					stringColumns.get(ChartConstants.STATUS).add(
							ChartConstants.OK);
				}

				plotables.put(id, plotable);
			}
		}
	}

	public List<String> getIds() {
		return ids;
	}

	public List<TimeSeries> getData() {
		return data;
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

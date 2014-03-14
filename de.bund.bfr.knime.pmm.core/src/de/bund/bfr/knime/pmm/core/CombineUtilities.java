package de.bund.bfr.knime.pmm.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.util.EcoreUtil;

import de.bund.bfr.knime.pmm.core.common.Unit;
import de.bund.bfr.knime.pmm.core.data.Condition;
import de.bund.bfr.knime.pmm.core.data.TimeSeries;
import de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint;
import de.bund.bfr.knime.pmm.core.math.ConvertException;
import de.bund.bfr.knime.pmm.core.math.Converter;
import de.bund.bfr.knime.pmm.core.math.MathUtilities;
import de.bund.bfr.knime.pmm.core.models.Model;
import de.bund.bfr.knime.pmm.core.models.ModelsFactory;
import de.bund.bfr.knime.pmm.core.models.Parameter;
import de.bund.bfr.knime.pmm.core.models.ParameterValue;
import de.bund.bfr.knime.pmm.core.models.PrimaryModel;
import de.bund.bfr.knime.pmm.core.models.SecondaryModel;
import de.bund.bfr.knime.pmm.core.models.SecondaryModelFormula;
import de.bund.bfr.knime.pmm.core.models.TertiaryModel;
import de.bund.bfr.knime.pmm.core.models.TertiaryModelFormula;
import de.bund.bfr.knime.pmm.core.models.Variable;
import de.bund.bfr.knime.pmm.core.models.VariableRange;

public class CombineUtilities {

	public static void applyAssignmentsAndConversion(PrimaryModel model) {
		model.setModelFormula(EcoreUtil.copy(model.getModelFormula()));
		model.setData(EcoreUtil.copy(model.getData()));

		Variable depVar = model.getModelFormula().getDepVar();
		Variable indepVar = model.getModelFormula().getIndepVar();

		if (!indepVar.getName().equals(Utilities.TIME)) {
			model.getModelFormula().setFormula(
					MathUtilities.replaceVariable(model.getModelFormula()
							.getFormula(), indepVar.getName(), Utilities.TIME));
			model.getVariableRanges().put(Utilities.TIME,
					model.getVariableRanges().get(indepVar.getName()));
			model.getVariableRanges().remove(indepVar.getName());
		}

		depVar.setName(Utilities.CONCENTRATION);
		indepVar.setName(Utilities.TIME);

		TimeSeries data = model.getData();
		Converter converter = Converter.getInstance();

		for (TimeSeriesPoint p : data.getPoints()) {
			try {
				p.setTime(converter.convert(p.getTime(), data.getTimeUnit(),
						indepVar.getUnit()));
				p.setConcentration(converter.convert(p.getConcentration(),
						data.getConcentrationUnit(), depVar.getUnit()));
				data.setTimeUnit(indepVar.getUnit());
				data.setConcentrationUnit(depVar.getUnit());
			} catch (ConvertException e) {
				e.printStackTrace();
			}
		}
	}

	public static void applyAssignmentsAndConversion(SecondaryModel model) {
		List<PrimaryModel> primData = EmfUtilities.copy(model.getData());

		model.setModelFormula(EcoreUtil.copy(model.getModelFormula()));
		model.getData().clear();
		model.getData().addAll(primData);

		Map<String, Unit> modelUnits = new LinkedHashMap<String, Unit>();
		Converter converter = Converter.getInstance();
		Variable depVar = model.getModelFormula().getDepVar();

		depVar.setName(model.getAssignments().get(depVar.getName()));

		for (Variable indep : model.getModelFormula().getIndepVars()) {
			String newName = model.getAssignments().get(indep.getName());

			modelUnits.put(newName, indep.getUnit());
			model.getModelFormula().setFormula(
					MathUtilities.replaceVariable(model.getModelFormula()
							.getFormula(), indep.getName(), newName));
			model.getVariableRanges().put(newName,
					model.getVariableRanges().get(indep.getName()));
			model.getVariableRanges().remove(indep.getName());
			indep.setName(newName);
		}

		for (String condName : modelUnits.keySet()) {
			if (modelUnits.get(condName) == null) {
				Unit unit = null;

				for (Condition cond : model.getData().get(0).getData()
						.getConditions()) {
					if (cond.getParameter().getName().equals(condName)) {
						unit = cond.getUnit();
						break;
					}
				}

				for (Variable indep : model.getModelFormula().getIndepVars()) {
					if (indep.getName().equals(condName)) {
						indep.setUnit(unit);
					}
				}

				modelUnits.put(condName, unit);
			}
		}

		for (PrimaryModel data : model.getData()) {
			applyAssignmentsAndConversion(data);

			for (Condition cond : data.getData().getConditions()) {
				Unit toUnit = modelUnits.get(cond.getParameter().getName());

				if (toUnit != null) {
					try {
						cond.setValue(converter.convert(cond.getValue(),
								cond.getUnit(), toUnit));
						cond.setUnit(toUnit);
					} catch (ConvertException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static List<TertiaryModel> combine(List<SecondaryModel> dataModels) {
		// 1. call applyAssignmentsAndConversion for all dataModels
		List<SecondaryModel> dataModelsCopy = new ArrayList<SecondaryModel>();

		for (SecondaryModel dataModel : dataModels) {
			SecondaryModel copy = EcoreUtil.copy(dataModel);

			applyAssignmentsAndConversion(copy);
			dataModelsCopy.add(copy);
		}

		// 2. find secondary models and data for each primary model
		Map<String, PrimaryModel> primModels = new LinkedHashMap<String, PrimaryModel>();
		Map<String, List<SecondaryModel>> secModels = new LinkedHashMap<String, List<SecondaryModel>>();
		Map<String, Map<String, Double>> paramMeanValues = new LinkedHashMap<String, Map<String, Double>>();
		Map<String, List<TimeSeries>> data = new LinkedHashMap<String, List<TimeSeries>>();

		for (SecondaryModel dataModel : dataModelsCopy) {
			PrimaryModel primModel = dataModel.getData().get(0);
			String id = primModel.getModelFormula().getId();

			if (!primModels.containsKey(id)) {
				primModels.put(id, primModel);
				secModels.put(id, new ArrayList<SecondaryModel>());

				Map<String, Double> paramMeans = new LinkedHashMap<String, Double>();
				Map<String, Integer> paramCount = new LinkedHashMap<String, Integer>();
				List<TimeSeries> dataList = new ArrayList<TimeSeries>();

				for (Parameter param : primModel.getModelFormula().getParams()) {
					paramMeans.put(param.getName(), 0.0);
					paramCount.put(param.getName(), 0);
				}

				for (PrimaryModel primData : dataModel.getData()) {
					for (Parameter param : primData.getModelFormula()
							.getParams()) {
						String name = param.getName();
						ParameterValue value = primData.getParamValues().get(
								name);

						if (value != null && value.getValue() != null) {
							paramMeans.put(name,
									paramMeans.get(name) + value.getValue());
							paramCount.put(name, paramCount.get(name) + 1);
						}
					}
				}

				for (String name : paramMeans.keySet()) {
					if (paramCount.get(name) != 0) {
						paramMeans.put(name,
								paramMeans.get(name) / paramCount.get(name));
					} else {
						paramMeans.put(name, null);
					}
				}

				for (PrimaryModel primData : dataModel.getData()) {
					dataList.add(primData.getData());
				}

				paramMeanValues.put(id, paramMeans);
				data.put(id, dataList);
			}

			secModels.get(id).add(dataModel);
			paramMeanValues.get(id).remove(
					dataModel.getModelFormula().getDepVar().getName());
		}

		// 3. if secondary does not exist for a parameter create constant model
		// 4. call combine for each primary models with its secondary models
		// 5. convert all conditions to the tertiary model units
		List<TertiaryModel> tertiaryDataList = new ArrayList<TertiaryModel>();

		for (String id : primModels.keySet()) {
			PrimaryModel primModel = primModels.get(id);
			List<SecondaryModel> secModel = secModels.get(id);

			for (String param : paramMeanValues.get(id).keySet()) {
				Double value = paramMeanValues.get(id).get(param);

				if (value != null) {
					secModel.add(createConstantModel(param, value));
				}
			}

			TertiaryModel model = combine(primModel, secModel);
			Converter converter = Converter.getInstance();

			model.getData().addAll(data.get(id));

			for (TimeSeries series : model.getData()) {
				for (Condition cond : series.getConditions()) {
					for (Variable indep : model.getModelFormula()
							.getIndepVars()) {
						if (cond.getParameter().getName()
								.equals(indep.getName())) {
							try {
								cond.setValue(converter.convert(
										cond.getValue(), cond.getUnit(),
										indep.getUnit()));
							} catch (ConvertException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}

			tertiaryDataList.add(model);
		}

		return tertiaryDataList;
	}

	private static TertiaryModel combine(PrimaryModel primModel,
			List<SecondaryModel> secModels) {
		TertiaryModel tertModel = ModelsFactory.eINSTANCE.createTertiaryModel();
		TertiaryModelFormula formula = ModelsFactory.eINSTANCE
				.createTertiaryModelFormula();
		List<Model> allModels = new ArrayList<Model>();

		allModels.add(primModel);
		allModels.addAll(secModels);

		formula.setName(primModel.getModelFormula().getName() + " (Tertiary)");
		formula.setId(getCombinedFormulaId(allModels));
		formula.setFormula(primModel.getModelFormula().getFormula());
		formula.setDepVar(EcoreUtil.copy(primModel.getModelFormula()
				.getDepVar()));
		formula.getIndepVars().add(
				EcoreUtil.copy(primModel.getModelFormula().getIndepVar()));
		formula.getParams().addAll(
				EmfUtilities.copy(primModel.getModelFormula().getParams()));
		tertModel.setModelFormula(formula);
		tertModel.setId(getCombinedId(allModels));
		tertModel.getVariableRanges().putAll(
				EmfUtilities.copy(primModel.getVariableRanges().map()));
		tertModel.getParamValues().putAll(
				EmfUtilities.copy(primModel.getParamValues().map()));

		for (SecondaryModel secModel : secModels) {
			String paramName = secModel.getModelFormula().getDepVar().getName();
			String secFormula = secModel.getModelFormula().getFormula();

			removeParam(tertModel, paramName);

			for (Variable indep : secModel.getModelFormula().getIndepVars()) {
				String newName = addIndep(
						tertModel,
						EcoreUtil.copy(indep),
						EcoreUtil.copy(secModel.getVariableRanges().get(
								indep.getName())));

				MathUtilities.replaceVariable(secFormula, indep.getName(),
						newName);
			}

			for (Parameter param : secModel.getModelFormula().getParams()) {
				String newName = addParam(
						tertModel,
						EcoreUtil.copy(param),
						EcoreUtil.copy(secModel.getParamValues().get(
								param.getName())));

				MathUtilities.replaceVariable(secFormula, param.getName(),
						newName);
			}

			if (!secFormula.equals(paramName)) {
				formula.setFormula(MathUtilities.replaceVariable(
						formula.getFormula(), paramName, "(" + secFormula + ")"));
			}
		}

		return tertModel;
	}

	private static SecondaryModel createConstantModel(String paramName,
			double value) {
		SecondaryModel model = ModelsFactory.eINSTANCE.createSecondaryModel();
		SecondaryModelFormula formula = ModelsFactory.eINSTANCE
				.createSecondaryModelFormula();

		formula.setId(paramName);
		formula.setName("generated");
		model.setModelFormula(formula);
		model.setId(paramName + value);

		Variable depVar = ModelsFactory.eINSTANCE.createVariable();
		Parameter param = ModelsFactory.eINSTANCE.createParameter();
		ParameterValue paramValue = ModelsFactory.eINSTANCE
				.createParameterValue();

		depVar.setName(paramName);
		param.setName(paramName);
		paramValue.setValue(value);
		model.getParamValues().put(paramName, paramValue);

		formula.setDepVar(depVar);
		formula.getParams().add(param);
		formula.setFormula(param.getName());

		return model;
	}

	private static String addIndep(TertiaryModel model, Variable indep,
			VariableRange range) {
		for (Variable i : model.getModelFormula().getIndepVars()) {
			if (i.getName().equals(indep.getName())) {
				VariableRange iRange = model.getVariableRanges().get(
						i.getName());

				if (iRange != null) {
					if (iRange.getMin() == null) {
						iRange.setMin(range.getMin());
					} else if (range.getMin() != null) {
						iRange.setMin(Math.max(iRange.getMin(), range.getMin()));
					}

					if (iRange.getMax() == null) {
						iRange.setMax(range.getMax());
					} else if (range.getMax() != null) {
						iRange.setMax(Math.min(iRange.getMax(), range.getMax()));
					}
				} else {
					model.getVariableRanges().put(i.getName(), range);
				}

				String replacement = indep.getName();

				if (!EcoreUtil.equals(i.getUnit(), indep.getUnit())) {
					String from = i.getUnit().getConvertFrom();
					String to = indep.getUnit().getConvertTo();

					from = MathUtilities.replaceVariable(from, "x",
							indep.getName());
					to = MathUtilities.replaceVariable(to, "x", "(" + from
							+ ")");
					replacement = to;
				}

				return replacement;
			}
		}

		model.getModelFormula().getIndepVars().add(indep);
		model.getVariableRanges().put(indep.getName(), range);

		return indep.getName();
	}

	private static String addParam(TertiaryModel model, Parameter param,
			ParameterValue value) {
		Set<String> paramNames = new LinkedHashSet<String>();

		for (Parameter p : model.getModelFormula().getParams()) {
			paramNames.add(p.getName());
		}

		String paramName = param.getName();
		int i = 2;

		while (paramNames.contains(paramName)) {
			paramName = param.getName() + i;
			i++;
		}

		param.setName(paramName);
		model.getModelFormula().getParams().add(param);
		model.getParamValues().put(paramName, value);

		return paramName;
	}

	private static void removeParam(TertiaryModel model, String paramName) {
		Parameter removeParam = null;

		for (Parameter p : model.getModelFormula().getParams()) {
			if (p.getName().equals(paramName)) {
				removeParam = p;
				break;
			}
		}

		model.getModelFormula().getParams().remove(removeParam);
		model.getParamValues().remove(paramName);
	}

	private static String getCombinedFormulaId(
			Collection<? extends Model> models) {
		String id = "";

		for (Model model : models) {
			id += model.getModelFormula().getId();
		}

		return id;
	}

	private static String getCombinedId(Collection<? extends Model> models) {
		String id = "";

		for (Model model : models) {
			id += model.getId();
		}

		return id;
	}
}

package de.bund.bfr.knime.pmm.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.bund.bfr.knime.pmm.core.Utilities;
import de.bund.bfr.knime.pmm.core.common.CommonFactory;
import de.bund.bfr.knime.pmm.core.common.QuantityType;
import de.bund.bfr.knime.pmm.core.common.Unit;
import de.bund.bfr.knime.pmm.core.data.ConditionParameter;
import de.bund.bfr.knime.pmm.core.data.DataFactory;
import de.bund.bfr.knime.pmm.core.data.Matrix;
import de.bund.bfr.knime.pmm.core.data.Organism;
import de.bund.bfr.knime.pmm.core.models.ModelsFactory;
import de.bund.bfr.knime.pmm.core.models.Parameter;
import de.bund.bfr.knime.pmm.core.models.PrimaryModelFormula;
import de.bund.bfr.knime.pmm.core.models.SecondaryModelFormula;
import de.bund.bfr.knime.pmm.core.models.Variable;

public class DefaultDB {

	private static DefaultDB instance = null;

	private List<QuantityType> quantities;
	private QuantityType timeQuantity;
	private List<QuantityType> concentrationQuantities;
	private QuantityType temperatureQuantity;
	private QuantityType phQuantity;
	private QuantityType waterActivityQuantity;
	private QuantityType volumeConcentrationQuantity;
	private List<ConditionParameter> conditionParameters;
	private List<Organism> organisms;
	private List<Matrix> matrices;
	private List<PrimaryModelFormula> primaryModels;
	private List<SecondaryModelFormula> secondaryModels;

	private DefaultDB() {
		readKindOfQuantities();
		readConditionParameters();
		readOrganisms();
		readMatrices();
		readPrimaryModels();
		readSecondaryModels();
	}

	public static DefaultDB getInstance() {
		if (instance == null) {
			instance = new DefaultDB();
		}

		return instance;
	}

	public List<QuantityType> getQuantities() {
		return quantities;
	}

	public QuantityType getTimeQuantity() {
		return timeQuantity;
	}

	public List<QuantityType> getConcentrationQuantities() {
		return concentrationQuantities;
	}

	public QuantityType getTemperatureQuantity() {
		return temperatureQuantity;
	}

	public QuantityType getPhQuantity() {
		return phQuantity;
	}

	public QuantityType getWaterActivityQuantity() {
		return waterActivityQuantity;
	}

	public QuantityType getVolumeConcentrationQuantity() {
		return volumeConcentrationQuantity;
	}

	public List<ConditionParameter> getConditionParameters() {
		return conditionParameters;
	}

	public List<Organism> getOrganisms() {
		return organisms;
	}

	public List<Matrix> getMatrices() {
		return matrices;
	}

	public List<PrimaryModelFormula> getPrimaryModels() {
		return primaryModels;
	}

	public List<SecondaryModelFormula> getSecondaryModels() {
		return secondaryModels;
	}

	private void readKindOfQuantities() {
		quantities = new ArrayList<QuantityType>();
		concentrationQuantities = new ArrayList<QuantityType>();
		addTimeQuantity();
		addNumberContentQuantity();
		addNumberConcentrationQuantity();
		addTemperatureQuantity();
		addPhQuantity();
		addWaterActivityQuantity();
		addVolumeConcentrationQuantity();
	}

	private void addTimeQuantity() {
		timeQuantity = CommonFactory.eINSTANCE.createQuantityType();
		Unit second = CommonFactory.eINSTANCE.createUnit();
		Unit minute = CommonFactory.eINSTANCE.createUnit();
		Unit hour = CommonFactory.eINSTANCE.createUnit();
		Unit day = CommonFactory.eINSTANCE.createUnit();
		Unit week = CommonFactory.eINSTANCE.createUnit();

		second.setName("s");
		second.setQuantityType(timeQuantity);
		second.setConvertFrom("x");
		second.setConvertTo("x");
		minute.setName("min");
		minute.setQuantityType(timeQuantity);
		minute.setConvertFrom("x*60");
		minute.setConvertTo("x/60");
		hour.setName("h");
		hour.setQuantityType(timeQuantity);
		hour.setConvertFrom("x*3600");
		hour.setConvertTo("x/3600");
		day.setName("day");
		day.setQuantityType(timeQuantity);
		day.setConvertFrom("x*86400");
		day.setConvertTo("x/86400");
		week.setName("week");
		week.setQuantityType(timeQuantity);
		week.setConvertFrom("x*604800");
		week.setConvertTo("x/604800");
		timeQuantity.setName("Time");
		timeQuantity.getUnits().addAll(
				Arrays.asList(second, minute, hour, day, week));
		timeQuantity.setDefaultUnit(hour);

		Utilities.setId(second);
		Utilities.setId(minute);
		Utilities.setId(hour);
		Utilities.setId(day);
		Utilities.setId(week);
		Utilities.setId(timeQuantity);

		quantities.add(timeQuantity);
	}

	private void addNumberContentQuantity() {
		QuantityType numberContentQuantity = CommonFactory.eINSTANCE
				.createQuantityType();
		Unit countPerGram = CommonFactory.eINSTANCE.createUnit();
		Unit log10CountPerGram = CommonFactory.eINSTANCE.createUnit();
		Unit lnCountPerGram = CommonFactory.eINSTANCE.createUnit();

		countPerGram.setName("count/g");
		countPerGram.setConvertFrom("x");
		countPerGram.setConvertTo("x");
		countPerGram.setQuantityType(numberContentQuantity);
		log10CountPerGram.setName("log10(count/g)");
		log10CountPerGram.setConvertFrom("10^x");
		log10CountPerGram.setConvertTo("log10(x)");
		log10CountPerGram.setQuantityType(numberContentQuantity);
		lnCountPerGram.setName("ln(count/g)");
		lnCountPerGram.setConvertFrom("exp(x)");
		lnCountPerGram.setConvertTo("ln(x)");
		lnCountPerGram.setQuantityType(numberContentQuantity);
		numberContentQuantity.setName("Number Content");
		numberContentQuantity.getUnits().addAll(
				Arrays.asList(countPerGram, log10CountPerGram, lnCountPerGram));
		numberContentQuantity.setDefaultUnit(log10CountPerGram);

		Utilities.setId(countPerGram);
		Utilities.setId(log10CountPerGram);
		Utilities.setId(lnCountPerGram);
		Utilities.setId(numberContentQuantity);

		quantities.add(numberContentQuantity);
		concentrationQuantities.add(numberContentQuantity);
	}

	private void addNumberConcentrationQuantity() {
		QuantityType numberConcentrationQuantity = CommonFactory.eINSTANCE
				.createQuantityType();
		Unit countPerMl = CommonFactory.eINSTANCE.createUnit();
		Unit log10CountPerMl = CommonFactory.eINSTANCE.createUnit();
		Unit lnCountPerMl = CommonFactory.eINSTANCE.createUnit();

		countPerMl.setName("count/ml");
		countPerMl.setConvertFrom("x");
		countPerMl.setConvertTo("x");
		countPerMl.setQuantityType(numberConcentrationQuantity);
		log10CountPerMl.setName("log10(count/ml)");
		log10CountPerMl.setConvertFrom("10^x");
		log10CountPerMl.setConvertTo("log10(x)");
		log10CountPerMl.setQuantityType(numberConcentrationQuantity);
		lnCountPerMl.setName("ln(count/ml)");
		lnCountPerMl.setConvertFrom("exp(x)");
		lnCountPerMl.setConvertTo("ln(x)");
		lnCountPerMl.setQuantityType(numberConcentrationQuantity);
		numberConcentrationQuantity.setName("Number Concentration");
		numberConcentrationQuantity.getUnits().addAll(
				Arrays.asList(countPerMl, log10CountPerMl, lnCountPerMl));
		numberConcentrationQuantity.setDefaultUnit(log10CountPerMl);

		Utilities.setId(countPerMl);
		Utilities.setId(log10CountPerMl);
		Utilities.setId(lnCountPerMl);
		Utilities.setId(numberConcentrationQuantity);

		quantities.add(numberConcentrationQuantity);
		concentrationQuantities.add(numberConcentrationQuantity);
	}

	private void addTemperatureQuantity() {
		temperatureQuantity = CommonFactory.eINSTANCE.createQuantityType();
		Unit celsius = CommonFactory.eINSTANCE.createUnit();
		Unit fahrenheit = CommonFactory.eINSTANCE.createUnit();
		Unit kelvin = CommonFactory.eINSTANCE.createUnit();

		celsius.setName("�C");
		celsius.setConvertFrom("x");
		celsius.setConvertTo("x");
		celsius.setQuantityType(temperatureQuantity);
		fahrenheit.setName("�F");
		fahrenheit.setConvertFrom("(x-32)*5/9");
		fahrenheit.setConvertTo("x*9/5+32");
		fahrenheit.setQuantityType(temperatureQuantity);
		kelvin.setName("Kelvin");
		kelvin.setConvertFrom("x-273.15");
		kelvin.setConvertTo("x+273.15");
		kelvin.setQuantityType(temperatureQuantity);
		temperatureQuantity.setName("Temperature");
		temperatureQuantity.getUnits().addAll(
				Arrays.asList(celsius, fahrenheit, kelvin));
		temperatureQuantity.setDefaultUnit(celsius);

		Utilities.setId(celsius);
		Utilities.setId(fahrenheit);
		Utilities.setId(kelvin);
		Utilities.setId(temperatureQuantity);

		quantities.add(temperatureQuantity);
	}

	private void addPhQuantity() {
		phQuantity = CommonFactory.eINSTANCE.createQuantityType();
		Unit pH = CommonFactory.eINSTANCE.createUnit();

		pH.setName("pH");
		pH.setConvertFrom("x");
		pH.setConvertTo("x");
		pH.setQuantityType(phQuantity);
		phQuantity.setName("pH");
		phQuantity.getUnits().add(pH);
		phQuantity.setDefaultUnit(pH);

		Utilities.setId(pH);
		Utilities.setId(phQuantity);

		quantities.add(phQuantity);
	}

	private void addWaterActivityQuantity() {
		waterActivityQuantity = CommonFactory.eINSTANCE.createQuantityType();
		Unit aw = CommonFactory.eINSTANCE.createUnit();

		aw.setName("aw");
		aw.setConvertFrom("x");
		aw.setConvertTo("x");
		aw.setQuantityType(waterActivityQuantity);
		waterActivityQuantity.setName("Water Activity");
		waterActivityQuantity.getUnits().add(aw);
		waterActivityQuantity.setDefaultUnit(aw);

		Utilities.setId(aw);
		Utilities.setId(waterActivityQuantity);

		quantities.add(waterActivityQuantity);
	}

	private void addVolumeConcentrationQuantity() {
		volumeConcentrationQuantity = CommonFactory.eINSTANCE
				.createQuantityType();
		Unit percent = CommonFactory.eINSTANCE.createUnit();

		percent.setName("% vol/wt");
		percent.setConvertFrom("x");
		percent.setConvertTo("x");
		percent.setQuantityType(volumeConcentrationQuantity);
		volumeConcentrationQuantity.setName("Volume Concentration");
		volumeConcentrationQuantity.getUnits().add(percent);
		volumeConcentrationQuantity.setDefaultUnit(percent);

		Utilities.setId(percent);
		Utilities.setId(volumeConcentrationQuantity);

		quantities.add(volumeConcentrationQuantity);
	}

	private void readConditionParameters() {
		conditionParameters = new ArrayList<ConditionParameter>();

		ConditionParameter temperature = DataFactory.eINSTANCE
				.createConditionParameter();
		ConditionParameter pH = DataFactory.eINSTANCE
				.createConditionParameter();
		ConditionParameter waterActivity = DataFactory.eINSTANCE
				.createConditionParameter();
		ConditionParameter carvacrol = DataFactory.eINSTANCE
				.createConditionParameter();
		ConditionParameter cinnamaldehyde = DataFactory.eINSTANCE
				.createConditionParameter();

		temperature.setName("Temperature");
		temperature.getQuantityTypes().add(getTemperatureQuantity());
		pH.setName("pH");
		pH.getQuantityTypes().add(getPhQuantity());
		waterActivity.setName("Water Activity");
		waterActivity.getQuantityTypes().add(getWaterActivityQuantity());
		carvacrol.setName("Carvacrol");
		carvacrol.getQuantityTypes().add(getVolumeConcentrationQuantity());
		cinnamaldehyde.setName("Cinnamaldehyde");
		cinnamaldehyde.getQuantityTypes().add(getVolumeConcentrationQuantity());

		Utilities.setId(temperature);
		Utilities.setId(pH);
		Utilities.setId(waterActivity);
		Utilities.setId(carvacrol);
		Utilities.setId(cinnamaldehyde);

		conditionParameters.add(temperature);
		conditionParameters.add(pH);
		conditionParameters.add(waterActivity);
		conditionParameters.add(carvacrol);
		conditionParameters.add(cinnamaldehyde);
	}

	private void readOrganisms() {
		organisms = new ArrayList<Organism>();
		addOrganism("Aeromonas caviae", "");
		addOrganism("Aeromonas hydrophila", "");
		addOrganism("Aeromonas sobria", "");
		addOrganism("bacillus spoilage bacteria", "");
		addOrganism("Bacillus cereus", "");
		addOrganism("Bacillus licheniformis", "");
		addOrganism("Bacillus subtilis", "");
		addOrganism("Brochothrix thermosphacta", "");
		addOrganism("Clostridium botulinum (non-prot.)", "");
		addOrganism("Clostridium botulinum (prot.)", "");
		addOrganism("Campylobacter", "");
		addOrganism("Clostridium perfringens", "");
		addOrganism("Escherichia coli", "");
		addOrganism("enterobacteriaceae", "");
		addOrganism("lactic acid bacteria", "");
		addOrganism("Listeria monocytogenes/innocua", "");
		addOrganism("micrococci", "");
		addOrganism("Paenibacillus odorifer", "");
		addOrganism("Photobacterium phosphoreum", "");
		addOrganism("pseudomonads", "");
		addOrganism("psychrotrophic bacteria", "");
		addOrganism("Staphylococcus aureus", "");
		addOrganism("Shigella flexneri and relatives", "");
		addOrganism("salmonella spp", "");
		addOrganism("aerobic total spoilage bacteria", "");
		addOrganism("vibrio spp.", "");
		addOrganism("Yersinia enterocolitica", "");
		addOrganism("Shewanella putrefaciens", "");
		addOrganism("spoilage yeast", "");
		addOrganism("yeast", "");
		addOrganism("Enterococci", "");
		addOrganism("Bacillus pumilus", "");
		addOrganism("non-proteolytic psychrotrophic clostridia", "");
		addOrganism("mould", "");
		addOrganism("halophilic bacteria", "");
		addOrganism("Paenibacillus polymyxa", "");
	}

	private void addOrganism(String name, String description) {
		Organism organism = DataFactory.eINSTANCE.createOrganism();

		organism.setName(name);
		organism.setDescription(description);
		Utilities.setId(organism);
		organisms.add(organism);
	}

	private void readMatrices() {
		matrices = new ArrayList<Matrix>();
		addMatrix("Beef", "Beef");
		addMatrix("Pork", "Pork");
		addMatrix("Poultry", "Poultry");
		addMatrix("Sausage", "Sausage");
		addMatrix("Meat_other", "Other or unknown type of meat");
		addMatrix("Seafood", "Seafood");
		addMatrix("Milk", "Milk");
		addMatrix("Cheese", "Cheese");
		addMatrix("Dairy_other", "Other or unknown type of dairy");
		addMatrix("Egg", "Egg or egg product");
		addMatrix("Dessert", "Dessert food");
		addMatrix("Sauce/Dressing", "Sauce/Dressing");
		addMatrix("Produce", "Vegetable or fruit and their products");
		addMatrix("Bread", "Bread");
		addMatrix("Infant_food", "Infant_food");
		addMatrix("Beverage", "Juice, beverage");
		addMatrix("Water", "Water");
		addMatrix("Other/mix",
				"Other, mixed, uncategorised or unknown type of food");

	}

	private void addMatrix(String name, String description) {
		Matrix matrix = DataFactory.eINSTANCE.createMatrix();

		matrix.setName(name);
		matrix.setDescription(description);
		Utilities.setId(matrix);
		matrices.add(matrix);
	}

	private void readPrimaryModels() {
		primaryModels = new ArrayList<PrimaryModelFormula>();
		addBaranyiModel();
		addGompertzModel();
		addLogLinearTailModel();
	}

	private void addBaranyiModel() {
		String formula = "LOG10N0+Rmax*t"
				+ "+log10(10^(-Rmax*t)+10^(-Rmax*lag)-10^(-Rmax*(t+lag)))"
				+ "-log10(1+(10^(Rmax*(t-lag))-10^(-Rmax*lag))/(10^(LOG10Nmax-LOG10N0)))";
		Variable log10N = ModelsFactory.eINSTANCE.createVariable();
		Variable time = ModelsFactory.eINSTANCE.createVariable();
		Parameter log10N0 = ModelsFactory.eINSTANCE.createParameter();
		Parameter log10Nmax = ModelsFactory.eINSTANCE.createParameter();
		Parameter rMax = ModelsFactory.eINSTANCE.createParameter();
		Parameter lag = ModelsFactory.eINSTANCE.createParameter();

		log10N.setName("LOG10N");
		log10N.setUnit(getConcentrationQuantities().get(0).getDefaultUnit());
		time.setName("t");
		time.setUnit(getTimeQuantity().getDefaultUnit());
		log10N0.setName("LOG10N0");
		log10N0.setMin(0.0);
		log10N0.setMax(10.0);
		log10Nmax.setName("LOG10Nmax");
		log10Nmax.setMin(0.0);
		log10Nmax.setMax(10.0);
		rMax.setName("Rmax");
		rMax.setMin(0.0);
		rMax.setMax(1.0);
		lag.setName("lag");
		lag.setMin(0.0);
		lag.setMax(100.0);

		PrimaryModelFormula model = ModelsFactory.eINSTANCE
				.createPrimaryModelFormula();

		model.setName("Baranyi");
		model.setFormula(formula);
		model.setDepVar(log10N);
		model.setIndepVar(time);
		model.getParams().add(log10N0);
		model.getParams().add(log10Nmax);
		model.getParams().add(rMax);
		model.getParams().add(lag);
		Utilities.setId(model);

		primaryModels.add(model);
	}

	private void addGompertzModel() {
		String formula = "(LOG10Nmax-LOG10N0)"
				+ "*exp(-exp((Rmax*exp(1)*(lag-t))/(LOG10Nmax-LOG10N0)+1))";
		Variable log10N = ModelsFactory.eINSTANCE.createVariable();
		Variable time = ModelsFactory.eINSTANCE.createVariable();
		Parameter log10N0 = ModelsFactory.eINSTANCE.createParameter();
		Parameter log10Nmax = ModelsFactory.eINSTANCE.createParameter();
		Parameter rMax = ModelsFactory.eINSTANCE.createParameter();
		Parameter lag = ModelsFactory.eINSTANCE.createParameter();

		log10N.setName("LOG10N");
		log10N.setUnit(getConcentrationQuantities().get(0).getDefaultUnit());
		time.setName("t");
		time.setUnit(getTimeQuantity().getDefaultUnit());
		log10N0.setName("LOG10N0");
		log10N0.setMin(0.0);
		log10N0.setMax(10.0);
		log10Nmax.setName("LOG10Nmax");
		log10Nmax.setMin(0.0);
		log10Nmax.setMax(10.0);
		rMax.setName("Rmax");
		rMax.setMin(0.0);
		rMax.setMax(1.0);
		lag.setName("lag");
		lag.setMin(0.0);
		lag.setMax(100.0);

		PrimaryModelFormula model = ModelsFactory.eINSTANCE
				.createPrimaryModelFormula();

		model.setName("Gompertz");
		model.setFormula(formula);
		model.setDepVar(log10N);
		model.setIndepVar(time);
		model.getParams().add(log10N0);
		model.getParams().add(log10Nmax);
		model.getParams().add(rMax);
		model.getParams().add(lag);
		Utilities.setId(model);

		primaryModels.add(model);
	}

	private void addLogLinearTailModel() {
		String formula = "log((10^log10N0-10^log10Nres)*exp(-k*t)+10^log10Nres)";
		Variable log10N = ModelsFactory.eINSTANCE.createVariable();
		Variable time = ModelsFactory.eINSTANCE.createVariable();
		Parameter log10N0 = ModelsFactory.eINSTANCE.createParameter();
		Parameter log10Nres = ModelsFactory.eINSTANCE.createParameter();
		Parameter k = ModelsFactory.eINSTANCE.createParameter();

		log10N.setName("log10N");
		log10N.setUnit(getConcentrationQuantities().get(0).getDefaultUnit());
		time.setName("t");

		for (Unit unit : getTimeQuantity().getUnits()) {
			if (unit.getName().equals("min")) {
				time.setUnit(unit);
			}
		}

		log10N0.setName("log10N0");
		log10N0.setMin(0.0);
		log10N0.setMax(10.0);
		log10Nres.setName("log10Nres");
		log10Nres.setMin(0.0);
		log10Nres.setMax(10.0);
		k.setName("k");

		PrimaryModelFormula model = ModelsFactory.eINSTANCE
				.createPrimaryModelFormula();

		model.setName("LogLinearTail");
		model.setFormula(formula);
		model.setDepVar(log10N);
		model.setIndepVar(time);
		model.getParams().add(log10N0);
		model.getParams().add(log10Nres);
		model.getParams().add(k);
		Utilities.setId(model);

		primaryModels.add(model);
	}

	private void readSecondaryModels() {
		secondaryModels = new ArrayList<SecondaryModelFormula>();
		addTwoVarQuadraticModel();
		addLogLinearTailKModel();
		addLogLinearTailLog10NresModel();
	}

	private void addTwoVarQuadraticModel() {
		String formula = "a*x1^2+b*x1*x2+c*x2^2+d*x1+e*x2+f";
		Variable y = ModelsFactory.eINSTANCE.createVariable();
		Variable x1 = ModelsFactory.eINSTANCE.createVariable();
		Variable x2 = ModelsFactory.eINSTANCE.createVariable();
		Parameter a = ModelsFactory.eINSTANCE.createParameter();
		Parameter b = ModelsFactory.eINSTANCE.createParameter();
		Parameter c = ModelsFactory.eINSTANCE.createParameter();
		Parameter d = ModelsFactory.eINSTANCE.createParameter();
		Parameter e = ModelsFactory.eINSTANCE.createParameter();
		Parameter f = ModelsFactory.eINSTANCE.createParameter();

		y.setName("y");
		x1.setName("x1");
		x2.setName("x2");
		a.setName("a");
		b.setName("b");
		c.setName("c");
		d.setName("d");
		e.setName("e");
		f.setName("f");

		SecondaryModelFormula model = ModelsFactory.eINSTANCE
				.createSecondaryModelFormula();

		model.setName("2 Var Quadratic");
		model.setFormula(formula);
		model.setDepVar(y);
		model.getIndepVars().add(x1);
		model.getIndepVars().add(x2);
		model.getParams().add(a);
		model.getParams().add(b);
		model.getParams().add(c);
		model.getParams().add(d);
		model.getParams().add(e);
		model.getParams().add(f);
		Utilities.setId(model);

		secondaryModels.add(model);
	}

	private void addLogLinearTailKModel() {
		String formula = "exp(k1+k2/T+k3*carv^2+k4*carv/T+k5*cin/T)";
		Variable k = ModelsFactory.eINSTANCE.createVariable();
		Variable temperature = ModelsFactory.eINSTANCE.createVariable();
		Variable carv = ModelsFactory.eINSTANCE.createVariable();
		Variable cin = ModelsFactory.eINSTANCE.createVariable();
		Parameter k1 = ModelsFactory.eINSTANCE.createParameter();
		Parameter k2 = ModelsFactory.eINSTANCE.createParameter();
		Parameter k3 = ModelsFactory.eINSTANCE.createParameter();
		Parameter k4 = ModelsFactory.eINSTANCE.createParameter();
		Parameter k5 = ModelsFactory.eINSTANCE.createParameter();

		k.setName("k");
		temperature.setName("T");
		temperature.setUnit(getTemperatureQuantity().getDefaultUnit());
		carv.setName("carv");
		carv.setUnit(getVolumeConcentrationQuantity().getDefaultUnit());
		cin.setName("cin");
		cin.setUnit(getVolumeConcentrationQuantity().getDefaultUnit());
		k1.setName("k1");
		k2.setName("k2");
		k3.setName("k3");
		k4.setName("k4");
		k5.setName("k5");

		SecondaryModelFormula model = ModelsFactory.eINSTANCE
				.createSecondaryModelFormula();

		model.setName("K (LogLinearTail)");
		model.setFormula(formula);
		model.setDepVar(k);
		model.getIndepVars().add(temperature);
		model.getIndepVars().add(carv);
		model.getIndepVars().add(cin);
		model.getParams().add(k1);
		model.getParams().add(k2);
		model.getParams().add(k3);
		model.getParams().add(k4);
		model.getParams().add(k5);
		Utilities.setId(model);

		secondaryModels.add(model);
	}

	private void addLogLinearTailLog10NresModel() {
		String formula = "r1+r2*cin/T+r3*ln(T)";
		Variable log10Nres = ModelsFactory.eINSTANCE.createVariable();
		Variable temperature = ModelsFactory.eINSTANCE.createVariable();
		Variable cin = ModelsFactory.eINSTANCE.createVariable();
		Parameter r1 = ModelsFactory.eINSTANCE.createParameter();
		Parameter r2 = ModelsFactory.eINSTANCE.createParameter();
		Parameter r3 = ModelsFactory.eINSTANCE.createParameter();

		log10Nres.setName("log10Nres");
		temperature.setName("T");
		temperature.setUnit(getTemperatureQuantity().getDefaultUnit());
		cin.setName("cin");
		cin.setUnit(getVolumeConcentrationQuantity().getDefaultUnit());
		r1.setName("r1");
		r2.setName("r2");
		r3.setName("r3");

		SecondaryModelFormula model = ModelsFactory.eINSTANCE
				.createSecondaryModelFormula();

		model.setName("Log10Nres (LogLinearTail)");
		model.setFormula(formula);
		model.setDepVar(log10Nres);
		model.getIndepVars().add(temperature);
		model.getIndepVars().add(cin);
		model.getParams().add(r1);
		model.getParams().add(r2);
		model.getParams().add(r3);
		Utilities.setId(model);

		secondaryModels.add(model);
	}
}

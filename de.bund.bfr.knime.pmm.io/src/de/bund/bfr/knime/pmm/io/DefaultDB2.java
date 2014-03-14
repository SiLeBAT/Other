package de.bund.bfr.knime.pmm.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;

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

public class DefaultDB2 {

	private Unit minute;
	private Unit hour;
	private Unit log10CountPerGram;
	private Unit celsius;
	private Unit percent;

	private QuantityType temperatureQuantity;
	private QuantityType phQuantity;
	private QuantityType waterActivityQuantity;
	private QuantityType volumeConcentrationQuantity;

	public DefaultDB2() {
		Session session = DB.getDataStore().getSessionFactory().openSession();

		session.beginTransaction();

		for (QuantityType quantity : getQuantityTypes()) {
			session.save(quantity);
		}

		for (ConditionParameter param : getConditionParameters()) {
			session.save(param);
		}

		for (Organism organism : getOrganisms()) {
			session.save(organism);
		}

		for (Matrix matrix : getMatrices()) {
			session.save(matrix);
		}

		for (PrimaryModelFormula formula : getPrimaryModels()) {
			session.save(formula);
		}

		for (SecondaryModelFormula formula : getSecondaryModels()) {
			session.save(formula);
		}

		session.getTransaction().commit();
		session.close();
	}

	private List<QuantityType> getQuantityTypes() {
		List<QuantityType> quantities = new ArrayList<QuantityType>();

		temperatureQuantity = createTemperatureQuantity();
		phQuantity = createPhQuantity();
		waterActivityQuantity = createWaterActivityQuantity();
		volumeConcentrationQuantity = createVolumeConcentrationQuantity();

		quantities.add(createTimeQuantity());
		quantities.add(createNumberContentQuantity());
		quantities.add(createNumberConcentrationQuantity());
		quantities.add(temperatureQuantity);
		quantities.add(phQuantity);
		quantities.add(waterActivityQuantity);
		quantities.add(volumeConcentrationQuantity);

		return quantities;
	}

	private List<ConditionParameter> getConditionParameters() {
		List<ConditionParameter> conditionParameters = new ArrayList<ConditionParameter>();

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
		temperature.getQuantityTypes().add(temperatureQuantity);
		pH.setName("pH");
		pH.getQuantityTypes().add(phQuantity);
		waterActivity.setName("Water Activity");
		waterActivity.getQuantityTypes().add(waterActivityQuantity);
		carvacrol.setName("Carvacrol");
		carvacrol.getQuantityTypes().add(volumeConcentrationQuantity);
		cinnamaldehyde.setName("Cinnamaldehyde");
		cinnamaldehyde.getQuantityTypes().add(volumeConcentrationQuantity);

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

		return conditionParameters;
	}

	private List<Organism> getOrganisms() {
		List<Organism> organisms = new ArrayList<Organism>();

		organisms.add(createOrganism("Aeromonas caviae", ""));
		organisms.add(createOrganism("Aeromonas hydrophila", ""));
		organisms.add(createOrganism("Aeromonas sobria", ""));
		organisms.add(createOrganism("bacillus spoilage bacteria", ""));
		organisms.add(createOrganism("Bacillus cereus", ""));
		organisms.add(createOrganism("Bacillus licheniformis", ""));
		organisms.add(createOrganism("Bacillus subtilis", ""));
		organisms.add(createOrganism("Brochothrix thermosphacta", ""));
		organisms.add(createOrganism("Clostridium botulinum (non-prot.)", ""));
		organisms.add(createOrganism("Clostridium botulinum (prot.)", ""));
		organisms.add(createOrganism("Campylobacter", ""));
		organisms.add(createOrganism("Clostridium perfringens", ""));
		organisms.add(createOrganism("Escherichia coli", ""));
		organisms.add(createOrganism("enterobacteriaceae", ""));
		organisms.add(createOrganism("lactic acid bacteria", ""));
		organisms.add(createOrganism("Listeria monocytogenes/innocua", ""));
		organisms.add(createOrganism("micrococci", ""));
		organisms.add(createOrganism("Paenibacillus odorifer", ""));
		organisms.add(createOrganism("Photobacterium phosphoreum", ""));
		organisms.add(createOrganism("pseudomonads", ""));
		organisms.add(createOrganism("psychrotrophic bacteria", ""));
		organisms.add(createOrganism("Staphylococcus aureus", ""));
		organisms.add(createOrganism("Shigella flexneri and relatives", ""));
		organisms.add(createOrganism("salmonella spp", ""));
		organisms.add(createOrganism("aerobic total spoilage bacteria", ""));
		organisms.add(createOrganism("vibrio spp.", ""));
		organisms.add(createOrganism("Yersinia enterocolitica", ""));
		organisms.add(createOrganism("Shewanella putrefaciens", ""));
		organisms.add(createOrganism("spoilage yeast", ""));
		organisms.add(createOrganism("yeast", ""));
		organisms.add(createOrganism("Enterococci", ""));
		organisms.add(createOrganism("Bacillus pumilus", ""));
		organisms.add(createOrganism(
				"non-proteolytic psychrotrophic clostridia", ""));
		organisms.add(createOrganism("mould", ""));
		organisms.add(createOrganism("halophilic bacteria", ""));
		organisms.add(createOrganism("Paenibacillus polymyxa", ""));

		return organisms;
	}

	private List<Matrix> getMatrices() {
		List<Matrix> matrices = new ArrayList<Matrix>();

		matrices.add(createMatrix("Beef", "Beef"));
		matrices.add(createMatrix("Pork", "Pork"));
		matrices.add(createMatrix("Poultry", "Poultry"));
		matrices.add(createMatrix("Sausage", "Sausage"));
		matrices.add(createMatrix("Meat_other", "Other or unknown type of meat"));
		matrices.add(createMatrix("Seafood", "Seafood"));
		matrices.add(createMatrix("Milk", "Milk"));
		matrices.add(createMatrix("Cheese", "Cheese"));
		matrices.add(createMatrix("Dairy_other",
				"Other or unknown type of dairy"));
		matrices.add(createMatrix("Egg", "Egg or egg product"));
		matrices.add(createMatrix("Dessert", "Dessert food"));
		matrices.add(createMatrix("Sauce/Dressing", "Sauce/Dressing"));
		matrices.add(createMatrix("Produce",
				"Vegetable or fruit and their products"));
		matrices.add(createMatrix("Bread", "Bread"));
		matrices.add(createMatrix("Infant_food", "Infant_food"));
		matrices.add(createMatrix("Beverage", "Juice, beverage"));
		matrices.add(createMatrix("Water", "Water"));
		matrices.add(createMatrix("Other/mix",
				"Other, mixed, uncategorised or unknown type of food"));

		return matrices;
	}

	private List<PrimaryModelFormula> getPrimaryModels() {
		List<PrimaryModelFormula> primaryModels = new ArrayList<PrimaryModelFormula>();

		primaryModels.add(createBaranyiModel());
		primaryModels.add(createGompertzModel());
		primaryModels.add(createLogLinearTailModel());

		return primaryModels;
	}

	private List<SecondaryModelFormula> getSecondaryModels() {
		List<SecondaryModelFormula> secondaryModels = new ArrayList<SecondaryModelFormula>();

		secondaryModels.add(createTwoVarQuadraticModel());
		secondaryModels.add(createLogLinearTailKModel());
		secondaryModels.add(createLogLinearTailLog10NresModel());

		return secondaryModels;
	}

	private QuantityType createTimeQuantity() {
		QuantityType timeQuantity = CommonFactory.eINSTANCE
				.createQuantityType();
		Unit second = CommonFactory.eINSTANCE.createUnit();
		minute = CommonFactory.eINSTANCE.createUnit();
		hour = CommonFactory.eINSTANCE.createUnit();
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

		return timeQuantity;
	}

	private QuantityType createNumberContentQuantity() {
		QuantityType numberContentQuantity = CommonFactory.eINSTANCE
				.createQuantityType();
		Unit countPerGram = CommonFactory.eINSTANCE.createUnit();
		log10CountPerGram = CommonFactory.eINSTANCE.createUnit();
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

		return numberContentQuantity;
	}

	private QuantityType createNumberConcentrationQuantity() {
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

		return numberConcentrationQuantity;
	}

	private QuantityType createTemperatureQuantity() {
		QuantityType temperatureQuantity = CommonFactory.eINSTANCE
				.createQuantityType();
		celsius = CommonFactory.eINSTANCE.createUnit();
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

		return temperatureQuantity;
	}

	private QuantityType createPhQuantity() {
		QuantityType phQuantity = CommonFactory.eINSTANCE.createQuantityType();
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

		return phQuantity;
	}

	private QuantityType createWaterActivityQuantity() {
		QuantityType waterActivityQuantity = CommonFactory.eINSTANCE
				.createQuantityType();
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

		return waterActivityQuantity;
	}

	private QuantityType createVolumeConcentrationQuantity() {
		QuantityType volumeConcentrationQuantity = CommonFactory.eINSTANCE
				.createQuantityType();
		percent = CommonFactory.eINSTANCE.createUnit();

		percent.setName("% vol/wt");
		percent.setConvertFrom("x");
		percent.setConvertTo("x");
		percent.setQuantityType(volumeConcentrationQuantity);
		volumeConcentrationQuantity.setName("Volume Concentration");
		volumeConcentrationQuantity.getUnits().add(percent);
		volumeConcentrationQuantity.setDefaultUnit(percent);

		Utilities.setId(percent);
		Utilities.setId(volumeConcentrationQuantity);

		return volumeConcentrationQuantity;
	}

	private Organism createOrganism(String name, String description) {
		Organism organism = DataFactory.eINSTANCE.createOrganism();

		organism.setName(name);
		organism.setDescription(description);
		Utilities.setId(organism);

		return organism;
	}

	private Matrix createMatrix(String name, String description) {
		Matrix matrix = DataFactory.eINSTANCE.createMatrix();

		matrix.setName(name);
		matrix.setDescription(description);
		Utilities.setId(matrix);

		return matrix;
	}

	private PrimaryModelFormula createBaranyiModel() {
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
		log10N.setUnit(log10CountPerGram);
		time.setName("t");
		time.setUnit(hour);
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

		return model;
	}

	private PrimaryModelFormula createGompertzModel() {
		String formula = "(LOG10Nmax-LOG10N0)"
				+ "*exp(-exp((Rmax*exp(1)*(lag-t))/(LOG10Nmax-LOG10N0)+1))";
		Variable log10N = ModelsFactory.eINSTANCE.createVariable();
		Variable time = ModelsFactory.eINSTANCE.createVariable();
		Parameter log10N0 = ModelsFactory.eINSTANCE.createParameter();
		Parameter log10Nmax = ModelsFactory.eINSTANCE.createParameter();
		Parameter rMax = ModelsFactory.eINSTANCE.createParameter();
		Parameter lag = ModelsFactory.eINSTANCE.createParameter();

		log10N.setName("LOG10N");
		log10N.setUnit(log10CountPerGram);
		time.setName("t");
		time.setUnit(hour);
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

		return model;
	}

	private PrimaryModelFormula createLogLinearTailModel() {
		String formula = "log((10^log10N0-10^log10Nres)*exp(-k*t)+10^log10Nres)";
		Variable log10N = ModelsFactory.eINSTANCE.createVariable();
		Variable time = ModelsFactory.eINSTANCE.createVariable();
		Parameter log10N0 = ModelsFactory.eINSTANCE.createParameter();
		Parameter log10Nres = ModelsFactory.eINSTANCE.createParameter();
		Parameter k = ModelsFactory.eINSTANCE.createParameter();

		log10N.setName("log10N");
		log10N.setUnit(log10CountPerGram);
		time.setName("t");
		time.setUnit(minute);
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

		return model;
	}

	private SecondaryModelFormula createTwoVarQuadraticModel() {
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

		return model;
	}

	private SecondaryModelFormula createLogLinearTailKModel() {
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
		temperature.setUnit(celsius);
		carv.setName("carv");
		carv.setUnit(percent);
		cin.setName("cin");
		cin.setUnit(percent);
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

		return model;
	}

	private SecondaryModelFormula createLogLinearTailLog10NresModel() {
		String formula = "r1+r2*cin/T+r3*ln(T)";
		Variable log10Nres = ModelsFactory.eINSTANCE.createVariable();
		Variable temperature = ModelsFactory.eINSTANCE.createVariable();
		Variable cin = ModelsFactory.eINSTANCE.createVariable();
		Parameter r1 = ModelsFactory.eINSTANCE.createParameter();
		Parameter r2 = ModelsFactory.eINSTANCE.createParameter();
		Parameter r3 = ModelsFactory.eINSTANCE.createParameter();

		log10Nres.setName("log10Nres");
		temperature.setName("T");
		temperature.setUnit(celsius);
		cin.setName("cin");
		cin.setUnit(percent);
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

		return model;
	}
}

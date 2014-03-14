package de.bund.bfr.knime.pmm.io.formulareader;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import de.bund.bfr.knime.pmm.core.EmfUtilities;
import de.bund.bfr.knime.pmm.core.models.PrimaryModelFormula;
import de.bund.bfr.knime.pmm.core.models.SecondaryModelFormula;

public class FormulaReaderSettings {

	protected static final String CFGKEY_MODEL_TYPE = "ModelType";
	protected static final String CFGKEY_PRIMARY_MODELS = "PrimaryModels";
	protected static final String CFGKEY_SECONDARY_MODELS = "SecondaryModels";

	protected static final String PRIMARY_TYPE = "Primary";
	protected static final String SECONDARY_TYPE = "Secondary";
	protected static final String DEFAULT_MODEL_TYPE = PRIMARY_TYPE;

	private String modelType;
	private List<PrimaryModelFormula> primaryModels;
	private List<SecondaryModelFormula> secondaryModels;

	public FormulaReaderSettings() {
		modelType = DEFAULT_MODEL_TYPE;
		primaryModels = new ArrayList<PrimaryModelFormula>();
		secondaryModels = new ArrayList<SecondaryModelFormula>();
	}

	public void load(NodeSettingsRO settings) {
		try {
			modelType = settings.getString(CFGKEY_MODEL_TYPE);
		} catch (InvalidSettingsException e) {
			modelType = DEFAULT_MODEL_TYPE;
		}

		try {
			primaryModels = EmfUtilities.listFromXml(
					settings.getString(CFGKEY_PRIMARY_MODELS),
					new ArrayList<PrimaryModelFormula>());
		} catch (InvalidSettingsException e) {
			primaryModels = new ArrayList<PrimaryModelFormula>();
		}

		try {
			secondaryModels = EmfUtilities.listFromXml(
					settings.getString(CFGKEY_SECONDARY_MODELS),
					new ArrayList<SecondaryModelFormula>());
		} catch (InvalidSettingsException e) {
			secondaryModels = new ArrayList<SecondaryModelFormula>();
		}
	}

	public void save(NodeSettingsWO settings) {
		settings.addString(CFGKEY_MODEL_TYPE, modelType);
		settings.addString(CFGKEY_PRIMARY_MODELS,
				EmfUtilities.listToXml(primaryModels));
		settings.addString(CFGKEY_SECONDARY_MODELS,
				EmfUtilities.listToXml(secondaryModels));
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public List<PrimaryModelFormula> getPrimaryModels() {
		return primaryModels;
	}

	public void setPrimaryModels(List<PrimaryModelFormula> primaryModels) {
		this.primaryModels = primaryModels;
	}

	public List<SecondaryModelFormula> getSecondaryModels() {
		return secondaryModels;
	}

	public void setSecondaryModels(List<SecondaryModelFormula> secondaryModels) {
		this.secondaryModels = secondaryModels;
	}
}

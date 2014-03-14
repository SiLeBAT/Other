package de.bund.bfr.knime.pmm.io.xls.datareader;

import java.util.LinkedHashMap;
import java.util.Map;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import de.bund.bfr.knime.pmm.core.EmfUtilities;
import de.bund.bfr.knime.pmm.core.XmlUtilities;
import de.bund.bfr.knime.pmm.core.common.Unit;
import de.bund.bfr.knime.pmm.core.data.ConditionParameter;
import de.bund.bfr.knime.pmm.core.data.Matrix;
import de.bund.bfr.knime.pmm.core.data.Organism;

public class XlsDataReaderSettings {

	protected static final String CFGKEY_FILENAME = "FileName";
	protected static final String CFGKEY_SHEETNAME = "SheetName";
	protected static final String CFGKEY_COLUMNMAPPINGS = "ColumnMappings";
	protected static final String CFGKEY_CONDITIONS = "Conditions";
	protected static final String CFGKEY_CONDITIONUNITS = "ConditionUnits";
	protected static final String CFGKEY_TIMEUNIT = "TimeUnit";
	protected static final String CFGKEY_CONCENTRATIONUNIT = "ConcentrationUnit";
	protected static final String CFGKEY_ORGANISM = "Organism";
	protected static final String CFGKEY_ORGANISMCOLUMN = "OrganismColumn";
	protected static final String CFGKEY_ORGANISMMAPPINGS = "OrganismMappings";
	protected static final String CFGKEY_MATRIX = "Matrix";
	protected static final String CFGKEY_MATRIXCOLUMN = "MatrixColumn";
	protected static final String CFGKEY_MATRIXMAPPINGS = "MatrixMappings";

	private String fileName;
	private String sheetName;
	private Map<String, String> columnMappings;
	private Map<String, ConditionParameter> conditions;
	private Map<String, Unit> conditionUnits;
	private Unit timeUnit;
	private Unit concentrationUnit;
	private Organism organism;
	private String organismColumn;
	private Map<String, Organism> organismMappings;
	private Matrix matrix;
	private String matrixColumn;
	private Map<String, Matrix> matrixMappings;

	public XlsDataReaderSettings() {
		fileName = null;
		sheetName = null;
		columnMappings = new LinkedHashMap<String, String>();
		conditions = new LinkedHashMap<String, ConditionParameter>();
		conditionUnits = new LinkedHashMap<String, Unit>();
		timeUnit = null;
		concentrationUnit = null;
		organismColumn = null;
		organismMappings = new LinkedHashMap<String, Organism>();
		matrixColumn = null;
		matrixMappings = new LinkedHashMap<String, Matrix>();
		organism = null;
		matrix = null;
	}

	public void load(NodeSettingsRO settings) {
		try {
			fileName = settings.getString(CFGKEY_FILENAME);
		} catch (InvalidSettingsException e) {
			fileName = null;
		}

		try {
			sheetName = settings.getString(CFGKEY_SHEETNAME);
		} catch (InvalidSettingsException e) {
			sheetName = null;
		}

		try {
			columnMappings = XmlUtilities.fromXml(
					settings.getString(CFGKEY_COLUMNMAPPINGS),
					new LinkedHashMap<String, String>());
		} catch (InvalidSettingsException e) {
			columnMappings = new LinkedHashMap<String, String>();
		}

		try {
			conditions = EmfUtilities.mapFromXml(
					settings.getString(CFGKEY_CONDITIONS),
					new LinkedHashMap<String, ConditionParameter>());
		} catch (InvalidSettingsException e) {
			conditions = new LinkedHashMap<String, ConditionParameter>();
		}

		try {
			conditionUnits = EmfUtilities.mapFromXml(
					settings.getString(CFGKEY_CONDITIONUNITS),
					new LinkedHashMap<String, Unit>());
		} catch (InvalidSettingsException e) {
			conditionUnits = new LinkedHashMap<String, Unit>();
		}

		try {
			timeUnit = EmfUtilities.fromXml(
					settings.getString(CFGKEY_TIMEUNIT), null);
		} catch (InvalidSettingsException e) {
			timeUnit = null;
		}

		try {
			concentrationUnit = EmfUtilities.fromXml(
					settings.getString(CFGKEY_CONCENTRATIONUNIT), null);
		} catch (InvalidSettingsException e) {
			concentrationUnit = null;
		}

		try {
			organism = EmfUtilities.fromXml(
					settings.getString(CFGKEY_ORGANISM), null);
		} catch (InvalidSettingsException e) {
			organism = null;
		}

		try {
			organismColumn = settings.getString(CFGKEY_ORGANISMCOLUMN);
		} catch (InvalidSettingsException e) {
			organismColumn = null;
		}

		try {
			organismMappings = EmfUtilities.mapFromXml(
					settings.getString(CFGKEY_ORGANISMMAPPINGS),
					new LinkedHashMap<String, Organism>());
		} catch (InvalidSettingsException e) {
			organismMappings = new LinkedHashMap<String, Organism>();
		}

		try {
			matrix = EmfUtilities.fromXml(settings.getString(CFGKEY_MATRIX),
					null);
		} catch (InvalidSettingsException e) {
			matrix = null;
		}

		try {
			matrixColumn = settings.getString(CFGKEY_MATRIXCOLUMN);
		} catch (InvalidSettingsException e) {
			matrixColumn = null;
		}

		try {
			matrixMappings = EmfUtilities.mapFromXml(
					settings.getString(CFGKEY_MATRIXMAPPINGS),
					new LinkedHashMap<String, Matrix>());
		} catch (InvalidSettingsException e) {
			matrixMappings = new LinkedHashMap<String, Matrix>();
		}
	}

	public void save(NodeSettingsWO settings) {
		settings.addString(CFGKEY_FILENAME, fileName);
		settings.addString(CFGKEY_SHEETNAME, sheetName);
		settings.addString(CFGKEY_COLUMNMAPPINGS,
				XmlUtilities.toXml(columnMappings));
		settings.addString(CFGKEY_CONDITIONS, EmfUtilities.mapToXml(conditions));
		settings.addString(CFGKEY_CONDITIONUNITS,
				EmfUtilities.mapToXml(conditionUnits));
		settings.addString(CFGKEY_TIMEUNIT, EmfUtilities.toXml(timeUnit));
		settings.addString(CFGKEY_CONCENTRATIONUNIT,
				EmfUtilities.toXml(concentrationUnit));
		settings.addString(CFGKEY_ORGANISM, EmfUtilities.toXml(organism));
		settings.addString(CFGKEY_ORGANISMCOLUMN, organismColumn);
		settings.addString(CFGKEY_ORGANISMMAPPINGS,
				EmfUtilities.mapToXml(organismMappings));
		settings.addString(CFGKEY_MATRIX, EmfUtilities.toXml(matrix));
		settings.addString(CFGKEY_MATRIXCOLUMN, matrixColumn);
		settings.addString(CFGKEY_MATRIXMAPPINGS,
				EmfUtilities.mapToXml(matrixMappings));
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public Map<String, String> getColumnMappings() {
		return columnMappings;
	}

	public void setColumnMappings(Map<String, String> columnMappings) {
		this.columnMappings = columnMappings;
	}

	public Map<String, ConditionParameter> getConditions() {
		return conditions;
	}

	public void setConditions(Map<String, ConditionParameter> conditions) {
		this.conditions = conditions;
	}

	public Map<String, Unit> getConditionUnits() {
		return conditionUnits;
	}

	public void setConditionUnits(Map<String, Unit> conditionUnits) {
		this.conditionUnits = conditionUnits;
	}

	public Unit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(Unit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public Unit getConcentrationUnit() {
		return concentrationUnit;
	}

	public void setConcentrationUnit(Unit concentrationUnit) {
		this.concentrationUnit = concentrationUnit;
	}

	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	public String getOrganismColumn() {
		return organismColumn;
	}

	public void setOrganismColumn(String organismColumn) {
		this.organismColumn = organismColumn;
	}

	public Map<String, Organism> getOrganismMappings() {
		return organismMappings;
	}

	public void setOrganismMappings(Map<String, Organism> organismMappings) {
		this.organismMappings = organismMappings;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}

	public String getMatrixColumn() {
		return matrixColumn;
	}

	public void setMatrixColumn(String matrixColumn) {
		this.matrixColumn = matrixColumn;
	}

	public Map<String, Matrix> getMatrixMappings() {
		return matrixMappings;
	}

	public void setMatrixMappings(Map<String, Matrix> matrixMappings) {
		this.matrixMappings = matrixMappings;
	}
}

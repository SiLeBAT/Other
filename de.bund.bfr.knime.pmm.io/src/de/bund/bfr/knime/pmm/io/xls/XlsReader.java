package de.bund.bfr.knime.pmm.io.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import de.bund.bfr.knime.pmm.core.Utilities;
import de.bund.bfr.knime.pmm.core.common.Unit;
import de.bund.bfr.knime.pmm.core.data.Condition;
import de.bund.bfr.knime.pmm.core.data.ConditionParameter;
import de.bund.bfr.knime.pmm.core.data.DataFactory;
import de.bund.bfr.knime.pmm.core.data.Matrix;
import de.bund.bfr.knime.pmm.core.data.Organism;
import de.bund.bfr.knime.pmm.core.data.TimeSeries;
import de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint;

public class XlsReader {

	public static final String ID_COLUMN = "ID";
	public static final String TIME_COLUMN = "Time";
	public static final String CONCENTRATION_COLUMN = "Concentration";
	public static final String CONDITION_COLUMN = "Condition";

	private Workbook wb;
	private Sheet s;
	private List<String> warnings;

	public XlsReader() {
		wb = null;
		s = null;
		warnings = new ArrayList<String>();
	}

	public void setFile(String fileName) throws Exception {
		File file = new File(fileName);
		InputStream inputStream = null;

		if (file.exists()) {
			inputStream = new FileInputStream(file);
		} else {
			try {
				URL url = new URL(file.getPath());

				inputStream = url.openStream();
			} catch (Exception e) {
				throw new FileNotFoundException("File not found");
			}
		}

		wb = WorkbookFactory.create(inputStream);
	}

	public void setSheet(String sheetName) throws Exception {
		if (wb == null) {
			throw new FileNotFoundException("File not found");
		}

		s = wb.getSheet(sheetName);
	}

	public List<TimeSeries> getTimeSeriesList(
			Map<String, String> columnMappings,
			Map<String, ConditionParameter> conditions,
			Map<String, Unit> conditionUnits, Unit timeUnit,
			Unit concentrationUnit, Organism organism,
			String organismColumnName, Map<String, Organism> organismMappings,
			Matrix matrix, String matrixColumnName,
			Map<String, Matrix> matrixMappings) throws Exception {
		if (s == null) {
			throw new Exception("Sheet not found");
		}

		warnings.clear();

		List<TimeSeries> list = new ArrayList<TimeSeries>();
		Map<String, Integer> columns = getColumns(s);
		Integer idColumn = null;
		Integer timeColumn = null;
		Integer logcColumn = null;
		Integer organismColumn = null;
		Integer matrixColumn = null;
		String timeColumnName = null;
		String logcColumnName = null;

		if (organismColumnName != null) {
			organismColumn = columns.get(organismColumnName);
		}

		if (matrixColumnName != null) {
			matrixColumn = columns.get(matrixColumnName);
		}

		for (String column : columns.keySet()) {
			if (columnMappings.containsKey(column)) {
				String mapping = columnMappings.get(column);

				if (mapping.equals(ID_COLUMN)) {
					idColumn = columns.get(column);
				} else if (mapping.equals(TIME_COLUMN)) {
					timeColumn = columns.get(column);
					timeColumnName = column;
				} else if (mapping.equals(CONCENTRATION_COLUMN)) {
					logcColumn = columns.get(column);
					logcColumnName = column;
				}
			}
		}

		TimeSeries timeSeries = null;
		String id = null;

		for (int i = 1;; i++) {
			if (isEndOfFile(s, i)) {
				if (timeSeries != null) {
					list.add(timeSeries);
				}

				break;
			}

			Row row = s.getRow(i);
			Cell idCell = null;
			Cell timeCell = null;
			Cell logcCell = null;
			Cell organismCell = null;
			Cell matrixCell = null;

			if (idColumn != null) {
				idCell = row.getCell(idColumn);
			}

			if (timeColumn != null) {
				timeCell = row.getCell(timeColumn);
			}

			if (logcColumn != null) {
				logcCell = row.getCell(logcColumn);
			}

			if (organismColumn != null) {
				organismCell = row.getCell(organismColumn);
			}

			if (matrixColumn != null) {
				matrixCell = row.getCell(matrixColumn);
			}

			if (hasData(idCell) && !getData(idCell).equals(id)) {
				if (timeSeries != null) {
					list.add(timeSeries);
				}

				id = getData(idCell);
				timeSeries = DataFactory.eINSTANCE.createTimeSeries();
				timeSeries.setName(Utilities.getRandomId());
				timeSeries.setTimeUnit(timeUnit);
				timeSeries.setConcentrationUnit(concentrationUnit);
				Utilities.setId(timeSeries);

				if (hasData(organismCell)
						&& organismMappings.get(getData(organismCell)) != null) {
					timeSeries.setOrganism(organismMappings
							.get(getData(organismCell)));
				} else if (organismColumnName == null && organism != null) {
					timeSeries.setOrganism(organism);
				}

				if (hasData(matrixCell)
						&& matrixMappings.get(getData(matrixCell)) != null) {
					timeSeries.setMatrix(matrixMappings
							.get(getData(matrixCell)));
				} else if (matrixColumnName == null && matrix != null) {
					timeSeries.setMatrix(matrix);
				}

				for (String column : conditions.keySet()) {
					ConditionParameter conditionParam = conditions.get(column);
					Condition condition = DataFactory.eINSTANCE
							.createCondition();
					Cell cell = row.getCell(columns.get(column));

					if (hasData(cell)) {
						try {
							condition.setValue(getDouble(cell));
						} catch (NumberFormatException e) {
							warnings.add(column + " value in row " + (i + 1)
									+ " is not valid ("
									+ cell.toString().trim() + ")");
						}
					}

					condition.setParameter(conditionParam);
					condition.setUnit(conditionUnits.get(column));
					timeSeries.getConditions().add(condition);
				}
			}

			if (timeSeries != null) {
				double time = Double.NaN;
				double concentration = Double.NaN;

				if (hasData(timeCell)) {
					try {
						time = getDouble(timeCell);
					} catch (NumberFormatException e) {
						warnings.add(timeColumnName + " value in row "
								+ (i + 1) + " is not valid ("
								+ getData(timeCell) + ")");
					}
				}

				if (hasData(logcCell)) {
					try {
						concentration = getDouble(logcCell);
					} catch (NumberFormatException e) {
						warnings.add(logcColumnName + " value in row "
								+ (i + 1) + " is not valid ("
								+ getData(logcCell) + ")");
					}
				}

				TimeSeriesPoint point = DataFactory.eINSTANCE
						.createTimeSeriesPoint();

				point.setTime(time);
				point.setConcentration(concentration);
				timeSeries.getPoints().add(point);
			}
		}

		return list;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public List<String> getSheets() throws Exception {
		if (wb == null) {
			throw new FileNotFoundException("File not found");
		}

		List<String> sheets = new ArrayList<String>();

		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			sheets.add(wb.getSheetName(i));
		}

		return sheets;
	}

	public List<String> getColumns() throws Exception {
		if (s == null) {
			throw new Exception("Sheet not found");
		}

		return new ArrayList<String>(getColumns(s).keySet());
	}

	public Set<String> getValuesInColumn(String column) throws Exception {
		if (s == null) {
			throw new Exception("Sheet not found");
		}

		Set<String> valueSet = new LinkedHashSet<String>();
		Map<String, Integer> columns = getColumns(s);
		int columnId = columns.get(column);

		for (int i = 1; i <= s.getLastRowNum(); i++) {
			if (s.getRow(i) != null) {
				Cell cell = s.getRow(i).getCell(columnId);

				if (hasData(cell)) {
					valueSet.add(getData(cell));
				}
			}
		}

		return valueSet;
	}

	private Map<String, Integer> getColumns(Sheet sheet) {
		Map<String, Integer> columns = new LinkedHashMap<String, Integer>();

		for (int i = 0;; i++) {
			Cell cell = sheet.getRow(0).getCell(i);

			if (!hasData(cell)) {
				break;
			}

			columns.put(getData(cell), i);
		}

		return columns;
	}

	private boolean isEndOfFile(Sheet sheet, int i) {
		Row row = sheet.getRow(i);

		if (row == null) {
			return true;
		}

		for (int j = 0;; j++) {
			Cell headerCell = sheet.getRow(0).getCell(j);
			Cell cell = sheet.getRow(i).getCell(j);

			if (!hasData(headerCell)) {
				return true;
			}

			if (hasData(cell)) {
				return false;
			}
		}
	}

	private boolean hasData(Cell cell) {
		return cell != null && !cell.toString().trim().isEmpty();
	}

	private String getData(Cell cell) {
		if (cell != null) {
			if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
				CellValue value = wb.getCreationHelper()
						.createFormulaEvaluator().evaluate(cell);

				switch (value.getCellType()) {
				case Cell.CELL_TYPE_BOOLEAN:
					return value.getBooleanValue() + "";
				case Cell.CELL_TYPE_NUMERIC:
					return value.getNumberValue() + "";
				case Cell.CELL_TYPE_STRING:
					return value.getStringValue();
				default:
					return "";
				}
			} else {
				return cell.toString().trim();
			}
		}

		return null;
	}

	private double getDouble(Cell cell) throws NumberFormatException {
		return Double.parseDouble(getData(cell).replace(",", "."));
	}

}

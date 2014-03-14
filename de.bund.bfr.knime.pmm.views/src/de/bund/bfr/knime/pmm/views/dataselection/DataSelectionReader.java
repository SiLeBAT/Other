package de.bund.bfr.knime.pmm.views.dataselection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bund.bfr.knime.pmm.core.Utilities;
import de.bund.bfr.knime.pmm.core.common.Unit;
import de.bund.bfr.knime.pmm.core.data.Condition;
import de.bund.bfr.knime.pmm.core.data.TimeSeries;
import de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.views.chart.Plotable;

public class DataSelectionReader {

	private List<TimeSeries> timeSeries;
	private List<String> allIds;
	private List<String> ids;
	private List<TimeSeries> data;
	private Map<String, List<String>> stringColumns;
	private Map<String, List<Double>> conditionValues;
	private Map<String, List<String>> conditionUnits;
	private Set<String> standardVisibleColumns;
	private Map<String, Plotable> plotables;
	private Map<String, String> shortLegend;
	private Map<String, String> longLegend;

	public DataSelectionReader(PmmPortObject input) {
		timeSeries = input.getData(new ArrayList<TimeSeries>());
		allIds = new ArrayList<String>();
		ids = new ArrayList<String>();
		plotables = new LinkedHashMap<String, Plotable>();
		stringColumns = new LinkedHashMap<String, List<String>>();
		stringColumns.put(Utilities.DATA, new ArrayList<String>());
		stringColumns.put(Utilities.ORGANISM, new ArrayList<String>());
		stringColumns.put(Utilities.ORGANISM_DETAILS, new ArrayList<String>());
		stringColumns.put(Utilities.MATRIX, new ArrayList<String>());
		stringColumns.put(Utilities.MATRIX_DETAILS, new ArrayList<String>());
		conditionValues = new LinkedHashMap<String, List<Double>>();
		conditionUnits = new LinkedHashMap<String, List<String>>();
		data = new ArrayList<TimeSeries>();
		shortLegend = new LinkedHashMap<String, String>();
		longLegend = new LinkedHashMap<String, String>();
		standardVisibleColumns = new LinkedHashSet<String>(Arrays.asList(
				Utilities.DATA, Utilities.ORGANISM, Utilities.MATRIX));

		Set<String> idSet = new LinkedHashSet<String>();
		List<String> allConditions = Utilities.getConditions(timeSeries);

		for (String cond : allConditions) {
			conditionValues.put(cond, new ArrayList<Double>());
			conditionUnits.put(cond, new ArrayList<String>());
		}

		for (TimeSeries series : timeSeries) {
			String id = series.getId() + "";

			allIds.add(id);

			if (!idSet.add(id)) {
				continue;
			}

			ids.add(id);

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

			stringColumns.get(Utilities.DATA).add(series.getName());
			stringColumns.get(Utilities.ORGANISM).add(organism);
			stringColumns.get(Utilities.ORGANISM_DETAILS).add(organismDetails);
			stringColumns.get(Utilities.MATRIX).add(matrix);
			stringColumns.get(Utilities.MATRIX_DETAILS).add(matrixDetails);
			data.add(series);
			shortLegend.put(id, series.getName());
			longLegend.put(id, series.getName() + " " + organism);

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

			Plotable plotable = new Plotable(Plotable.DATASET);
			Map<String, Unit> units = new LinkedHashMap<String, Unit>();

			units.put(Utilities.TIME, series.getTimeUnit());
			units.put(Utilities.CONCENTRATION, series.getConcentrationUnit());
			plotable.setUnits(units);

			if (!timeList.isEmpty() && !logcList.isEmpty()) {
				plotable.addValueList(Utilities.TIME, timeList);
				plotable.addValueList(Utilities.CONCENTRATION, logcList);
			}

			plotables.put(id, plotable);
		}
	}

	public List<TimeSeries> getTimeSeries() {
		return timeSeries;
	}

	public List<String> getAllIds() {
		return allIds;
	}

	public List<String> getIds() {
		return ids;
	}

	public List<TimeSeries> getData() {
		return data;
	}

	public Map<String, List<String>> getStringColumns() {
		return stringColumns;
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

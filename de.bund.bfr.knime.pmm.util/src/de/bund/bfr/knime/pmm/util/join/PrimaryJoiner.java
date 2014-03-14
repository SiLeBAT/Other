/*******************************************************************************
 * PMM-Lab � 2012, Federal Institute for Risk Assessment (BfR), Germany
 * 
 * PMM-Lab is a set of KNIME-Nodes and KNIME workflows running within the KNIME software plattform (http://www.knime.org.).
 * 
 * PMM-Lab � 2012, Federal Institute for Risk Assessment (BfR), Germany
 * Contact: armin.weiser@bfr.bund.de or matthias.filter@bfr.bund.de 
 * 
 * Developers and contributors to the PMM-Lab project are 
 * Joergen Brandt (BfR)
 * Armin A. Weiser (BfR)
 * Matthias Filter (BfR)
 * Alexander Falenski (BfR)
 * Christian Thoens (BfR)
 * Annemarie Kaesbohrer (BfR)
 * Bernd Appel (BfR)
 * 
 * PMM-Lab is a project under development. Contributions are welcome.
 * 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.bund.bfr.knime.pmm.util.join;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.bund.bfr.knime.pmm.core.Utilities;
import de.bund.bfr.knime.pmm.core.XmlUtilities;
import de.bund.bfr.knime.pmm.core.data.TimeSeries;
import de.bund.bfr.knime.pmm.core.models.ModelsFactory;
import de.bund.bfr.knime.pmm.core.models.PrimaryModel;
import de.bund.bfr.knime.pmm.core.models.PrimaryModelFormula;
import de.bund.bfr.knime.pmm.core.models.Variable;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.core.port.PmmPortObjectSpec;

public class PrimaryJoiner implements Joiner {

	private List<PrimaryModelFormula> models;
	private List<TimeSeries> data;

	private Map<String, Map<String, JComboBox<String>>> variableBoxes;

	private Map<String, String> modelNames;
	private Map<String, Map<String, String>> variableCategories;
	private Map<String, Map<String, String>> parameterCategories;

	public PrimaryJoiner(PmmPortObject in1, PmmPortObject in2) {
		this.models = in1.getData(new ArrayList<PrimaryModelFormula>());
		this.data = in2.getData(new ArrayList<TimeSeries>());

		readModelTable();
		readDataTable();
	}

	@Override
	public JComponent createPanel(String assignments) {
		Map<String, Map<String, String>> assignmentsMap = XmlUtilities.fromXml(
				assignments, new LinkedHashMap<String, Map<String, String>>());

		JPanel panel = new JPanel();
		JPanel topPanel = new JPanel();

		panel.setLayout(new BorderLayout());
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		variableBoxes = new LinkedHashMap<String, Map<String, JComboBox<String>>>();

		for (String id : modelNames.keySet()) {
			JPanel modelPanel = new JPanel();
			Map<String, JComboBox<String>> boxes = new LinkedHashMap<String, JComboBox<String>>();

			modelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			modelPanel.setBorder(BorderFactory.createTitledBorder(modelNames
					.get(id)));

			for (String var : variableCategories.get(id).keySet()) {
				String category = variableCategories.get(id).get(var);
				List<String> params = getParamsFromCategory(category);
				JComboBox<String> box = new JComboBox<String>(
						params.toArray(new String[0]));

				if (assignmentsMap.containsKey(id)) {
					box.setSelectedItem(assignmentsMap.get(id).get(var));
				} else if (params.size() == 1) {
					box.setSelectedIndex(0);
				} else {
					box.setSelectedItem(null);
				}

				boxes.put(var, box);
				modelPanel.add(new JLabel(var + ":"));
				modelPanel.add(box);
			}

			variableBoxes.put(id, boxes);
			topPanel.add(modelPanel);
		}

		panel.add(topPanel, BorderLayout.NORTH);

		return panel;
	}

	@Override
	public String getAssignments() {
		Map<String, Map<String, String>> assignmentsMap = new LinkedHashMap<String, Map<String, String>>();

		for (String id : variableBoxes.keySet()) {
			assignmentsMap.put(id, new LinkedHashMap<String, String>());

			for (String param : variableBoxes.get(id).keySet()) {
				String assignment = (String) variableBoxes.get(id).get(param)
						.getSelectedItem();

				assignmentsMap.get(id).put(param, assignment);
			}
		}

		return XmlUtilities.toXml(assignmentsMap);
	}

	@Override
	public PmmPortObject getOutput(String assignments) {
		Map<String, Map<String, String>> assignmentsMap = XmlUtilities.fromXml(
				assignments, new LinkedHashMap<String, Map<String, String>>());
		List<PrimaryModel> result = new ArrayList<PrimaryModel>();

		for (PrimaryModelFormula model : models) {
			for (TimeSeries series : data) {
				PrimaryModel joined = ModelsFactory.eINSTANCE
						.createPrimaryModel();
				boolean convertFail = false;

				for (String var : assignmentsMap.get(model.getId()).keySet()) {
					String param = assignmentsMap.get(model.getId()).get(var);
					String varType = variableCategories.get(model.getId()).get(
							var);
					String paramType = parameterCategories.get(series.getId())
							.get(param);

					if (!varType.equals(paramType)) {
						convertFail = true;
						break;
					}
				}

				if (convertFail) {
					continue;
				}

				joined.setModelFormula(model);
				joined.setData(series);
				joined.getAssignments().putAll(
						assignmentsMap.get(model.getId()));
				result.add(joined);
			}
		}

		return new PmmPortObject(result, PmmPortObjectSpec.PRIMARY_MODEL_TYPE);
	}

	@Override
	public boolean isValid() {
		for (String id : variableBoxes.keySet()) {
			for (String param : variableBoxes.get(id).keySet()) {
				if (variableBoxes.get(id).get(param).getSelectedItem() == null) {
					return false;
				}
			}
		}

		return true;
	}

	private void readModelTable() {
		modelNames = new LinkedHashMap<String, String>();
		variableCategories = new LinkedHashMap<String, Map<String, String>>();

		for (PrimaryModelFormula model : models) {
			String id = model.getId();

			if (modelNames.containsKey(id)) {
				continue;
			}

			modelNames.put(id, model.getName());

			Map<String, String> categories = new LinkedHashMap<String, String>();
			Variable dep = model.getDepVar();
			Variable indep = model.getIndepVar();

			categories.put(dep.getName(), dep.getUnit().getQuantityType()
					.getName());
			categories.put(indep.getName(), indep.getUnit().getQuantityType()
					.getName());

			variableCategories.put(id, categories);
		}
	}

	private void readDataTable() {
		parameterCategories = new LinkedHashMap<String, Map<String, String>>();

		for (TimeSeries series : data) {
			String id = series.getId();

			if (parameterCategories.containsKey(id)) {
				continue;
			}

			Map<String, String> categories = new LinkedHashMap<String, String>();

			categories.put(Utilities.TIME, series.getTimeUnit()
					.getQuantityType().getName());
			categories.put(Utilities.CONCENTRATION, series
					.getConcentrationUnit().getQuantityType().getName());

			parameterCategories.put(id, categories);
		}
	}

	private List<String> getParamsFromCategory(String category) {
		Set<String> params = new LinkedHashSet<String>();

		for (Map<String, String> paramCategories : parameterCategories.values()) {
			for (String param : paramCategories.keySet()) {
				if (paramCategories.get(param).equals(category)) {
					params.add(param);
				}
			}
		}

		return new ArrayList<String>(params);
	}

}

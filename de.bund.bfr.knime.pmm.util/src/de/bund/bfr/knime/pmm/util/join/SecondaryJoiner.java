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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.bund.bfr.knime.pmm.core.XmlUtilities;
import de.bund.bfr.knime.pmm.core.data.Condition;
import de.bund.bfr.knime.pmm.core.models.ModelsFactory;
import de.bund.bfr.knime.pmm.core.models.Parameter;
import de.bund.bfr.knime.pmm.core.models.PrimaryModel;
import de.bund.bfr.knime.pmm.core.models.SecondaryModel;
import de.bund.bfr.knime.pmm.core.models.SecondaryModelFormula;
import de.bund.bfr.knime.pmm.core.models.Variable;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.core.port.PmmPortObjectSpec;

public class SecondaryJoiner implements Joiner, ActionListener {

	private List<SecondaryModelFormula> modelTable;
	private List<PrimaryModel> dataTable;

	private Map<String, String> modelNames;
	private Map<String, String> modelFormulas;
	private Map<String, String> dependentVariables;
	private Map<String, Map<String, String>> independentVariableCategories;
	private Map<String, Map<String, String>> independentVariableUnits;
	private Map<String, List<String>> dependentParameters;
	private Map<String, String> primaryModelNames;
	private Map<String, String> independentParameterCategories;

	private Map<String, JPanel> boxPanels;
	private Map<String, JPanel> buttonPanels;
	private Map<String, List<Map<String, JComboBox<String>>>> comboBoxes;
	private Map<String, List<JButton>> addButtons;
	private Map<String, List<JButton>> removeButtons;

	private boolean isValid;

	public SecondaryJoiner(PmmPortObject in1, PmmPortObject in2) {
		this.modelTable = in1.getData(new ArrayList<SecondaryModelFormula>());
		this.dataTable = in2.getData(new ArrayList<PrimaryModel>());

		readModelTable();
		readDataTable();
	}

	@Override
	public JComponent createPanel(String assignments) {
		Map<String, List<Map<String, String>>> assignmentsMap = XmlUtilities
				.fromXml(assignments,
						new LinkedHashMap<String, List<Map<String, String>>>());
		JPanel panel = new JPanel();
		JPanel topPanel = new JPanel();

		boxPanels = new LinkedHashMap<String, JPanel>();
		buttonPanels = new LinkedHashMap<String, JPanel>();
		comboBoxes = new LinkedHashMap<String, List<Map<String, JComboBox<String>>>>();
		addButtons = new LinkedHashMap<String, List<JButton>>();
		removeButtons = new LinkedHashMap<String, List<JButton>>();
		panel.setLayout(new BorderLayout());
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

		for (String modelID : modelNames.keySet()) {
			List<Map<String, String>> modelAssignments = new ArrayList<Map<String, String>>();
			List<Map<String, JComboBox<String>>> modelBoxes = new ArrayList<Map<String, JComboBox<String>>>();
			List<JButton> modelAddButtons = new ArrayList<JButton>();
			List<JButton> modelRemoveButtons = new ArrayList<JButton>();

			if (assignmentsMap.containsKey(modelID)) {
				modelAssignments = assignmentsMap.get(modelID);
			}

			JPanel modelPanel = new JPanel();
			JPanel leftPanel = new JPanel();
			JPanel rightPanel = new JPanel();

			leftPanel.setLayout(new GridLayout(0, 1));
			rightPanel.setLayout(new GridLayout(0, 1));

			for (Map<String, String> assignment : modelAssignments) {
				Map<String, JComboBox<String>> boxes = new LinkedHashMap<String, JComboBox<String>>();
				JPanel assignmentPanel = new JPanel();

				assignmentPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

				JComboBox<String> depBox = new JComboBox<String>(getDepParams()
						.toArray(new String[0]));

				depBox.setSelectedItem(assignment.get(dependentVariables
						.get(modelID)));
				depBox.addActionListener(this);
				boxes.put(dependentVariables.get(modelID), depBox);
				assignmentPanel.add(new JLabel(dependentVariables.get(modelID)
						+ ":"));
				assignmentPanel.add(depBox);

				for (String indepVar : independentVariableCategories.get(
						modelID).keySet()) {
					JComboBox<String> indepBox = new JComboBox<String>(
							getIndepParamsFromCategory(
									independentVariableCategories.get(modelID)
											.get(indepVar)).toArray(
									new String[0]));

					indepBox.setSelectedItem(assignment.get(indepVar));
					indepBox.addActionListener(this);
					boxes.put(indepVar, indepBox);
					assignmentPanel.add(new JLabel(indepVar + ":"));
					assignmentPanel.add(indepBox);
				}

				modelBoxes.add(boxes);
				leftPanel.add(assignmentPanel);

				JPanel buttonPanel = new JPanel();
				JButton addButton = new JButton("+");
				JButton removeButton = new JButton("-");

				addButton.addActionListener(this);
				removeButton.addActionListener(this);
				modelAddButtons.add(addButton);
				modelRemoveButtons.add(removeButton);
				buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
				buttonPanel.add(removeButton);
				buttonPanel.add(addButton);
				rightPanel.add(buttonPanel);
			}

			JPanel buttonPanel = new JPanel();
			JButton addButton = new JButton("+");

			addButton.addActionListener(this);
			modelAddButtons.add(addButton);
			buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPanel.add(addButton);
			leftPanel.add(new JPanel());
			rightPanel.add(buttonPanel);

			boxPanels.put(modelID, leftPanel);
			buttonPanels.put(modelID, rightPanel);
			comboBoxes.put(modelID, modelBoxes);
			addButtons.put(modelID, modelAddButtons);
			removeButtons.put(modelID, modelRemoveButtons);
			modelPanel.setBorder(BorderFactory.createTitledBorder(modelNames
					.get(modelID)));
			modelPanel.setLayout(new BorderLayout());
			modelPanel.setToolTipText(modelFormulas.get(modelID));
			modelPanel.add(leftPanel, BorderLayout.CENTER);
			modelPanel.add(rightPanel, BorderLayout.EAST);
			topPanel.add(modelPanel);
		}

		panel.add(topPanel, BorderLayout.NORTH);
		checkIfInputIsValid();

		return new JScrollPane(panel);
	}

	@Override
	public String getAssignments() {
		Map<String, List<Map<String, String>>> assignmentsMap = new LinkedHashMap<String, List<Map<String, String>>>();

		for (String modelId : comboBoxes.keySet()) {
			List<Map<String, String>> modelAssignments = new ArrayList<Map<String, String>>();

			for (Map<String, JComboBox<String>> modelBoxes : comboBoxes
					.get(modelId)) {
				Map<String, String> assignment = new LinkedHashMap<String, String>();

				for (String var : modelBoxes.keySet()) {
					JComboBox<String> box = modelBoxes.get(var);

					assignment.put(var, (String) box.getSelectedItem());
				}

				modelAssignments.add(assignment);
			}

			assignmentsMap.put(modelId, modelAssignments);
		}

		return XmlUtilities.toXml(assignmentsMap);
	}

	@Override
	public PmmPortObject getOutput(String assignments) {
		Map<String, List<Map<String, String>>> assignmentsMap = XmlUtilities
				.fromXml(assignments,
						new LinkedHashMap<String, List<Map<String, String>>>());
		List<SecondaryModel> result = new ArrayList<SecondaryModel>();

		for (String modelId : assignmentsMap.keySet()) {
			for (Map<String, String> assign : assignmentsMap.get(modelId)) {
				SecondaryModelFormula model = null;

				for (SecondaryModelFormula m : modelTable) {
					if (m.getId().equals(modelId)) {
						model = m;
						break;
					}
				}

				if (model == null) {
					continue;
				}

				SecondaryModel joined = ModelsFactory.eINSTANCE
						.createSecondaryModel();
				String primModelName = null;

				for (String key : assign.keySet()) {
					String s = assign.get(key);
					int i0 = s.indexOf(" ");
					int i1 = s.indexOf("(");
					int i2 = s.indexOf(")");

					if (i0 != -1 && i1 != -1 && i2 != -1) {
						primModelName = s.substring(i1 + 1, i2);
						assign.put(key, s.substring(0, i0));
						break;
					}
				}

				for (PrimaryModel data : dataTable) {
					if (data.getModelFormula().getName().equals(primModelName)) {
						joined.getData().add(data);
					}
				}

				joined.setModelFormula(model);
				joined.getAssignments().putAll(assign);
				result.add(joined);
			}
		}

		return new PmmPortObject(result, PmmPortObjectSpec.SECONDARY_MODEL_TYPE);
	}

	@Override
	public boolean isValid() {
		return isValid;
	}

	private void readModelTable() {
		modelNames = new LinkedHashMap<String, String>();
		modelFormulas = new LinkedHashMap<String, String>();
		dependentVariables = new LinkedHashMap<String, String>();
		independentVariableCategories = new LinkedHashMap<String, Map<String, String>>();
		independentVariableUnits = new LinkedHashMap<String, Map<String, String>>();

		for (SecondaryModelFormula model : modelTable) {
			if (dependentVariables.containsKey(model.getId())) {
				continue;
			}

			modelNames.put(model.getId(), model.getName());
			modelFormulas.put(model.getId(), model.getFormula());
			dependentVariables.put(model.getId(), model.getDepVar().getName());

			Map<String, String> indepCategories = new LinkedHashMap<String, String>();
			Map<String, String> indepUnits = new LinkedHashMap<String, String>();

			for (Variable indep : model.getIndepVars()) {
				if (indep.getUnit() != null) {
					indepCategories.put(indep.getName(), indep.getUnit()
							.getQuantityType().getName());
					indepUnits.put(indep.getName(), indep.getUnit().getName());
				} else {
					indepCategories.put(indep.getName(), null);
					indepUnits.put(indep.getName(), null);
				}
			}

			independentVariableCategories.put(model.getId(), indepCategories);
			independentVariableUnits.put(model.getId(), indepUnits);
		}
	}

	private void readDataTable() {
		dependentParameters = new LinkedHashMap<String, List<String>>();
		primaryModelNames = new LinkedHashMap<String, String>();
		independentParameterCategories = new LinkedHashMap<String, String>();

		for (PrimaryModel data : dataTable) {
			String modelId = data.getModelFormula().getId();

			if (!primaryModelNames.containsKey(modelId)) {
				List<String> params = new ArrayList<String>();

				for (Parameter param : data.getModelFormula().getParams()) {
					params.add(param.getName());
				}

				primaryModelNames
						.put(modelId, data.getModelFormula().getName());
				dependentParameters.put(modelId, params);
			}

			for (Condition cond : data.getData().getConditions()) {
				independentParameterCategories.put(cond.getParameter()
						.getName(), cond.getUnit().getQuantityType().getName());
			}
		}
	}

	private List<String> getDepParams() {
		List<String> params = new ArrayList<String>();

		for (String modelID : dependentParameters.keySet()) {
			for (String param : dependentParameters.get(modelID)) {
				params.add(param + " (" + primaryModelNames.get(modelID) + ")");
			}
		}

		return params;
	}

	private List<String> getIndepParamsFromCategory(String category) {
		List<String> params = new ArrayList<String>();

		for (String param : independentParameterCategories.keySet()) {
			if (category == null
					|| independentParameterCategories.get(param).equals(
							category)) {
				params.add(param);
			}
		}

		return params;
	}

	private void addOrRemoveButtonPressed(JButton button) {
		for (String modelId : addButtons.keySet()) {
			List<JButton> modelAddButtons = addButtons.get(modelId);
			List<JButton> modelRemoveButtons = removeButtons.get(modelId);
			List<Map<String, JComboBox<String>>> modelBoxes = comboBoxes
					.get(modelId);
			JPanel leftPanel = boxPanels.get(modelId);
			JPanel rightPanel = buttonPanels.get(modelId);

			if (modelAddButtons.contains(button)) {
				int index = modelAddButtons.indexOf(button);
				Map<String, JComboBox<String>> boxes = new LinkedHashMap<String, JComboBox<String>>();
				JPanel assignmentPanel = new JPanel();

				assignmentPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

				JComboBox<String> depBox = new JComboBox<String>(getDepParams()
						.toArray(new String[0]));

				depBox.setSelectedItem(null);
				depBox.addActionListener(this);
				boxes.put(dependentVariables.get(modelId), depBox);
				assignmentPanel.add(new JLabel(dependentVariables.get(modelId)
						+ ":"));
				assignmentPanel.add(depBox);

				for (String indepVar : independentVariableCategories.get(
						modelId).keySet()) {
					JComboBox<String> indepBox = new JComboBox<String>(
							getIndepParamsFromCategory(
									independentVariableCategories.get(modelId)
											.get(indepVar)).toArray(
									new String[0]));

					indepBox.setSelectedItem(null);
					indepBox.addActionListener(this);
					boxes.put(indepVar, indepBox);
					assignmentPanel.add(new JLabel(indepVar + ":"));
					assignmentPanel.add(indepBox);
				}

				modelBoxes.add(index, boxes);
				leftPanel.add(assignmentPanel, index);

				JPanel buttonPanel = new JPanel();
				JButton addButton = new JButton("+");
				JButton removeButton = new JButton("-");

				addButton.addActionListener(this);
				removeButton.addActionListener(this);
				modelAddButtons.add(index, addButton);
				modelRemoveButtons.add(index, removeButton);
				buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
				buttonPanel.add(removeButton);
				buttonPanel.add(addButton);
				rightPanel.add(buttonPanel, index);
				leftPanel.revalidate();
				rightPanel.revalidate();

				break;
			} else if (modelRemoveButtons.contains(button)) {
				int index = modelRemoveButtons.indexOf(button);

				modelAddButtons.remove(index);
				modelRemoveButtons.remove(index);
				modelBoxes.remove(index);
				leftPanel.remove(index);
				rightPanel.remove(index);
				leftPanel.revalidate();
				rightPanel.revalidate();

				break;
			}
		}
	}

	private void checkIfInputIsValid() {
		Map<String, JComboBox<String>> depVarBoxes = new LinkedHashMap<String, JComboBox<String>>();
		isValid = true;

		for (String modelId : comboBoxes.keySet()) {
			String depVar = dependentVariables.get(modelId);

			for (Map<String, JComboBox<String>> boxes : comboBoxes.get(modelId)) {
				JComboBox<String> box = boxes.get(depVar);

				if (box.getSelectedItem() == null) {
					isValid = false;
				} else {
					JComboBox<String> sameValueBox = depVarBoxes.get(box
							.getSelectedItem());

					if (sameValueBox != null) {
						box.setForeground(Color.RED);
						sameValueBox.setForeground(Color.RED);
						isValid = false;
					} else {
						box.setForeground(Color.BLACK);
						depVarBoxes.put((String) box.getSelectedItem(), box);
					}
				}
			}
		}

		for (String modelId : comboBoxes.keySet()) {
			String depVar = dependentVariables.get(modelId);

			for (Map<String, JComboBox<String>> boxes : comboBoxes.get(modelId)) {
				Map<String, JComboBox<String>> indepVarBoxes = new LinkedHashMap<String, JComboBox<String>>();

				for (String var : boxes.keySet()) {
					if (var.equals(depVar)) {
						continue;
					}

					JComboBox<String> box = boxes.get(var);

					if (box.getSelectedItem() == null) {
						isValid = false;
					} else {
						JComboBox<String> sameValueBox = indepVarBoxes.get(box
								.getSelectedItem());

						if (sameValueBox != null) {
							box.setForeground(Color.RED);
							sameValueBox.setForeground(Color.RED);
							isValid = false;
						} else {
							box.setForeground(Color.BLACK);
							indepVarBoxes.put((String) box.getSelectedItem(),
									box);
						}
					}
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComboBox) {
			checkIfInputIsValid();
		} else if (e.getSource() instanceof JButton) {
			addOrRemoveButtonPressed((JButton) e.getSource());
			checkIfInputIsValid();
		}
	}

}

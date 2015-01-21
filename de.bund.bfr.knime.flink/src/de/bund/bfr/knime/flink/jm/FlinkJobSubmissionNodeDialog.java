/*******************************************************************************
 * Copyright (c) 2014 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.bund.bfr.knime.flink.jm;

import java.awt.Component;
import java.awt.Container;
import java.text.Format;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import layout.KnimeLayoutUtilties;
import layout.SpringUtilities;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.FlowVariableModelButton;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.port.PortObjectSpec;

import de.bund.bfr.knime.flink.Parameter;
import de.bund.bfr.knime.flink.port.FlinkProgramObjectSpec;

/**
 * <code>NodeDialog</code> for the "FlinkJobSubmission" Node.
 * Submits a job to Flink.
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Arvid Heise
 */
public class FlinkJobSubmissionNodeDialog extends DefaultNodeSettingsPane {

	private SubmissionSettings submissionSettings = new SubmissionSettings();

	private transient List<Component> parameterComponents = new ArrayList<>();

	private transient Map<Parameter, JTextField> textFields = new IdentityHashMap<>();

	private transient Map<Parameter, FlowVariableModel> flowVariableModels = new IdentityHashMap<>();

	/**
	 * New pane for configuring FlinkJobSubmission node dialog.
	 * This is just a suggestion to demonstrate possible default dialog
	 * components.
	 */
	protected FlinkJobSubmissionNodeDialog() {
		this.addDialogComponent(new DialogComponentNumber(FlinkJobSubmissionNodeModel.createDOPModel(),
			"Degree of parallelism", /* step */1, /* componentwidth */5));

		new KnimeLayoutUtilties().beautify(this);
		
		// add third col
		Container mainPanel = KnimeLayoutUtilties.getMainPanel(this);
		mainPanel.add(new JLabel(""), 2);
		mainPanel.add(new JLabel(""), 5);
	}

	@Override
	public void loadAdditionalSettingsFrom(NodeSettingsRO settings, PortObjectSpec[] specs)
			throws NotConfigurableException {
		if (specs[0] == null)
			throw new NotConfigurableException("Flink not connected.");

		if (specs[1] == null)
			throw new NotConfigurableException("No program connected.");

		super.loadAdditionalSettingsFrom(settings, specs);
		this.submissionSettings.setProgram(((FlinkProgramObjectSpec) specs[1]).getProgram());
		try {
			this.submissionSettings.loadSettingsFrom(settings);
		} catch (InvalidSettingsException e) {
			throw new NotConfigurableException(e.getMessage(), e);
		}

		Container mainPanel = KnimeLayoutUtilties.getMainPanel(this);
		for (Component component : this.parameterComponents)
			mainPanel.remove(component);
		this.parameterComponents.clear();
		
		this.parameterComponents.add(new JLabel("Parameters"));
		this.parameterComponents.add(new JLabel(""));
		this.parameterComponents.add(new JLabel(""));
		for (Parameter parameter : this.submissionSettings.getFlinkProgramWithUsage().getParameters()) {
			this.parameterComponents.add(new JLabel(String.format("%s (%s)", parameter.getName(), parameter.getType()),
				SwingConstants.TRAILING));
			Format format = parameter.getType().getFormat();
			final JTextField textField = format == null ? new JTextField() : new JFormattedTextField(format);
			this.parameterComponents.add(textField);
			textField.setText(this.submissionSettings.getParameterValue(parameter));
			this.textFields.put(parameter, textField);

			FlowVariableModel varModel =
				createFlowVariableModel(new String[] { SubmissionSettings.CFGKEY_PARAMETER, parameter.getName() },
					parameter.getType().getFlowType());
			this.parameterComponents.add(new FlowVariableModelButton(varModel));
			varModel.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(final ChangeEvent e) {
					FlowVariableModel wvm = (FlowVariableModel) (e.getSource());
					textField.setEnabled(!wvm.isVariableReplacementEnabled());
				}
			});
			this.flowVariableModels.put(parameter, varModel);
		}
		this.parameterComponents.add(Box.createVerticalGlue());
		this.parameterComponents.add(Box.createVerticalGlue());
		this.parameterComponents.add(Box.createVerticalGlue());

		for (Component component : this.parameterComponents)
			mainPanel.add(component);
		SpringUtilities.makeCompactGrid(mainPanel, mainPanel.getComponentCount() / 3, 3, 3, 3, 5, 5);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane#saveAdditionalSettingsTo(org.knime.core.node.
	 * NodeSettingsWO)
	 */
	@Override
	public void saveAdditionalSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
		super.saveAdditionalSettingsTo(settings);
		for (Parameter parameter : this.submissionSettings.getFlinkProgramWithUsage().getParameters()) {
			if (this.flowVariableModels.get(parameter).isVariableReplacementEnabled())
				this.submissionSettings.setParameterValue(parameter, "<variable>");
			else
				this.submissionSettings.setParameterValue(parameter, this.textFields.get(parameter).getText());
		}
		this.submissionSettings.validateSettings();
		this.submissionSettings.saveSettingsTo(settings);
	}
}

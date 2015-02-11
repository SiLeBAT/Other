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
package de.bund.bfr.knime.flink.program;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import layout.SpringUtilities;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObjectSpec;

import de.bund.bfr.knime.flink.FlinkProgramWithUsage;
import de.bund.bfr.knime.flink.SerializationHelper;

/**
 * <code>NodeDialog</code> for the "FlinkProgramLoader" Node.
 * Loads an existing Flink program.
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Arvid Heise
 */
public class FlinkProgramLoaderNodeDialog extends NodeDialogPane {

	private FilePanel filePanel;

	private ParameterTable parameterTable;

	/**
	 * New pane for configuring FlinkProgramLoader node dialog.
	 * This is just a suggestion to demonstrate possible default dialog
	 * components.
	 */
	protected FlinkProgramLoaderNodeDialog() {
		JPanel mainPanel = new JPanel(new SpringLayout());
		String[] labels = { "Jar path: ", "Parameters: " };
		this.filePanel = new FilePanel("Choose jar", FilePanel.OPEN_DIALOG);
		this.parameterTable = new ParameterTable();
		JComponent[] components = { this.filePanel, this.parameterTable };
		int numPairs = labels.length;

		// Create and populate the panel.
		for (int i = 0; i < numPairs; i++) {
			JLabel l = new JLabel(labels[i], SwingConstants.TRAILING);
			mainPanel.add(l);
			JComponent component = components[i];
			l.setLabelFor(component);
			mainPanel.add(component);
		}

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(mainPanel,
			labels.length, 2, // rows, cols
			6, 6, // initX, initY
			6, 6); // xPad, yPad

		addTab("Program settings", mainPanel);
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeDialogPane#loadSettingsFrom(org.knime.core.node.NodeSettingsRO,
	 * org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, PortObjectSpec[] specs) throws NotConfigurableException {
		try {
			FlinkProgramWithUsage program = SerializationHelper.readObject(settings, "program");
			this.filePanel.setFileName(program.getJarPath() == null ? "" : program.getJarPath().toString());
			this.parameterTable.setParameters(program.getParameters());
		} catch (Exception e) {
			throw new NotConfigurableException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeDialogPane#saveSettingsTo(org.knime.core.node.NodeSettingsWO)
	 */
	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
		ParameterTableModel parameterModel = (ParameterTableModel) this.parameterTable.getTable().getModel();
		if (!parameterModel.validateValues())
			throw new InvalidSettingsException("The parameter table has errors.");

		File file = new File(this.filePanel.getFileName());
		if (!file.exists())
			throw new InvalidSettingsException("The jar does not exist.");

		FlinkProgramWithUsage program = new FlinkProgramWithUsage();
		program.setParameters(this.parameterTable.getParameters());
		program.setJarPath(file.getAbsolutePath());
		SerializationHelper.writeObject(settings, "program", program);
	}
}

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
package de.bund.bfr.knime.flink.port;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.bund.bfr.knime.flink.FlinkProgramWithUsage;
import de.bund.bfr.knime.flink.JMultilineLabel;
import de.bund.bfr.knime.flink.Parameter;

final class FlinkProgramObjectView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7638638767267140428L;

	FlinkProgramObjectView(final FlinkProgramWithUsage program) {
		super(new BorderLayout());
		super.setName("Flink program");
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		panel.add(new JLabel("<html><body><h2>Flink program</h2></body></html>"));
		panel.add(Box.createVerticalStrut(20));
		panel.add(new JLabel("<html><body><h3>Location</h3></body></html>"));
		panel.add(new JMultilineLabel(program.getJarPath(), 1, 0));
		panel.add(Box.createVerticalStrut(20));
		panel.add(new JLabel("<html><body><h3>Parameters</h3></body></html>"));
		for (Parameter parameter : program.getParameters()) {
			StringBuilder buf = new StringBuilder();
			buf.append(parameter.getType()).append(" ").append(parameter.getName());
			if (parameter.isOptional())
				buf.append("?");
			panel.add(new JLabel(buf.toString()));
		}
		panel.add(Box.createVerticalGlue());
		for (Component child : panel.getComponents())
			((JComponent) child).setAlignmentX(0);
		// final JScrollPane jsp = new JScrollPane(f, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
		// ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		super.add(panel, BorderLayout.CENTER);
	}
}
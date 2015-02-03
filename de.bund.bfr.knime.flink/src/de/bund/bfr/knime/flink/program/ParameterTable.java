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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import org.knime.core.node.util.ConfigTablePanel;

import de.bund.bfr.knime.flink.Parameter;
import de.bund.bfr.knime.flink.Parameter.Type;
import de.bund.bfr.knime.flink.program.ParameterTableModel.Column;

/**
 * The model for the parameter table. Performs validation of the given data. <br/>
 * <br/>
 * This class is heavily inspired by JSnippet plugin from Heiko Hofer.
 */
public class ParameterTable extends ConfigTablePanel {
	/**
	 * Property fired when a row is manually added by the user.
	 */
	public static final String PROP_FIELD_ADDED = "prop_field_added";

	/**
	 * 
	 */
	private static final long serialVersionUID = -601022685311139615L;

	private ParameterTableModel model;

	/**
	 * Create a new component.
	 */
	public ParameterTable() {
		super(new ParameterTableModel());
		final JTable table = this.getTable();

		this.model = (ParameterTableModel) this.getModel();

		DefaultCellEditor textEditor = new DefaultCellEditor(new JTextField());
		textEditor.setClickCountToStart(1);
		table.setDefaultEditor(String.class, textEditor);

		TableColumn nameColumn = table.getColumnModel().getColumn(this.model.getIndex(Column.NAME));
		nameColumn.setCellEditor(textEditor);
		TableColumn typeColumn = table.getColumnModel().getColumn(this.model.getIndex(Column.TYPE));
		typeColumn.setCellEditor(new DefaultCellEditor(new JComboBox<Type>(Type.values())));

		// commit editor on focus lost
		this.getTable().putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	}

	public void addParameter(final Parameter outVar) {
		this.model.addRow(new Object[] { outVar.getName(), outVar.getType(), outVar.isOptional() });
	}

	/**
	 * Adds a row using the give values as a hint.
	 * 
	 * @param name
	 *        the knime name
	 * @param type
	 *        the Type
	 * @return true when the row was added successfully
	 */
	public void addRow(final String name, final Type type) {
		this.addParameter(new Parameter(name, type, false));
	}

	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<>();
		for (int r = 0; r < this.model.getRowCount(); r++) {
			if (!this.model.validateValues(r))
				// there are errors in this row
				continue;
			parameters.add(new Parameter(
				(String) this.model.getValueAt(r, Column.NAME),
				(Type) this.model.getValueAt(r, Column.TYPE),
				(Boolean) this.model.getValueAt(r, Column.OPTIONAL)));
		}
		// sort optional last
		Collections.sort(parameters, new Comparator<Parameter>() {
			@Override
			public int compare(Parameter o1, Parameter o2) {
				return (o1.isOptional() ? 1 : 0) - (o2.isOptional() ? 1 : 0);
			}
		});
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.model.clear();
		for (Parameter parameter : parameters) {
			addParameter(parameter);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ActionListener createAddButtonListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				ParameterTable.this.addRow("param" + (ParameterTable.this.model.getRowCount() + 1), Type.STRING);
			}
		};
	}

	/**
	 * Set the table data.
	 * 
	 * @param fields
	 *        the fields
	 * @param spec
	 *        the input spec
	 * @param flowVars
	 *        the flow variables
	 */
	void updateData(final List<Parameter> vars) {
		this.model.clear();
		for (int r = 0; r < vars.size(); r++)
			this.addParameter(vars.get(r));
	}

}

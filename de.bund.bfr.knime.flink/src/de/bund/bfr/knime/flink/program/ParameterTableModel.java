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

import org.knime.core.node.util.DefaultConfigTableModel;
import org.knime.core.node.workflow.FlowVariable;

import de.bund.bfr.knime.flink.Parameter.Type;

/**
 * The model for the parameter table. Performs validation of the given data. <br/>
 * <br/>
 * This class is heavily inspired by JSnippet plugin from Heiko Hofer.
 */
public class ParameterTableModel extends DefaultConfigTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3989703625477341457L;

	/**
	 * Create a new instance.
	 * 
	 * @param flowVarsOnly
	 *        true when only flow variables and no columns can
	 *        be defined.
	 */
	public ParameterTableModel() {
		super(new String[] { "Name", "Type", "Optional" });
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int column) {
		switch (Column.values()[column]) {
		case OPTIONAL:
			return Boolean.class;

		case TYPE:
			return Type.class;

		default:
			return String.class;
		}
	}

	/**
	 * Get the index of the column.
	 * 
	 * @param column
	 *        the column
	 * @return the index of the column of -1 if the column is not found.
	 */
	public int getIndex(final Column column) {
		return column.ordinal();
	}

	/**
	 * Returns an attribute value for the cell at <code>row</code> and <code>column</code>.
	 * 
	 * @param row
	 *        the row whose value is to be queried
	 * @param column
	 *        the column whose value is to be queried
	 * @return the value Object at the specified cell
	 * @exception ArrayIndexOutOfBoundsException
	 *            if an invalid row or
	 *            column was given
	 */
	public Object getValueAt(final int row, final Column column) {
		return this.getValueAt(row, column.ordinal());
	}

	/**
	 * Sets the object value for the cell at <code>column</code> and <code>row</code>. <code>aValue</code> is the new
	 * value. This method
	 * will generate a <code>tableChanged</code> notification.
	 * 
	 * @param aValue
	 *        the new value; this can be null
	 * @param row
	 *        the row whose value is to be changed
	 * @param column
	 *        the column whose value is to be changed
	 * @exception ArrayIndexOutOfBoundsException
	 *            if an invalid row or
	 *            column was given
	 */
	public void setValueAt(final Object aValue, final int row,
			final Column column) {
		this.setValueAt(aValue, row, column.ordinal());
	}

	/**
	 * Checks whether the all cell values are valid.
	 * 
	 * @return true when all values are valid
	 */
	public boolean validateValues() {
		for (int r = 0; r < this.getRowCount(); r++)
			if (!this.validateValues(r))
				return false;
		return true;
	}

	/**
	 * Checks whether the cell values in the given row are valid.
	 * 
	 * @param row
	 *        the row to check
	 * @return true when all tested values are valid
	 */
	public boolean validateValues(final int row) {
		for (Column c : Column.values())
			if (!this.isValidValue(row, c))
				return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	String getErrorMessage(final int row, final Column column) {
		switch (column) {
		case NAME:
			return this.validateColColumn(row);
		case TYPE:
			return this.validateJavaTypeColumn(row);
		case OPTIONAL:
			return null;
		default:
			throw new IllegalStateException("Unknown column.");
		}
	}

	String getErrorMessage(final int row, final int column) {
		return this.getErrorMessage(row, Column.values()[column]);
	}

	/**
	 * Checks whether to given value is unique in the given column.
	 * 
	 * @param value
	 *        the value to check
	 * @param row
	 *        the row
	 * @param column
	 *        the column
	 * @return true when value is unique with special constraints.
	 */
	boolean isUnique(final Object value, final int row, final Column column) {
		for (int i = 0; i < row; i++)
			if (value.equals(this.getValueAt(i, column)))
				return false;
		for (int i = row + 1; i < this.getRowCount(); i++)
			if (value.equals(this.getValueAt(i, column)))
				return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	boolean isValidValue(final int row, final Column column) {
		return this.getErrorMessage(row, column) == null;
	}

	boolean isValidValue(final int row, final int column) {
		return this.getErrorMessage(row, Column.values()[column]) == null;
	}

	/**
	 * Validate column / flow variable name in the given row.
	 * 
	 * @param row
	 *        the row to check
	 * @return error message if error occurs
	 */
	private String validateColColumn(final int row) {
		String value = (String) this.getValueAt(row, Column.NAME);
		if (null == value || value.isEmpty())
			return "Please select a value";
		if (!verifyNameOfFlowVariable(value))
			return "Flow variable has reserved prefix. Please change the name.";
		// check if data column is unique
		boolean isUnique = this.isUnique(value, row, Column.NAME);
		if (!isUnique)
			return "Duplicated flow variable.";
		// no errors found
		return null;
	}

	/**
	 * Validate java type in the given row.
	 * 
	 * @param row
	 *        the row to check
	 * @return error message if error occurs
	 */
	private String validateJavaTypeColumn(final int row) {
		Object value = this.getValueAt(row, Column.TYPE);
		if (null == value)
			return "Please select a value";

		// no errror found
		return null;
	}

	/**
	 * Test whether the given name is allowed for flow variables. Flow variables
	 * are i.e. not allowed to start with "knime."
	 * 
	 * @param name
	 *        the name the name
	 * @return true when give name is valid
	 */
	static boolean verifyNameOfFlowVariable(final String name) {
		try {
			// test if a flow variable of this name might be
			// created. verifyName throws the package private
			// exception: IllegalFlowObjectStackException
			new FlowVariable(name, "").getScope().verifyName(name);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * The columns of the output table.
	 */
	public enum Column {
		NAME,
		TYPE,
		OPTIONAL
	}

}

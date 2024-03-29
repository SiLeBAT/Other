/*******************************************************************************
 * Copyright (c) 2015 Federal Institute for Risk Assessment (BfR), Germany
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
 *
 * Contributors:
 *     Department Biological Safety - BfR
 *******************************************************************************/
package de.bund.bfr.knime.flink.program;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class StringTextField extends JTextField implements DocumentListener {

	private static final long serialVersionUID = 1L;

	private List<TextListener> listeners;

	private boolean optional;

	private boolean isValueValid;
	private String value;

	public StringTextField(boolean optional, int columns) {
		super(columns);
		this.optional = optional;
		getDocument().addDocumentListener(this);
		listeners = new ArrayList<>();
		textChanged();
	}

	public void addTextListener(TextListener listener) {
		listeners.add(listener);
	}

	public void removeTextListener(TextListener listener) {
		listeners.remove(listener);
	}

	public boolean isValueValid() {
		return isValueValid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		getDocument().removeDocumentListener(this);

		if (value != null) {
			setText(value);
		} else {
			setText("");
		}

		getDocument().addDocumentListener(this);
		textChanged();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		textChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		textChanged();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		textChanged();
	}

	@Override
	public Color getForeground() {
		if (!isValueValid && isEnabled()) {
			return Color.RED;
		}

		return super.getForeground();
	}

	@Override
	public Color getBackground() {
		if (!isValueValid && isEnabled() && getDocument() != null
				&& getText().trim().isEmpty()) {
			return Color.RED;
		}

		return super.getBackground();
	}

	private void textChanged() {
		value = getText().trim();
		value = value.isEmpty() ? null : value;

		if (value == null && !optional) {
			isValueValid = false;
		} else {
			isValueValid = true;
		}

		for (TextListener listener : listeners) {
			listener.textChanged(this);
		}
	}

}

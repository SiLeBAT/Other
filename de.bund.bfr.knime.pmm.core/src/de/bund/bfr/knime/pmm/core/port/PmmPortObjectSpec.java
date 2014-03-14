/*******************************************************************************
 * Copyright (c) 2013 Federal Institute for Risk Assessment (BfR), Germany 
 * 
 * Developers and contributors are 
 * Christian Thoens (BfR)
 * Armin A. Weiser (BfR)
 * Matthias Filter (BfR)
 * Annemarie Kaesbohrer (BfR)
 * Bernd Appel (BfR)
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
package de.bund.bfr.knime.pmm.core.port;

import java.io.Serializable;

import javax.swing.JComponent;

import org.knime.core.node.port.PortObjectSpec;

public class PmmPortObjectSpec implements PortObjectSpec, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String DATA_TYPE = "Data";
	public static final String PRIMARY_MODEL_FORMULA_TYPE = "PrimaryModelFormula";
	public static final String SECONDARY_MODEL_FORMULA_TYPE = "SecondaryModelFormula";
	public static final String TERTIARY_MODEL_FORMULA_TYPE = "TertiaryModelFormula";
	public static final String PRIMARY_MODEL_TYPE = "PrimaryModel";
	public static final String SECONDARY_MODEL_TYPE = "SecondaryModel";
	public static final String TERTIARY_MODEL_TYPE = "TertiaryModel";

	private String type;

	public PmmPortObjectSpec(String type) {
		this.type = type;
	}

	@Override
	public JComponent[] getViews() {
		return new JComponent[] { new PmmPortObjectSpecView(this) };
	}

	public String getType() {
		return type;
	}

	public static PortObjectSpecSerializer<PmmPortObjectSpec> getPortObjectSpecSerializer() {
		return new PmmPortObjectSpecSerializer();
	}

}

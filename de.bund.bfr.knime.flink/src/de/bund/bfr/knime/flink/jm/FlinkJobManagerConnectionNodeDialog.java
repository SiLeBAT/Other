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

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;

/**
 * <code>NodeDialog</code> for the "FlinkJobManagerConnection" Node.
 * Connects to the Flink job manager.
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Arvid Heise
 */
public class FlinkJobManagerConnectionNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring FlinkJobManagerConnection node dialog.
	 * This is just a suggestion to demonstrate possible default dialog
	 * components.
	 */
	protected FlinkJobManagerConnectionNodeDialog() {
		this.addDialogComponent(new DialogComponentString(FlinkJobManagerConnectionNodeModel.createAddressModel(),
			"Address:", true, /* componentwidth */5));
		this.addDialogComponent(new DialogComponentNumber(FlinkJobManagerConnectionNodeModel.createPortModel(),
			"Port:", /* step */2, /* componentwidth */5));

	}
}

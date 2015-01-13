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
package de.bund.bfr.knime.hdfs.connection;

import layout.KnimeLayoutUtilties;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumberEdit;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;

/**
 * <code>NodeDialog</code> for the "HDFSConnection" Node.
 * Connects to a local or remote HDFS server.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Arvid Heise
 */
public class HDFSConnectionNodeDialog extends DefaultNodeSettingsPane {
    /**
     * New pane for configuring the HDFSConnection node.
     */
    protected HDFSConnectionNodeDialog() {
		this.addDialogComponent(new DialogComponentStringSelection(HDFSConnectionNodeModel.createProtocolModel(),
				"Protocol:", "webhdfs", "hdfs"));
		this.addDialogComponent(new DialogComponentString(HDFSConnectionNodeModel.createAddressModel(),
			"Address:", true, 30));
		this.addDialogComponent(new DialogComponentNumberEdit(HDFSConnectionNodeModel.createPortModel(),
			"Port:"));
		
		new KnimeLayoutUtilties().beautify(this);
    }
}


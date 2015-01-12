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
package de.bund.bfr.knime.hdfs.file;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentString;

/**
 * <code>NodeDialog</code> for the "HDFSUpload" Node.
 * Transfers a file to HDFS.
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Arvid Heise
 */
public class HDFSUploadNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the HDFSUpload node.
	 */
	protected HDFSUploadNodeDialog() {
		this.addDialogComponent(new DialogComponentFileChooser(HDFSUploadNodeModel.createSourceModel(),
			HDFSUploadNodeDialog.class.getName(), JFileChooser.OPEN_DIALOG, false));
		this.addDialogComponent(new DialogComponentString(HDFSDownloadNodeModel.createTargetModel(), "Remote path"));
		this.addDialogComponent(new DialogComponentBoolean(HDFSUploadNodeModel.createOverrideModel(), "Override: "));
	}
}

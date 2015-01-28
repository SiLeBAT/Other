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

import layout.KnimeLayoutUtilties;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.FlowVariable.Type;

/**
 * <code>NodeDialog</code> for the "HDFSDownload" Node.
 * Downloads an HDFS file from a remote HDFS namenode to the local filesystem.
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Arvid Heise
 */
public class HDFSDownloadNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the HDFSUpload node.
	 */
	protected HDFSDownloadNodeDialog() {
		SettingsModelString sourceModel = HDFSDownloadNodeModel.createSourceModel();
		FlowVariableModel sourceFlowVariableModel = createFlowVariableModel(sourceModel.getKey(), Type.STRING);
		this.addDialogComponent(new DialogComponentString(sourceModel, "Source path", false, 30,
			sourceFlowVariableModel));
		SettingsModelString targetModel = HDFSDownloadNodeModel.createTargetModel();
		FlowVariableModel targetFlowVariableModel = createFlowVariableModel(targetModel.getKey(), Type.STRING);
		final DialogComponentFileChooser component = new DialogComponentFileChooser(targetModel,
			HDFSUploadNodeDialog.class.getName(), JFileChooser.SAVE_DIALOG, false, targetFlowVariableModel);
		component.setBorderTitle("Target path");
		this.addDialogComponent(component);
		this.addDialogComponent(new DialogComponentString(HDFSDownloadNodeModel.createTargetVariableModel(false),
			"Target variable"));
		this.addDialogComponent(new DialogComponentBoolean(HDFSDownloadNodeModel.createOverrideModel(),
			"Override target?"));

		new KnimeLayoutUtilties().beautify(this);
	}
}

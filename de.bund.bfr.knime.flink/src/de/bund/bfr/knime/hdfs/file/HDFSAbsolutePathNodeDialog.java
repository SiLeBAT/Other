package de.bund.bfr.knime.hdfs.file;

import layout.KnimeLayoutUtilties;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.FlowVariable.Type;

/**
 * <code>NodeDialog</code> for the "HDFSAbsolutePath" Node.
 * Transforms a relative HDFS path to an absolute path with an HDFS connection.
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Arvid Heise
 */
public class HDFSAbsolutePathNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the HDFSAbsolutePath node.
	 */
	protected HDFSAbsolutePathNodeDialog() {
		SettingsModelString relativePathModel = HDFSAbsolutePathNodeModel.createRelativePathModel();
		FlowVariableModel flowVariableModel = createFlowVariableModel(relativePathModel.getKey(), Type.STRING);
		this.addDialogComponent(new DialogComponentString(relativePathModel, "Relative path", false, 30,
			flowVariableModel));
		this.addDialogComponent(new DialogComponentString(HDFSAbsolutePathNodeModel.createVariableModel(false),
			"Target variable"));

		new KnimeLayoutUtilties().beautify(this);
	}
}

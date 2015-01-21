package de.bund.bfr.knime.hdfs.file;

import layout.KnimeLayoutUtilties;

import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.FlowVariable.Type;

/**
 * <code>NodeDialog</code> for the "HDFSFileDelete" Node.
 * Deletes a file or directory in HDFS.
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Arvid Heise
 */
public class HDFSFileDeleteNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the HDFSFileDelete node.
	 */
	protected HDFSFileDeleteNodeDialog() {
		SettingsModelString model = HDFSFileDeleteNodeModel.createPathModel();
		FlowVariableModel flowVariableModel = createFlowVariableModel(model.getKey(), Type.STRING);
		this.addDialogComponent(new DialogComponentString(model, "Path", true, 0, flowVariableModel));

		new KnimeLayoutUtilties().beautify(this);
	}
}

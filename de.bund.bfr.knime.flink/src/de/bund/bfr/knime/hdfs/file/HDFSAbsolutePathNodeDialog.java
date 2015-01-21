package de.bund.bfr.knime.hdfs.file;

import layout.KnimeLayoutUtilties;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;

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
		this.addDialogComponent(new DialogComponentString(HDFSAbsolutePathNodeModel.createRelativePathModel(),
			"Relative path"));
		this.addDialogComponent(new DialogComponentString(HDFSAbsolutePathNodeModel.createVariableModel(false),
			"Target variable"));
		
		new KnimeLayoutUtilties().beautify(this);
	}
}

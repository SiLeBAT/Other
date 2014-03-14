package de.bund.bfr.knime.pmm.io.modelwriter;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

/**
 * <code>NodeDialog</code> for the "ModelWriter" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christian Thoens
 */
public class ModelWriterNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the ModelWriter node.
	 */
	protected ModelWriterNodeDialog() {
	}
}

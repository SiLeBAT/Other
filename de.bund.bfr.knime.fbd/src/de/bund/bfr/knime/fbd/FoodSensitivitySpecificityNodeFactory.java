package de.bund.bfr.knime.fbd;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "FoodSensitivitySpecificity" Node.
 * 
 * 
 * @author
 */
public class FoodSensitivitySpecificityNodeFactory extends
		NodeFactory<FoodSensitivitySpecificityNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FoodSensitivitySpecificityNodeModel createNodeModel() {
		return new FoodSensitivitySpecificityNodeModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNrNodeViews() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<FoodSensitivitySpecificityNodeModel> createNodeView(
			final int viewIndex,
			final FoodSensitivitySpecificityNodeModel nodeModel) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasDialog() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeDialogPane createNodeDialogPane() {
		return new FoodSensitivitySpecificityNodeDialog();
	}

}

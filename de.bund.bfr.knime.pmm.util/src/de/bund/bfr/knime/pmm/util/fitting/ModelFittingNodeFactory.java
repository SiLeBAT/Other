package de.bund.bfr.knime.pmm.util.fitting;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "ModelFitting" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class ModelFittingNodeFactory extends NodeFactory<ModelFittingNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelFittingNodeModel createNodeModel() {
		return new ModelFittingNodeModel();
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
	public NodeView<ModelFittingNodeModel> createNodeView(final int viewIndex,
			final ModelFittingNodeModel nodeModel) {
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
		return new ModelFittingNodeDialog();
	}

}

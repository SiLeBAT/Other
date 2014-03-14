package de.bund.bfr.knime.pmm.io.modelwriter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "ModelWriter" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class ModelWriterNodeFactory extends NodeFactory<ModelWriterNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelWriterNodeModel createNodeModel() {
		return new ModelWriterNodeModel();
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
	public NodeView<ModelWriterNodeModel> createNodeView(final int viewIndex,
			final ModelWriterNodeModel nodeModel) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasDialog() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeDialogPane createNodeDialogPane() {
		return new ModelWriterNodeDialog();
	}

}

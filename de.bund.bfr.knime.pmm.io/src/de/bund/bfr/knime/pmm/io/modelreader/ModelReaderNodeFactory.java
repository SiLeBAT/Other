package de.bund.bfr.knime.pmm.io.modelreader;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "ModelReader" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class ModelReaderNodeFactory extends NodeFactory<ModelReaderNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelReaderNodeModel createNodeModel() {
		return new ModelReaderNodeModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNrNodeViews() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<ModelReaderNodeModel> createNodeView(final int viewIndex,
			final ModelReaderNodeModel nodeModel) {
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
		return new ModelReaderNodeDialog();
	}

}

package de.bund.bfr.knime.pmm.views.tertiarymodelview;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "TertiaryModelView" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class TertiaryModelViewNodeFactory extends
		NodeFactory<TertiaryModelViewNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TertiaryModelViewNodeModel createNodeModel() {
		return new TertiaryModelViewNodeModel();
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
	public NodeView<TertiaryModelViewNodeModel> createNodeView(
			final int viewIndex, final TertiaryModelViewNodeModel nodeModel) {
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
		return new TertiaryModelViewNodeDialog();
	}

}

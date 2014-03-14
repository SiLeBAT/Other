package de.bund.bfr.knime.pmm.views.secondarymodelview;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "SecondaryModelView" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class SecondaryModelViewNodeFactory extends
		NodeFactory<SecondaryModelViewNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SecondaryModelViewNodeModel createNodeModel() {
		return new SecondaryModelViewNodeModel();
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
	public NodeView<SecondaryModelViewNodeModel> createNodeView(
			final int viewIndex, final SecondaryModelViewNodeModel nodeModel) {
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
		return new SecondaryModelViewNodeDialog();
	}

}

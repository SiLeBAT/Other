package de.bund.bfr.knime.pmm.views.primarymodelselection;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "PrimaryModelSelection" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class PrimaryModelSelectionNodeFactory extends
		NodeFactory<PrimaryModelSelectionNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimaryModelSelectionNodeModel createNodeModel() {
		return new PrimaryModelSelectionNodeModel();
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
	public NodeView<PrimaryModelSelectionNodeModel> createNodeView(
			final int viewIndex, final PrimaryModelSelectionNodeModel nodeModel) {
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
		return new PrimaryModelSelectionNodeDialog();
	}

}

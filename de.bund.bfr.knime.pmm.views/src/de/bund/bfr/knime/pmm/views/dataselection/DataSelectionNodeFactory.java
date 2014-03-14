package de.bund.bfr.knime.pmm.views.dataselection;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "DataSelection" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class DataSelectionNodeFactory extends
		NodeFactory<DataSelectionNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSelectionNodeModel createNodeModel() {
		return new DataSelectionNodeModel();
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
	public NodeView<DataSelectionNodeModel> createNodeView(final int viewIndex,
			final DataSelectionNodeModel nodeModel) {
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
		return new DataSelectionNodeDialog();
	}

}

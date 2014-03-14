package de.bund.bfr.knime.pmm.util.join;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "PmmJoiner" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class JoinerNodeFactory extends NodeFactory<JoinerNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JoinerNodeModel createNodeModel() {
		return new JoinerNodeModel();
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
	public NodeView<JoinerNodeModel> createNodeView(final int viewIndex,
			final JoinerNodeModel nodeModel) {
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
		return new JoinerNodeDialog();
	}

}

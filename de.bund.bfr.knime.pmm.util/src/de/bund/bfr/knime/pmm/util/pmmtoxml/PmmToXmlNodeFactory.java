package de.bund.bfr.knime.pmm.util.pmmtoxml;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "PmmToXml" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class PmmToXmlNodeFactory extends NodeFactory<PmmToXmlNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PmmToXmlNodeModel createNodeModel() {
		return new PmmToXmlNodeModel();
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
	public NodeView<PmmToXmlNodeModel> createNodeView(final int viewIndex,
			final PmmToXmlNodeModel nodeModel) {
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
		return new PmmToXmlNodeDialog();
	}

}

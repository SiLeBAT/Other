package de.bund.bfr.knime.pmm.io.xls.datareader;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "XlsDataReader" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class XlsDataReaderNodeFactory extends
		NodeFactory<XlsDataReaderNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public XlsDataReaderNodeModel createNodeModel() {
		return new XlsDataReaderNodeModel();
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
	public NodeView<XlsDataReaderNodeModel> createNodeView(final int viewIndex,
			final XlsDataReaderNodeModel nodeModel) {
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
		return new XlsDataReaderNodeDialog();
	}

}

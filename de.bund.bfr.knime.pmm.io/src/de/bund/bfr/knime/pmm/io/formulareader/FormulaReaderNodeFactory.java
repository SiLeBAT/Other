package de.bund.bfr.knime.pmm.io.formulareader;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "ModelReader" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class FormulaReaderNodeFactory extends
		NodeFactory<FormulaReaderNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormulaReaderNodeModel createNodeModel() {
		return new FormulaReaderNodeModel();
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
	public NodeView<FormulaReaderNodeModel> createNodeView(final int viewIndex,
			final FormulaReaderNodeModel nodeModel) {
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
		return new FormulaReaderNodeDialog();
	}

}

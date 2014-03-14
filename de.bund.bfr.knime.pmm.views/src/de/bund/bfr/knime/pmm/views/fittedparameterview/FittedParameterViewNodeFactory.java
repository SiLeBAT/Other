package de.bund.bfr.knime.pmm.views.fittedparameterview;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "FittedParameterView" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class FittedParameterViewNodeFactory extends
		NodeFactory<FittedParameterViewNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FittedParameterViewNodeModel createNodeModel() {
		return new FittedParameterViewNodeModel();
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
	public NodeView<FittedParameterViewNodeModel> createNodeView(
			final int viewIndex, final FittedParameterViewNodeModel nodeModel) {
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
		return new FittedParameterViewNodeDialog();
	}

}

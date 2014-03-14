package de.bund.bfr.knime.pmm.views.predictorview;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "PredictorView" Node.
 * 
 * 
 * @author Christian Thoens
 */
public class PredictorViewNodeFactory extends
		NodeFactory<PredictorViewNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PredictorViewNodeModel createNodeModel() {
		return new PredictorViewNodeModel();
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
	public NodeView<PredictorViewNodeModel> createNodeView(final int viewIndex,
			final PredictorViewNodeModel nodeModel) {
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
		return new PredictorViewNodeDialog();
	}

}

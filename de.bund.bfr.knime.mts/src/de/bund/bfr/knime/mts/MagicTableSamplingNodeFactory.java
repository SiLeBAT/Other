package de.bund.bfr.knime.mts;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MagicTableSampling" Node.
 * This node creates a sample from a given distribution. A.J. Walkers method is used.
 *
 * @author Markus Freitag
 */
public class MagicTableSamplingNodeFactory 
        extends NodeFactory<MagicTableSamplingNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MagicTableSamplingNodeModel createNodeModel() {
        return new MagicTableSamplingNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<MagicTableSamplingNodeModel> createNodeView(final int viewIndex,
            final MagicTableSamplingNodeModel nodeModel) {
        return new MagicTableSamplingNodeView(nodeModel);
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
        return new MagicTableSamplingNodeDialog();
    }

}


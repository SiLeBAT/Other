package de.bund.bfr.knime.oasq;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "OASQ" Node.
 * Status quo implementation of LBM and SPC
 *
 * @author Markus Freitag
 */
public class OASQNodeFactory 
        extends NodeFactory<OASQNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public OASQNodeModel createNodeModel() {
        return new OASQNodeModel();
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
    public NodeView<OASQNodeModel> createNodeView(final int viewIndex,
            final OASQNodeModel nodeModel) {
        return new OASQNodeView(nodeModel);
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
        return new OASQNodeDialog();
    }

}


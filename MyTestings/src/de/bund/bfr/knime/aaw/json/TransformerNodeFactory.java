package de.bund.bfr.knime.aaw.json;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "Transformer" Node.
 * 
 *
 * @author 
 */
public class TransformerNodeFactory 
        extends NodeFactory<TransformerNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public TransformerNodeModel createNodeModel() {
        return new TransformerNodeModel();
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
    public NodeView<TransformerNodeModel> createNodeView(final int viewIndex,
            final TransformerNodeModel nodeModel) {
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
        return null;
    }

}


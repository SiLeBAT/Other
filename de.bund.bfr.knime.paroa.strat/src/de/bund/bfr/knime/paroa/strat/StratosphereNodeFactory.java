package de.bund.bfr.knime.paroa.strat;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "Stratosphere" Node.
 * 
 *
 * @author Markus Freitag
 */
public class StratosphereNodeFactory 
        extends NodeFactory<StratosphereNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public StratosphereNodeModel createNodeModel() {
        return new StratosphereNodeModel();
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
    public NodeView<StratosphereNodeModel> createNodeView(final int viewIndex,
            final StratosphereNodeModel nodeModel) {
        return new StratosphereNodeView(nodeModel);
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
        return new StratosphereNodeDialog();
    }

}


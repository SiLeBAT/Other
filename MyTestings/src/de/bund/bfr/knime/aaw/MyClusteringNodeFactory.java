package de.bund.bfr.knime.aaw;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MyClustering" Node.
 * 
 *
 * @author aaw
 */
public class MyClusteringNodeFactory 
        extends NodeFactory<MyClusteringNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MyClusteringNodeModel createNodeModel() {
        return new MyClusteringNodeModel();
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
    public NodeView<MyClusteringNodeModel> createNodeView(final int viewIndex,
            final MyClusteringNodeModel nodeModel) {
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
        return new MyClusteringNodeDialog();
    }

}


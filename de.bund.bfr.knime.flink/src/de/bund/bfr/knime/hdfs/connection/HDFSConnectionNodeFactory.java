package de.bund.bfr.knime.hdfs.connection;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HDFSConnection" Node.
 * Connects to a local or remote HDFS server.
 *
 * @author Arvid Heise
 */
public class HDFSConnectionNodeFactory 
        extends NodeFactory<HDFSConnectionNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public HDFSConnectionNodeModel createNodeModel() {
        return new HDFSConnectionNodeModel();
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
    public NodeView<HDFSConnectionNodeModel> createNodeView(final int viewIndex,
            final HDFSConnectionNodeModel nodeModel) {
        return new HDFSConnectionNodeView(nodeModel);
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
        return new HDFSConnectionNodeDialog();
    }

}


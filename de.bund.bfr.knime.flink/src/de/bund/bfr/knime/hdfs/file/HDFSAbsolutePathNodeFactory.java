package de.bund.bfr.knime.hdfs.file;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HDFSAbsolutePath" Node.
 * Transforms a relative HDFS path to an absolute path with an HDFS connection.
 *
 * @author Arvid Heise
 */
public class HDFSAbsolutePathNodeFactory 
        extends NodeFactory<HDFSAbsolutePathNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public HDFSAbsolutePathNodeModel createNodeModel() {
        return new HDFSAbsolutePathNodeModel();
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
    public NodeView<HDFSAbsolutePathNodeModel> createNodeView(final int viewIndex,
            final HDFSAbsolutePathNodeModel nodeModel) {
        return new HDFSAbsolutePathNodeView(nodeModel);
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
        return new HDFSAbsolutePathNodeDialog();
    }

}


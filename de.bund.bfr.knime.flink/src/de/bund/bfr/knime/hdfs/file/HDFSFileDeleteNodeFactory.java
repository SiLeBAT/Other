package de.bund.bfr.knime.hdfs.file;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HDFSFileDelete" Node.
 * Deletes a file or directory in HDFS.
 *
 * @author Arvid Heise
 */
public class HDFSFileDeleteNodeFactory 
        extends NodeFactory<HDFSFileDeleteNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public HDFSFileDeleteNodeModel createNodeModel() {
        return new HDFSFileDeleteNodeModel();
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
    public NodeView<HDFSFileDeleteNodeModel> createNodeView(final int viewIndex,
            final HDFSFileDeleteNodeModel nodeModel) {
        return new HDFSFileDeleteNodeView(nodeModel);
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
        return new HDFSFileDeleteNodeDialog();
    }

}


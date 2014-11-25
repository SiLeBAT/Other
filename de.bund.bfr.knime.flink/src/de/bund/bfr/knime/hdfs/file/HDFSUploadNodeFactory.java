package de.bund.bfr.knime.hdfs.file;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HDFSUpload" Node.
 * Transfers a file to HDFS.
 *
 * @author Arvid Heise
 */
public class HDFSUploadNodeFactory 
        extends NodeFactory<HDFSUploadNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public HDFSUploadNodeModel createNodeModel() {
        return new HDFSUploadNodeModel();
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
    public NodeView<HDFSUploadNodeModel> createNodeView(final int viewIndex,
            final HDFSUploadNodeModel nodeModel) {
        return new HDFSUploadNodeView(nodeModel);
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
        return new HDFSUploadNodeDialog();
    }

}


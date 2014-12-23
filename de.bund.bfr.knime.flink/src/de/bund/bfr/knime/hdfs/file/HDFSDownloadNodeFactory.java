package de.bund.bfr.knime.hdfs.file;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HDFSDownload" Node.
 * Downloads an HDFS file from a remote HDFS namenode to the local filesystem.
 *
 * @author Arvid Heise
 */
public class HDFSDownloadNodeFactory 
        extends NodeFactory<HDFSDownloadNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public HDFSDownloadNodeModel createNodeModel() {
        return new HDFSDownloadNodeModel();
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
    public NodeView<HDFSDownloadNodeModel> createNodeView(final int viewIndex,
            final HDFSDownloadNodeModel nodeModel) {
        return new HDFSDownloadNodeView(nodeModel);
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
        return new HDFSDownloadNodeDialog();
    }

}


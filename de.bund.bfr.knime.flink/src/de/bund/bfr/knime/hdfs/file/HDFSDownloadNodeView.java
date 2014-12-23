package de.bund.bfr.knime.hdfs.file;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "HDFSDownload" Node.
 * Downloads an HDFS file from a remote HDFS namenode to the local filesystem.
 *
 * @author Arvid Heise
 */
public class HDFSDownloadNodeView extends NodeView<HDFSDownloadNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link HDFSDownloadNodeModel})
     */
    protected HDFSDownloadNodeView(final HDFSDownloadNodeModel nodeModel) {
        super(nodeModel);
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {
        // TODO: generated method stub
    }

}


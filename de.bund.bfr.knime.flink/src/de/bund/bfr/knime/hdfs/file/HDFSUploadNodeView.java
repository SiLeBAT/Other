package de.bund.bfr.knime.hdfs.file;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "HDFSUpload" Node.
 * Transfers a file to HDFS.
 *
 * @author Arvid Heise
 */
public class HDFSUploadNodeView extends NodeView<HDFSUploadNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link HDFSUploadNodeModel})
     */
    protected HDFSUploadNodeView(final HDFSUploadNodeModel nodeModel) {
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


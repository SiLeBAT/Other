package de.bund.bfr.knime.hdfs.connection;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "HDFSConnection" Node.
 * Connects to a local or remote HDFS server.
 *
 * @author Arvid Heise
 */
public class HDFSConnectionNodeView extends NodeView<HDFSConnectionNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link HDFSConnectionNodeModel})
     */
    protected HDFSConnectionNodeView(final HDFSConnectionNodeModel nodeModel) {
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


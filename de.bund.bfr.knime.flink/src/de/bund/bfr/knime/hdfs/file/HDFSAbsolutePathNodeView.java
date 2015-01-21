package de.bund.bfr.knime.hdfs.file;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "HDFSAbsolutePath" Node.
 * Transforms a relative HDFS path to an absolute path with an HDFS connection.
 *
 * @author Arvid Heise
 */
public class HDFSAbsolutePathNodeView extends NodeView<HDFSAbsolutePathNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link HDFSAbsolutePathNodeModel})
     */
    protected HDFSAbsolutePathNodeView(final HDFSAbsolutePathNodeModel nodeModel) {
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


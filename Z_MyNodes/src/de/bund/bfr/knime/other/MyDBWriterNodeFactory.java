package de.bund.bfr.knime.other;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MyDBWriter" Node.
 * 
 *
 * @author 
 */
public class MyDBWriterNodeFactory 
        extends NodeFactory<MyDBWriterNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MyDBWriterNodeModel createNodeModel() {
        return new MyDBWriterNodeModel();
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
    public NodeView<MyDBWriterNodeModel> createNodeView(final int viewIndex,
            final MyDBWriterNodeModel nodeModel) {
        return new MyDBWriterNodeView(nodeModel);
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
        return new MyDBWriterNodeDialog();
    }

}


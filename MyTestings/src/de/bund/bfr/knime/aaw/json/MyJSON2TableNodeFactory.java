package de.bund.bfr.knime.aaw.json;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MyJSON2Table" Node.
 * 
 *
 * @author BfR
 */
public class MyJSON2TableNodeFactory 
        extends NodeFactory<MyJSON2TableNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MyJSON2TableNodeModel createNodeModel() {
        return new MyJSON2TableNodeModel();
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
    public NodeView<MyJSON2TableNodeModel> createNodeView(final int viewIndex,
            final MyJSON2TableNodeModel nodeModel) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return null;
    }

}


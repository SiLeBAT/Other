package de.bund.bfr.knime.aaw.lims;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MyTab21" Node.
 * 
 *
 * @author aaw
 */
public class MyTab21NodeFactory 
        extends NodeFactory<MyTab21NodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MyTab21NodeModel createNodeModel() {
        return new MyTab21NodeModel();
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
    public NodeView<MyTab21NodeModel> createNodeView(final int viewIndex,
            final MyTab21NodeModel nodeModel) {
        return null;
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
        return new MyTab21NodeDialog();
    }

}


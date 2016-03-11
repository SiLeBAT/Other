package de.bund.bfr.knime.aaw.lims;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MyJavaJoiner" Node.
 * 
 *
 * @author BfR
 */
public class MyJavaMatcherNodeFactory 
        extends NodeFactory<MyJavaMatcherNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MyJavaMatcherNodeModel createNodeModel() {
        return new MyJavaMatcherNodeModel();
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
    public NodeView<MyJavaMatcherNodeModel> createNodeView(final int viewIndex,
            final MyJavaMatcherNodeModel nodeModel) {
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
        return new MyJavaMatcherNodeDialog();
    }

}


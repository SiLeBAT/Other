package de.bund.bfr.knime.wf;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "ExportWorkflow" Node.
 * 
 *
 * @author BfR
 */
public class ExportWorkflowNodeFactory 
        extends NodeFactory<ExportWorkflowNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ExportWorkflowNodeModel createNodeModel() {
        return new ExportWorkflowNodeModel();
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
    public NodeView<ExportWorkflowNodeModel> createNodeView(final int viewIndex,
            final ExportWorkflowNodeModel nodeModel) {
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
        return new ExportWorkflowNodeDialog();
    }

}


package de.bund.bfr.knime.aaw.lims;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MyLIMSZooImporter" Node.
 * 
 *
 * @author BfR
 */
public class MyLIMSZooImporterNodeFactory 
        extends NodeFactory<MyLIMSZooImporterNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MyLIMSZooImporterNodeModel createNodeModel() {
        return new MyLIMSZooImporterNodeModel();
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
    public NodeView<MyLIMSZooImporterNodeModel> createNodeView(final int viewIndex,
            final MyLIMSZooImporterNodeModel nodeModel) {
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
        return new MyLIMSZooImporterNodeDialog();
    }

}


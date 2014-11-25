package de.bund.bfr.knime.aaw.hartung;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MyImporter" Node.
 * 
 *
 * @author Armin Weiser
 */
public class MyImporterNodeFactory 
        extends NodeFactory<MyImporterNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MyImporterNodeModel createNodeModel() {
        return new MyImporterNodeModel();
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
    public NodeView<MyImporterNodeModel> createNodeView(final int viewIndex,
            final MyImporterNodeModel nodeModel) {
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
        return new MyImporterNodeDialog();
    }

}


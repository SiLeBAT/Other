package de.bund.bfr.knime.Xsd2Xml;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "Xsd2Xml" Node.
 * 
 *
 * @author 
 */
public class Xsd2XmlNodeFactory 
        extends NodeFactory<Xsd2XmlNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Xsd2XmlNodeModel createNodeModel() {
        return new Xsd2XmlNodeModel();
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
    public NodeView<Xsd2XmlNodeModel> createNodeView(final int viewIndex,
            final Xsd2XmlNodeModel nodeModel) {
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
        return new Xsd2XmlNodeDialog();
    }

}


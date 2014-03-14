package de.bund.bfr.knime.aaw;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

/**
 * <code>NodeDialog</code> for the "MyClustering" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author aaw
 */
public class MyClusteringNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring the MyClustering node.
     */
    protected MyClusteringNodeDialog() {

    }
}


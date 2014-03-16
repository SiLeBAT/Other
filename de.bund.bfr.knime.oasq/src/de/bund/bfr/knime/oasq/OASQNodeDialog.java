package de.bund.bfr.knime.oasq;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "OASQ" Node.
 * Status quo implementation of LBM and SPC
 * 
 * @author Markus Freitag
 */
public class OASQNodeDialog extends DefaultNodeSettingsPane {

	static final String CFGKEY_METHOD = "Method";
	static final String LIKELYHOOD_METHOD = "Likelyhood Method";
	static final String SPEARMAN_METHOD = "Spearman Method";
	static final String[] METHODS = {LIKELYHOOD_METHOD, SPEARMAN_METHOD };
    /**
     * New pane for configuring OASQ node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected OASQNodeDialog() {
        super();

		DialogComponentStringSelection methodComp = new DialogComponentStringSelection(
				new SettingsModelString(
						CFGKEY_METHOD,
						LIKELYHOOD_METHOD),
						"Method:", METHODS
				);		
		addDialogComponent(methodComp);
    }
}


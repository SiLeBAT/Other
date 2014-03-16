package de.bund.bfr.knime.mts;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "MagicTableSampling" Node.
 * This node creates a sample from a given distribution. A.J. Walkers method is used.
 * 
 * @author Markus Freitag
 */
public class MagicTableSamplingNodeDialog extends DefaultNodeSettingsPane {

    protected MagicTableSamplingNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    MagicTableSamplingNodeModel.CFGKEY_COUNT,
                    MagicTableSamplingNodeModel.DEFAULT_COUNT,
                    1, Integer.MAX_VALUE),
                    "Number of samples:", /*step*/ 1, /*componentwidth*/ 5));
        
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				MagicTableSamplingNodeModel.CFGKEY_GUILTY,
        				MagicTableSamplingNodeModel.DEFAULT_GU,
        				0, Integer.MAX_VALUE),
        				"Column index (0=first):", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}


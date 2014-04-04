package de.bund.bfr.knime.paroa.strat;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "Stratosphere" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Markus Freitag
 */
public class StratosphereNodeDialog extends DefaultNodeSettingsPane {

    protected StratosphereNodeDialog() {
        super();
        
        DialogComponentStringSelection methods = new DialogComponentStringSelection(
                new SettingsModelString(
                    StratosphereNodeModel.CFGKEY_METHODS,
                    StratosphereNodeModel.DEFAULT_METHODS),
                    "Method to apply:", StratosphereNodeModel.METHODS);
        
        DialogComponentFileChooser jar = new DialogComponentFileChooser(
        		new SettingsModelString(
        				StratosphereNodeModel.CFGKEY_JAR,
        				StratosphereNodeModel.DEFAULT_EMPTYSTRING),
        				"null", 0, ".jar");
        
        DialogComponentStringSelection local = new DialogComponentStringSelection(
        		new SettingsModelString(
        				StratosphereNodeModel.CFGKEY_LOCAL,
        				StratosphereNodeModel.DEFAULT_EMPTYSTRING),
        				"Run locally or on attached cluster:", StratosphereNodeModel.LOCAL);
        
        DialogComponentFileChooser input_path = new DialogComponentFileChooser(
        		new SettingsModelString(
        				StratosphereNodeModel.CFGKEY_INPUT,
        				StratosphereNodeModel.DEFAULT_EMPTYSTRING),
        				"Input Path:", 1, true, ".*");
                    
        DialogComponentFileChooser output_path = new DialogComponentFileChooser(
        		new SettingsModelString(
        				StratosphereNodeModel.CFGKEY_OUTPUT,
        				StratosphereNodeModel.DEFAULT_EMPTYSTRING),
        				"Output Path:", 2, true, ".*");
        
        jar.setBorderTitle("Executable JAR:");
        input_path.setBorderTitle("Input Path:");
        output_path.setBorderTitle("Output Path:");
        
        addDialogComponent(methods);
        addDialogComponent(local);
        addDialogComponent(jar);
        addDialogComponent(input_path);
        addDialogComponent(output_path);
    }
}


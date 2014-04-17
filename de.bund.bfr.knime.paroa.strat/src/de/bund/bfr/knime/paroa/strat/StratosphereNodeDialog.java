package de.bund.bfr.knime.paroa.strat;

import java.awt.JobAttributes.DialogType;

import javax.swing.JFileChooser;

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
                    "Method to apply:", StratosphereNodeModel.METHOD_CHIOCES);
       /* 
        DialogComponentStringSelection local = new DialogComponentStringSelection(
        		new SettingsModelString(
        				StratosphereNodeModel.CFGKEY_LOCAL,
        				StratosphereNodeModel.DEFAULT_EMPTYSTRING),
        				"Run locally or on attached cluster:", StratosphereNodeModel.LOCAL);
        */
        DialogComponentFileChooser jar = new DialogComponentFileChooser(
        		new SettingsModelString(
        				StratosphereNodeModel.CFGKEY_JAR,
        				StratosphereNodeModel.DEFAULT_EMPTYSTRING),
        				"Executable JAR:", 0, false, ".jar");
                    
        DialogComponentFileChooser inputSales_path = new DialogComponentFileChooser(
        		new SettingsModelString(
        				StratosphereNodeModel.CFGKEY_INPUT_SALES,
        				StratosphereNodeModel.DEFAULT_EMPTYSTRING),
        				"Sales Data Path:");
        
        DialogComponentFileChooser strat_path = new DialogComponentFileChooser(
        		new SettingsModelString(
        				StratosphereNodeModel.CFGKEY_STRAT_PATH,
        				StratosphereNodeModel.DEFAULT_STRAT_PATH),
        				"Strat Path:", 
        				JFileChooser.DIRECTORIES_ONLY , 
        				true);
        
        jar.setBorderTitle("Executable JAR:");
        inputSales_path.setBorderTitle("Sales Data Location:");
        strat_path.setBorderTitle("Stratosphere Location:");
        
        addDialogComponent(methods);
        addDialogComponent(strat_path);
//      addDialogComponent(local);
        addDialogComponent(jar);
        addDialogComponent(inputSales_path);

    }
}


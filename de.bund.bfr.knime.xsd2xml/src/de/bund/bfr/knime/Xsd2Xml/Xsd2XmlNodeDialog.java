package de.bund.bfr.knime.Xsd2Xml;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "Xsd2Xml" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class Xsd2XmlNodeDialog extends DefaultNodeSettingsPane {

	private final DialogComponentFileChooser xsdDialog;
	private final DialogComponentFileChooser xmlDialog;
    private final SettingsModelString xsdFile = new SettingsModelString(Xsd2XmlNodeModel.XSD_FILE, "");
    private final SettingsModelString xmlFile = new SettingsModelString(Xsd2XmlNodeModel.XML_FILE, "");

    /**
     * New pane for configuring the Xsd2Xml node.
     */
    protected Xsd2XmlNodeDialog() {
    	xsdDialog = new DialogComponentFileChooser(xsdFile, Xsd2XmlNodeModel.XSD_FILE, JFileChooser.OPEN_DIALOG, ".xsd");
    	xmlDialog = new DialogComponentFileChooser(xmlFile, Xsd2XmlNodeModel.XML_FILE, JFileChooser.OPEN_DIALOG, ".xml");
    	
    	xsdDialog.setBorderTitle("XSD File");
    	xmlDialog.setBorderTitle("XML File");
    	
    	addDialogComponent(xsdDialog);
    	addDialogComponent(xmlDialog);
    }
}


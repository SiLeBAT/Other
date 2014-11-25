package de.bund.bfr.knime.hdfs.connection;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import layout.SpringUtilities;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.DialogComponentNumberEdit;
import org.knime.core.node.defaultnodesettings.DialogComponentString;

/**
 * <code>NodeDialog</code> for the "HDFSConnection" Node.
 * Connects to a local or remote HDFS server.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Arvid Heise
 */
public class HDFSConnectionNodeDialog extends DefaultNodeSettingsPane {

    private final List<DialogComponent> dialogComponents = new ArrayList<>();
    /**
     * New pane for configuring the HDFSConnection node.
     */
    protected HDFSConnectionNodeDialog() {
		this.addDialogComponent(new DialogComponentString(HDFSConnectionNodeModel.createAddressModel(),
			"Address:", true, 30));
		this.addDialogComponent(new DialogComponentNumberEdit(HDFSConnectionNodeModel.createPortModel(),
			"Port:"));
		
		beautify();
    }

    /* (non-Javadoc)
     * @see org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane#addDialogComponent(org.knime.core.node.defaultnodesettings.DialogComponent)
     */
    @Override
    public void addDialogComponent(DialogComponent diaC) {
    	dialogComponents.add(diaC);
    	super.addDialogComponent(diaC);
    }
	/**
	 * 
	 */
	private void beautify() {
		Container panel = (Container) ((JTabbedPane) (getPanel().getComponent(1))).getTabComponentAt(0);
		panel.removeAll();		
		panel.setLayout(new SpringLayout());
		
		for (DialogComponent dialogComponent : dialogComponents) {
			JPanel componentPanel = dialogComponent.getComponentPanel();
			for (Component component : componentPanel.getComponents()) {
				panel.add(component);
			}
		}
		SpringUtilities.makeCompactGrid(panel, dialogComponents.size(), 2, 3, 3, 5, 5);
		panel.invalidate();
	}
}


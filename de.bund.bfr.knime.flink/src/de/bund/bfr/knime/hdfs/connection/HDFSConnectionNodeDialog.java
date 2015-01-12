/*******************************************************************************
 * Copyright (c) 2014 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.bund.bfr.knime.hdfs.connection;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import layout.SpringUtilities;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.DialogComponentNumberEdit;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;

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
		this.addDialogComponent(new DialogComponentStringSelection(HDFSConnectionNodeModel.createProtocolModel(),
				"Protocol:", "webhdfs", "hdfs"));
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
    	this.dialogComponents.add(diaC);
    	super.addDialogComponent(diaC);
    }
	/**
	 * 
	 */
	private void beautify() {
		Container panel = (Container) ((JTabbedPane) (getPanel().getComponent(1))).getComponentAt(0);
		if(panel instanceof JScrollPane)
			panel = (Container) ((JScrollPane) panel).getViewport().getView();
		panel.removeAll();		
		panel.setLayout(new SpringLayout());
		
		for (DialogComponent dialogComponent : this.dialogComponents) {
			JPanel componentPanel = dialogComponent.getComponentPanel();
			for (Component component : componentPanel.getComponents()) {
				panel.add(component);
			}
		}
		panel.add(Box.createVerticalGlue());
		SpringUtilities.makeCompactGrid(panel, this.dialogComponents.size(), 2, 3, 3, 5, 5);
		panel.invalidate();
	}
}


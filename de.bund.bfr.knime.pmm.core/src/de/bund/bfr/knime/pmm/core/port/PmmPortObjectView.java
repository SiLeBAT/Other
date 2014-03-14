/*******************************************************************************
 * PMM-Lab © 2012, Federal Institute for Risk Assessment (BfR), Germany
 * 
 * PMM-Lab is a set of KNIME-Nodes and KNIME workflows running within the KNIME software plattform (http://www.knime.org.).
 * 
 * PMM-Lab © 2012, Federal Institute for Risk Assessment (BfR), Germany
 * Contact: armin.weiser@bfr.bund.de or matthias.filter@bfr.bund.de 
 * 
 * Developers and contributors to the PMM-Lab project are 
 * Joergen Brandt (BfR)
 * Armin A. Weiser (BfR)
 * Matthias Filter (BfR)
 * Alexander Falenski (BfR)
 * Christian Thoens (BfR)
 * Annemarie Kaesbohrer (BfR)
 * Bernd Appel (BfR)
 * 
 * PMM-Lab is a project under development. Contributions are welcome.
 * 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.bund.bfr.knime.pmm.core.port;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.knime.core.node.NodeView;
import org.knime.core.node.port.pmml.XMLTreeCreator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PmmPortObjectView extends JComponent {

	private static final long serialVersionUID = 1L;

	public PmmPortObjectView(PmmPortObject portObject) {
		setName("PMM Data");
		setLayout(new BorderLayout());
		setBackground(NodeView.COLOR_BACKGROUND);

		try {
			SAXParserFactory saxFac = SAXParserFactory.newInstance();
			SAXParser parser = saxFac.newSAXParser();
			XMLTreeCreator treeCreator = new XMLTreeCreator();

			parser.parse(
					new InputSource(new StringReader(portObject.getXml())),
					treeCreator);

			JTree tree = new JTree();

			tree.setModel(new DefaultTreeModel(treeCreator.getTreeNode()));
			add(new JScrollPane(tree));
			revalidate();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}

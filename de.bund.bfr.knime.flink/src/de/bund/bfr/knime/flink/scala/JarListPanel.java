/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * -------------------------------------------------------------------
 *
 */
package de.bund.bfr.knime.flink.scala;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.knime.core.node.util.ConvenientComboBoxRenderer;
import org.knime.core.node.util.StringHistory;
import org.knime.core.util.SimpleFileFilter;

/**
 * List of jars required for compilation (separate tab).
 * 
 * @author Bernd Wiswedel, University of Konstanz
 */
@SuppressWarnings("serial")
public class JarListPanel extends JPanel {

	private JButton m_addButton;

	private final JList<String> m_addJarList;

	private String[] m_filesCache;

	private JFileChooser m_jarFileChooser;

	private JButton m_removeButton;

	/** Inits GUI. */
	public JarListPanel() {
		super(new BorderLayout());
		this.m_addJarList = new JList<String>(new DefaultListModel<String>()) {
			/** {@inheritDoc} */
			@Override
			protected void processComponentKeyEvent(final KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_A && e.isControlDown()) {
					int end = this.getModel().getSize() - 1;
					this.getSelectionModel().setSelectionInterval(0, end);
				} else if (e.getKeyCode() == KeyEvent.VK_DELETE)
					JarListPanel.this.onJarRemove();
			}
		};
		this.m_addJarList.setCellRenderer(new ConvenientComboBoxRenderer());
		this.add(new JScrollPane(this.m_addJarList), BorderLayout.CENTER);
		JPanel southP = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
		this.m_addButton = new JButton("Add...");
		this.m_addButton.addActionListener(new ActionListener() {
			/** {@inheritDoc} */
			@Override
			public void actionPerformed(final ActionEvent e) {
				JarListPanel.this.onJarAdd();
			}
		});
		this.m_removeButton = new JButton("Remove");
		this.m_removeButton.addActionListener(new ActionListener() {
			/** {@inheritDoc} */
			@Override
			public void actionPerformed(final ActionEvent e) {
				JarListPanel.this.onJarRemove();
			}
		});
		this.m_addJarList.addListSelectionListener(new ListSelectionListener() {
			/** {@inheritDoc} */
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				JarListPanel.this.m_removeButton.setEnabled(!JarListPanel.this.m_addJarList.isSelectionEmpty());
			}
		});
		this.m_removeButton.setEnabled(!this.m_addJarList.isSelectionEmpty());
		southP.add(this.m_addButton);
		southP.add(this.m_removeButton);
		this.add(southP, BorderLayout.SOUTH);

		JPanel northP = new JPanel(new FlowLayout());
		JLabel label = new JLabel("<html><body>Specify additional jar files "
			+ "that are necessary for the snippet to run</body></html>");
		northP.add(label);
		this.add(northP, BorderLayout.NORTH);
	}

	/**
	 * Adds a listener to the list that's notified each time a change
	 * to the list of jar files occurs.
	 * 
	 * @param l
	 *        the <code>ListDataListener</code> to be added
	 */
	public void addListDataListener(final ListDataListener l) {
		this.m_addJarList.getModel().addListDataListener(l);
	}

	/**
	 * Get the jar files defined in this panel.
	 * 
	 * @return the jar files
	 */
	public String[] getJarFiles() {
		DefaultListModel<?> jarListModel =
			(DefaultListModel<?>) this.m_addJarList.getModel();
		String[] copy = new String[jarListModel.getSize()];
		if (jarListModel.getSize() > 0)
			jarListModel.copyInto(copy);
		return copy;
	}

	/**
	 * Sets whether or not this component is enabled.
	 * A component that is enabled may respond to user input,
	 * while a component that is not enabled cannot respond to
	 * user input.
	 * 
	 * @param enabled
	 *        true if this component should be enabled, false otherwise
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		if (this.isEnabled() != enabled) {
			this.m_addJarList.setEnabled(enabled);
			this.m_addButton.setEnabled(enabled);
			this.m_removeButton.setEnabled(enabled);
		}
		super.setEnabled(enabled);
	}

	/**
	 * Set the files to display.
	 * 
	 * @param files
	 *        the files
	 */
	public void setJarFiles(final String[] files) {
		boolean doUpdate = false;
		if (this.m_filesCache == null) {
			this.m_filesCache = files;
			doUpdate = true;
		} else if (!Arrays.deepEquals(this.m_filesCache, files)) {
			this.m_filesCache = files;
			doUpdate = true;
		}
		if (doUpdate) {
			DefaultListModel<String> jarListModel =
				(DefaultListModel<String>) this.m_addJarList.getModel();
			jarListModel.removeAllElements();
			for (String f : files) {
				File file = new File(f);
				jarListModel.addElement(file.getAbsolutePath());
			}
		}
	}

	private void onJarAdd() {
		DefaultListModel<String> model = (DefaultListModel<String>) this.m_addJarList.getModel();
		Set<Object> hash = new HashSet<Object>();
		for (Enumeration<?> e = model.elements(); e.hasMoreElements();)
			hash.add(e.nextElement());
		StringHistory history =
			StringHistory.getInstance("java_snippet_jar_dirs");
		if (this.m_jarFileChooser == null) {
			File dir = null;
			for (String h : history.getHistory()) {
				File temp = new File(h);
				if (temp.isDirectory()) {
					dir = temp;
					break;
				}
			}
			this.m_jarFileChooser = new JFileChooser(dir);
			this.m_jarFileChooser.setFileFilter(
				new SimpleFileFilter(".zip", ".jar"));
			this.m_jarFileChooser.setMultiSelectionEnabled(true);
		}
		int result = this.m_jarFileChooser.showDialog(this.m_addJarList, "Select");

		if (result == JFileChooser.APPROVE_OPTION) {
			for (File f : this.m_jarFileChooser.getSelectedFiles()) {
				String s = f.getAbsolutePath();
				if (hash.add(s))
					model.addElement(s);
			}
			history.add(
				this.m_jarFileChooser.getCurrentDirectory().getAbsolutePath());
		}
	}

	private void onJarRemove() {
		DefaultListModel<?> model = (DefaultListModel<?>) this.m_addJarList.getModel();
		int[] sels = this.m_addJarList.getSelectedIndices();
		int last = Integer.MAX_VALUE;
		// traverse backwards (editing list in loop body)
		for (int i = sels.length - 1; i >= 0; i--) {
			assert sels[i] < last : "Selection list not ordered";
			model.remove(sels[i]);
		}
	}

}

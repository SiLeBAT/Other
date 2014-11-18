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
package de.bund.bfr.knime.flink.scala;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ViewUtils;

import de.bund.bfr.knime.flink.scala.ScalaSnippetDocument.Section;

/**
 * <code>NodeDialog</code> for the "FlinkScalaBuilder" Node.
 * Compiles a Scala snippet into a Flink jar.
 * 
 * @author Arvid Heise
 */
public class FlinkScalaBuilderNodeDialog extends NodeDialogPane {
	// the logger instance
	private static final NodeLogger logger = NodeLogger
		.getLogger(FlinkScalaBuilderNodeDialog.class);

	private static final String SNIPPET_TAB = "Scala Snippet";

	/** The settings. */
	protected ScalaSnippetSettings settings;

	private ParameterTable inFieldsTable;

	private boolean isEnabled;

	private JarListPanel jarPanel;

	private ScalaSnippet snippet;

	private SnippetTextArea snippetTextArea;

	/**
	 * New pane for configuring FlinkScalaBuilder node dialog.
	 * This is just a suggestion to demonstrate possible default dialog
	 * components.
	 */
	protected FlinkScalaBuilderNodeDialog() {
		this.settings = new ScalaSnippetSettings();
		this.snippet = new ScalaSnippet();
		JPanel panel = this.createPanel();
		this.addTab(SNIPPET_TAB, panel);
		panel.setPreferredSize(new Dimension(800, 600));
		this.addTab("Additional Libraries", this.createJarPanel());
		this.isEnabled = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean closeOnESC() {
		// do not close on ESC, since ESC is used to close autocomplete popups
		// in the snippets textarea.
		return false;
	}

	/**
	 * Determines whether this component is enabled. An enabled component
	 * can respond to user input and generate events.
	 * 
	 * @return <code>true</code> if the component is enabled, <code>false</code> otherwise
	 */
	public boolean isEnabled() {
		return this.isEnabled;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onOpen() {
		this.snippetTextArea.requestFocus();
		this.snippetTextArea.requestFocusInWindow();
		// reset style which causes a recreation of the popup window with
		// the side effect, that all folds are recreated, so that we must collapse
		// them next (bug 4061)
		// this.snippetTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
		// this.snippetTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SCALA);
		// collapse all folds
		// FoldManager foldManager = this.snippetTextArea.getFoldManager();
		// int foldCount = foldManager.getFoldCount();
		// for (int i = 0; i < foldCount; i++) {
		// Fold fold = foldManager.getFold(i);
		// fold.setCollapsed(true);
		// }
	}

	/**
	 * Create an empty, titled border.
	 * 
	 * @param string
	 *        Title of the border.
	 * @return Such a new border.
	 */
	protected Border createEmptyTitledBorder(final String string) {
		return BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(
			5, 0, 0, 0), string, TitledBorder.DEFAULT_JUSTIFICATION,
			TitledBorder.BELOW_TOP);
	}

	/**
	 * Create Panel with additional options to be displayed in the south.
	 * 
	 * @return options panel or null if there are no additional options.
	 */
	protected JPanel createOptionsPanel() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeDialogPane#loadSettingsFrom(org.knime.core.node.NodeSettingsRO,
	 * org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected void loadSettingsFrom(final NodeSettingsRO settings, PortObjectSpec[] specs)
			throws NotConfigurableException {
		ViewUtils.invokeAndWaitInEDT(new Runnable() {
			@Override
			public void run() {
				FlinkScalaBuilderNodeDialog.this.loadSettingsFromInternal(settings);
			}
		});
	}

	/**
	 * Load settings invoked from the EDT-Thread.
	 * 
	 * @param settings
	 *        the settings to load
	 * @param specs
	 *        the specs of the input table
	 */
	protected void loadSettingsFromInternal(final NodeSettingsRO settings) {
		this.settings.loadSettingsForDialog(settings);

		this.snippet.setSettings(this.settings);
		this.jarPanel.setJarFiles(this.settings.getJarFiles());

		// collapse all folds
		int mainOffset = this.snippet.getDocument().getGuardedSection(Section.MainStart).getStart().getOffset();
		FoldManager foldManager = this.snippetTextArea.getFoldManager();
		foldManager.reparse();
		int foldCount = foldManager.getFoldCount();
		for (int i = 0; i < foldCount - 1; i++) {
			Fold fold = this.snippetTextArea.getFoldManager().getFold(i);
			if (fold.getStartOffset() == mainOffset)
				fold.setCollapsed(true);
		}

		// set caret position to the start of the custom expression
		this.snippetTextArea.setCaretPosition(
			this.snippet.getDocument().getGuardedSection(
				ScalaSnippetDocument.Section.TaskStart).getEnd().getOffset() + 1);
		this.snippetTextArea.requestFocusInWindow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings)
			throws InvalidSettingsException {
		ViewUtils.invokeAndWaitInEDT(new Runnable() {

			@Override
			public void run() {
				// Commit editing - This is a workaround for a bug in the Dialog
				// since the tables do not loose focus when OK or Apply is
				// pressed.
				if (null != FlinkScalaBuilderNodeDialog.this.inFieldsTable.getTable().getCellEditor())
					FlinkScalaBuilderNodeDialog.this.inFieldsTable.getTable().getCellEditor().
						stopCellEditing();
			}
		});
		ScalaSnippetSettings s = this.snippet.getSettings();

		// if settings have less fields than defined in the table it means
		// that the tables contain errors
		ParameterTableModel inFieldsModel =
			(ParameterTableModel) this.inFieldsTable.getTable().getModel();
		if (!inFieldsModel.validateValues())
			throw new IllegalArgumentException(
				"The input fields table has errors.");

		s.saveSettings(settings);
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
	protected void setEnabled(final boolean enabled) {
		if (this.isEnabled != enabled) {
			this.inFieldsTable.setEnabled(enabled);
			this.jarPanel.setEnabled(enabled);
			this.snippetTextArea.setEnabled(enabled);
		}
		this.isEnabled = enabled;

	}

	private JPanel createJarPanel() {
		this.jarPanel = new JarListPanel();
		this.jarPanel.addListDataListener(new ListDataListener() {
			@Override
			public void contentsChanged(final ListDataEvent e) {
				this.updateSnippet();
			}

			@Override
			public void intervalAdded(final ListDataEvent e) {
				this.updateSnippet();
			}

			@Override
			public void intervalRemoved(final ListDataEvent e) {
				this.updateSnippet();
			}

			private void updateSnippet() {
				FlinkScalaBuilderNodeDialog.this.snippet.setJarFiles(FlinkScalaBuilderNodeDialog.this.jarPanel.getJarFiles());
				// force reparsing of the snippet
				for (int i = 0; i < FlinkScalaBuilderNodeDialog.this.snippetTextArea.getParserCount(); i++)
					FlinkScalaBuilderNodeDialog.this.snippetTextArea.forceReparsing(i);
				// update autocompletion
				FlinkScalaBuilderNodeDialog.this.updateAutocompletion();
			}
		});
		return this.jarPanel;
	}

	private JPanel createPanel() {
		JPanel p = new JPanel(new BorderLayout());
		JComponent snippet = this.createSnippetPanel();

		JPanel centerPanel = new JPanel(new GridLayout(0, 1));
		this.inFieldsTable = new ParameterTable();

		// use split pane for fields
		this.inFieldsTable.setBorder(BorderFactory.createTitledBorder("Input"));

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainSplitPane.setTopComponent(this.inFieldsTable);
		// minimize size of tables at the bottom
		this.inFieldsTable.setPreferredSize(this.inFieldsTable.getMinimumSize());
		mainSplitPane.setBottomComponent(snippet);
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setResizeWeight(0.3); // snippet gets more space, table with in/out gets less extra space

		centerPanel.add(mainSplitPane);

		this.inFieldsTable.getTable().getModel().addTableModelListener(
			new TableModelListener() {
				@Override
				public void tableChanged(final TableModelEvent e) {
					FlinkScalaBuilderNodeDialog.this.snippet.setParameters(FlinkScalaBuilderNodeDialog.this.inFieldsTable.getParameters());
				}
			});

		p.add(centerPanel, BorderLayout.CENTER);
		JPanel optionsPanel = this.createOptionsPanel();
		if (optionsPanel != null)
			p.add(optionsPanel, BorderLayout.SOUTH);
		return p;
	}

	/**
	 * Create the panel with the snippet.
	 */
	private JComponent createSnippetPanel() {
		this.updateAutocompletion();

		this.snippetTextArea = new SnippetTextArea(this.snippet);

		// reset style which causes a recreation of the folds
		// this code is also executed in "onOpen" but that is not called for the template viewer tab
		this.snippetTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
		this.snippetTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SCALA);
		JScrollPane snippetScroller = new RTextScrollPane(this.snippetTextArea);
		JPanel snippet = new JPanel(new BorderLayout());
		snippet.add(snippetScroller, BorderLayout.CENTER);
		ErrorStrip es = new ErrorStrip(this.snippetTextArea);
		snippet.add(es, BorderLayout.LINE_END);
		return snippet;
	}

	private void updateAutocompletion() {
		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		lsf.getSupportFor(
			org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_SCALA);

	}
}

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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 * The document used in the jsnippet dialogs.<br/>
 * <br/>
 * This class is heavily inspired by JSnippet plugin from Heiko Hofer.
 */
public class ScalaSnippetDocument extends GuardedDocument<ScalaSnippetDocument.Section> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6874925025746360720L;

	/**
	 * Create a new instance.
	 */
	public ScalaSnippetDocument() {
		super(SyntaxConstants.SYNTAX_STYLE_SCALA);
		try {
			this.addGuardedSection(Section.Imports, this.getLength());
			this.insertString(this.getLength(), "\n\n", null);
			GuardedSection jobStart = this.addGuardedSection(Section.JobStart, this.getLength());
			jobStart.setText("// Job class\nobject Job {\n");
			this.addGuardedSection(Section.Fields, this.getLength());
			this.insertString(this.getLength(), "\n", null);

			GuardedSection mainStart = this.addGuardedSection(Section.MainStart, this.getLength());
			GuardedSection mainEnd = this.addGuardedSection(Section.MainEnd, this.getLength());
			mainEnd.setFoldStart(mainStart);
			this.insertString(this.getLength(), "\n", null);

			GuardedSection taskStart = this.addGuardedSection(Section.TaskStart, this.getLength());
			taskStart.setText("  def performTask() {\n");
			this.insertString(this.getLength(), "    \n", null);
			GuardedSection taskEnd = this.addGuardedSection(Section.TaskEnd, this.getLength());
			taskEnd.setText("  }\n");
			taskEnd.setFoldStart(taskStart);
			this.insertString(this.getLength(), "\n  \n", null);

			GuardedSection jobEnd = this.addGuardedSection(Section.JobEnd, this.getLength());
			jobEnd.setText("}");
			jobEnd.setFoldStart(jobStart);
		} catch (BadLocationException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertString(final int offset, final String str, final AttributeSet a) throws BadLocationException {
		if (this.getBreakGuarded())
			super.insertString(offset, str, a);
		else // Hack when string is an import declaration as created by
		// auto completion.
		if (this.getGuardedSection(Section.Imports).contains(offset) && str.startsWith("\nimport ")) {
			// change offset so that the import is inserted in the
			// user imports.
			int min = this.getGuardedSection(Section.Imports).getEnd().getOffset() + 1;
			int pos = this.getGuardedSection(Section.JobStart).getStart().getOffset() - 1;
			try {
				while (this.getText(pos, 1).equals("\n")
					&& this.getText(pos - 1, 1).equals("\n")
					&& pos > min)
					pos--;
				super.insertString(pos, str, a);
			} catch (BadLocationException e) {
				// do nothing, not critical
			}
		} else
			super.insertString(offset, str, a);
	}

	public enum Section {
		Fields, Imports, JobEnd, JobStart, MainEnd, MainStart, TaskEnd, TaskStart;
	}

}

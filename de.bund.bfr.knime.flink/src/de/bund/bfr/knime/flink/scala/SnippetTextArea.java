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

import java.awt.Color;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.knime.base.node.util.KnimeSyntaxTextArea;

import de.bund.bfr.knime.flink.scala.ScalaSnippetDocument.Section;

/**
 * A text area for the Scala snippet expression.<br/>
 * <br/>
 * Adapted from JSnippet plugin from Heiko Hofer.
 */
@SuppressWarnings("serial")
public class SnippetTextArea extends KnimeSyntaxTextArea {

	/**
	 * Create a new component.
	 * 
	 * @param snippet
	 *        the snippet
	 */
	public SnippetTextArea(final ScalaSnippet snippet) {
		// initial text != null causes a null pointer exception
		super(new ScalaSnippetDocument(), null, 20, 60);

		this.setDocument(snippet.getDocument());
		// addParser(snippet.getParser());
		int parserCount = this.getParserCount();
		for (int index = 0; index < parserCount; index++)
			this.removeParser(this.getParser(0));
		// addParser(new DummyParser());

		boolean parserInstalled =
			FoldParserManager.get().getFoldParser(SYNTAX_STYLE_SCALA) instanceof GuardedDocumentFoldParser;
		if (!parserInstalled)
			FoldParserManager.get().addFoldParserMapping(SYNTAX_STYLE_SCALA, new GuardedDocumentFoldParser());

		this.setCodeFoldingEnabled(true);
		this.setSyntaxEditingStyle(SYNTAX_STYLE_SCALA);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getForegroundForToken(final Token t) {
		if (this.isInGuardedSection(t.offset))
			return Color.gray;
		return super.getForegroundForToken(t);
	}

	/**
	 * Returns true when offset is within a guarded section.
	 * 
	 * @param offset
	 *        the offset to test
	 * @return true when offset is within a guarded section.
	 */
	private boolean isInGuardedSection(final int offset) {
		@SuppressWarnings("unchecked")
		GuardedDocument<Section> doc = (GuardedDocument<Section>) this.getDocument();

		for (Section section : doc.getGuardedSections()) {
			GuardedSection gs = doc.getGuardedSection(section);
			if (gs.contains(offset))
				return true;
		}
		return false;
	}
}

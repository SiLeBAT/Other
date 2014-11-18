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

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldType;
import org.knime.core.node.NodeLogger;

/**
 * A FoldParser that gives folds for every guarded section.<br/>
 * <br/>
 * Adapted from JSnippet plugin from Heiko Hofer.
 */
public class GuardedDocumentFoldParser implements FoldParser {
	private static final NodeLogger LOGGER = NodeLogger.getLogger(GuardedDocumentFoldParser.class);

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List getFolds(final RSyntaxTextArea textArea) {
		List folds = new ArrayList();
		GuardedDocument doc = (GuardedDocument) textArea.getDocument();
		for (Object name : doc.getGuardedSections()) {
			GuardedSection guard = doc.getGuardedSection(name);
			if (guard.getFoldStart() != guard)
				continue;
			Fold fold;
			try {
				fold = new Fold(FoldType.FOLD_TYPE_USER_DEFINED_MIN,
					textArea, guard.getStart().getOffset());
				fold.setEndOffset(guard.getFoldEnd().getEnd().getOffset());
				folds.add(fold);
			} catch (BadLocationException e) {
				LOGGER.debug(e.getMessage());
			}

		}
		return folds;
	}

}

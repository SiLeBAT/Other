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

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

/**
 * A guarded, e.g. non editable section in a document<br/>
 * <br/>
 * Adapted from JSnippet plugin from Heiko Hofer.
 */
public final class GuardedSection {

	private Position end;

	private GuardedSection foldStart = this, foldEnd = this;

	private final GuardedDocument<?> m_document;

	private int m_endOffset;

	private Position start;

	/**
	 * Creates new <code>GuardedSection</code>.
	 * 
	 * @param start
	 *        the start position of the range
	 * @param end
	 *        the end position of the range
	 * @param document
	 *        the document this section belongs to
	 */
	private GuardedSection(final Position start, final Position end,
			final GuardedDocument<?> document, final boolean footer) {
		this.start = start;
		this.end = end;
		this.m_document = document;
		this.m_endOffset = footer ? 1 : 0;
	}

	/**
	 * Returns true when offset is in the guarded section.
	 * 
	 * @param offset
	 *        the offset to test
	 * @return true when offset is in the guarded section
	 */
	public boolean contains(final int offset) {
		return this.start.getOffset() <= offset
			&& this.end.getOffset() + this.m_endOffset >= offset;
	}

	/**
	 * Get the end position.
	 * 
	 * @return the end position
	 */
	public Position getEnd() {
		return this.end;
	}

	/**
	 * Returns the foldEnd.
	 * 
	 * @return the foldEnd
	 */
	public GuardedSection getFoldEnd() {
		return this.foldEnd;
	}

	/**
	 * Returns the foldStart.
	 * 
	 * @return the foldStart
	 */
	public GuardedSection getFoldStart() {
		return this.foldStart;
	}

	/**
	 * Get the start position.
	 * 
	 * @return the start position
	 */
	public Position getStart() {
		return this.start;
	}

	/**
	 * Get the text within the range.
	 * 
	 * @return the text
	 * @exception BadLocationException
	 *            if the positions are out of the
	 *            bounds of the document
	 */
	public String getText() throws BadLocationException {
		int p1 = this.start.getOffset();
		int p2 = this.end.getOffset();
		// for negative length when p1 > p2 => return ""
		return p1 <= p2 ? this.m_document.getText(p1, p2 - p1 + 1) : "";
	}

	/**
	 * Returns true when the guarded section intersects the given section.
	 * 
	 * @param offset
	 *        the start point of the section to test
	 * @param length
	 *        the length of the section to test
	 * @return true when the guarded section intersects the given section
	 */
	public boolean intersects(final int offset, final int length) {
		return this.end.getOffset() + this.m_endOffset >= offset
			&& this.start.getOffset() <= offset + length;
	}

	/**
	 * Sets the end to the specified value.
	 * 
	 * @param end
	 *        the end to set
	 */
	public void setEnd(Position end) {
		if (end == null)
			throw new NullPointerException("end must not be null");

		this.end = end;
	}

	/**
	 * Sets the foldEnd to the specified value.
	 * 
	 * @param foldEnd
	 *        the foldEnd to set
	 */
	public void setFoldEnd(GuardedSection foldEnd) {
		if (foldEnd == null)
			throw new NullPointerException("foldEnd must not be null");

		if (this.foldEnd != (this.foldEnd = foldEnd))
			foldEnd.setFoldStart(this);
	}

	/**
	 * Sets the foldStart to the specified value.
	 * 
	 * @param foldStart
	 *        the foldStart to set
	 */
	public void setFoldStart(GuardedSection foldStart) {
		if (foldStart == null)
			throw new NullPointerException("foldStart must not be null");

		if (this.foldStart != (this.foldStart = foldStart))
			foldStart.setFoldEnd(this);
	}

	/**
	 * Sets the start to the specified value.
	 * 
	 * @param start
	 *        the start to set
	 */
	public void setStart(Position start) {
		if (start == null)
			throw new NullPointerException("start must not be null");

		this.start = start;
	}

	/**
	 * Replaces the text of this guarded section.
	 * 
	 * @param t
	 *        new text to insert over existing text
	 * @exception BadLocationException
	 *            if the positions are out of the bounds
	 *            of the document
	 */
	public void setText(final String t) throws BadLocationException {
		int p1 = this.start.getOffset();
		int p2 = this.end.getOffset();

		boolean orig = this.m_document.getBreakGuarded();
		this.m_document.setBreakGuarded(true);
		// Empty text is not allowed, this would break the positioning
		String text = null == t || t.isEmpty() ? " " : t;
		text = text.endsWith("\n")
			? text.substring(0, text.length() - 1)
			: text;

		int docLen = this.m_document.getLength();
		this.m_document.insertString(p1 + 1, text, null);

		// compute length of inserted string
		int len = this.m_document.getLength() - docLen;
		this.m_document.remove(p1 + 1 + len, p2 - p1 - 1);
		this.m_document.remove(p1, 1);
		this.m_document.setBreakGuarded(orig);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "(" + this.start.getOffset() + ", " + this.end.getOffset() + ")";
	}

	/**
	 * Creates a guarded section in the document. Note that text can always
	 * be inserted after the guarded section. To prevent this use the method
	 * addGuardedFootterSection(...).
	 * 
	 * @param start
	 *        the start of the guarded section
	 * @param end
	 *        the end point of the guarded section
	 * @param document
	 *        the document
	 * @return the newly created guarded section
	 */
	public static GuardedSection create(final Position start,
			final Position end, final GuardedDocument<?> document) {
		return new GuardedSection(start, end, document, false);
	}

	/**
	 * Creates a guarded section in the document. No text can be inserted
	 * right after this guarded section.
	 * 
	 * @param start
	 *        the start of the guarded section
	 * @param end
	 *        the end point of the guarded section
	 * @param document
	 *        the document
	 * @return the newly created guarded section
	 */
	public static GuardedSection createFooter(final Position start,
			final Position end, final GuardedDocument<?> document) {
		return new GuardedSection(start, end, document, true);
	}
}

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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;

/**
 * A Document with guarded, non editable areas.<br/>
 * <br/>
 * Adapted from JSnippet plugin from Heiko Hofer.
 */
@SuppressWarnings("serial")
public class GuardedDocument<G> extends RSyntaxDocument {
	private boolean m_breakGuarded;

	private Map<G, GuardedSection> m_guards;

	/**
	 * Constructs a plain text document. A default root element is created,
	 * and the tab size set to 5.
	 * 
	 * @param syntaxStyle
	 *        The syntax highlighting scheme to use.
	 */
	public GuardedDocument(final String syntaxStyle) {
		super(syntaxStyle);
		this.m_guards = new LinkedHashMap<G, GuardedSection>();
	}

	/**
	 * Add a named guarded section to the document. No text can be inserted
	 * right after this guarded section.
	 * 
	 * @param name
	 *        the name of the guarded section
	 * @param offset
	 *        the offset of the section (start point)
	 * @return the newly created guarded section
	 * @throws BadLocationException
	 *         if offset is in a guarded section
	 */
	public GuardedSection addGuardedFooterSection(final G name,
			final int offset)
			throws BadLocationException {
		return this.doAddGuardedSection(name, offset, true);
	}

	/**
	 * Add a named guarded section to the document. Note that text can always
	 * be inserted after the guarded section. To prevent this use the method
	 * addGuardedFootterSection(...).
	 * 
	 * @param name
	 *        the name of the guarded section
	 * @param offset
	 *        the offset of the section (start point)
	 * @return the newly created guarded section
	 * @throws BadLocationException
	 *         if offset is in a guarded section
	 */
	public GuardedSection addGuardedSection(final G name, final int offset)
			throws BadLocationException {
		return this.doAddGuardedSection(name, offset, false);
	}

	/**
	 * Returns true guarded areas can be edited.
	 * 
	 * @return the break guarded property
	 */
	public boolean getBreakGuarded() {
		return this.m_breakGuarded;
	}

	/**
	 * Retrieve guarded section by its name.
	 * 
	 * @param name
	 *        the name of the guarded section
	 * @return the guarded section or null if a guarded section with the
	 *         given name does not exist
	 */
	public GuardedSection getGuardedSection(final G name) {
		return this.m_guards.get(name);
	}

	/**
	 * Get the list of guarded sections.
	 * 
	 * @return the list of guarded sections.
	 */
	public Set<G> getGuardedSections() {
		return this.m_guards.keySet();
	}

	/**
	 * Get the text between two subsequent guarded sections.
	 * 
	 * @param guard1
	 *        the first guarded section
	 * @param guard2
	 *        the second guarded section
	 * @return the string between the given guarded sections
	 * @throws BadLocationException
	 *         when the guarded sections do not exist,
	 *         when they are no subsequent guarded sections.
	 */
	public String getTextBetween(final G guard1, final G guard2) throws BadLocationException {
		int start = this.getGuardedSection(guard1).getEnd().getOffset();
		int end = this.getGuardedSection(guard2).getStart().getOffset();
		if (end < start)
			throw new BadLocationException("The offset of the first guarded"
				+ " section is greaten than the offset of the second"
				+ " guarded section.", start);

		int offset = start + 1;
		int length = end - start - 2;
		if (this.m_breakGuarded)
			return this.getText(offset, length);
		// check if a guarded section intersects with [offset, offset+len]
		for (GuardedSection gs : this.m_guards.values())
			if (gs.intersects(offset, length))
				throw new BadLocationException("Cannot replace text "
					+ "that intersects with a guarded section.",
					offset);
		return this.getText(offset, length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertString(final int offset, final String str,
			final AttributeSet a)
			throws BadLocationException {
		if (this.m_breakGuarded)
			super.insertString(offset, str, a);
		else {
			// Check if pos is within a guarded section
			for (GuardedSection gs : this.m_guards.values())
				if (gs.contains(offset))
					throw new BadLocationException(
						"Cannot insert text in guarded section.", offset);
			super.insertString(offset, str, a);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(final int offset, final int len)
			throws BadLocationException {
		if (this.m_breakGuarded)
			super.remove(offset, len);
		else {
			// check if a guarded section intersects with [offset, offset+len]
			for (GuardedSection gs : this.m_guards.values())
				if (gs.intersects(offset, len))
					throw new BadLocationException("Cannot remove text "
						+ "that intersects with a guarded section.",
						offset);
			super.remove(offset, len);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replace(final int offset, final int length, final String text,
			final AttributeSet attrs) throws BadLocationException {
		if (this.m_breakGuarded)
			super.replace(offset, length, text, attrs);
		else {
			// check if a guarded section intersects with [offset, offset+len]
			for (GuardedSection gs : this.m_guards.values())
				if (gs.intersects(offset, length))
					throw new BadLocationException("Cannot replace text that intersects with a guarded section.",
						offset);
			super.replace(offset, length, text, attrs);
		}
	}

	/**
	 * Replaces the text between two subsequent guarded sections.
	 * 
	 * @param guard1
	 *        the first guarded section
	 * @param guard2
	 *        the second guarded section
	 * @param s
	 *        the string to replace with
	 * @throws BadLocationException
	 *         when the guarded sections do not exist,
	 *         when they are not subsequent guarded sections or when there is no
	 *         character between the guarded sections.
	 */
	public void replaceBetween(final G guard1, final G guard2, final String s) throws BadLocationException {
		int start = this.getGuardedSection(guard1).getEnd().getOffset();
		int end = this.getGuardedSection(guard2).getStart().getOffset();
		if (end < start)
			throw new BadLocationException("The offset of the first guarded"
				+ " section is greaten than the offset of the second"
				+ " guarded section.", start);

		int offset = start + 1;
		int length = end - start - 2;

		this.replace(offset, length, s, null);
	}

	/**
	 * Set property if guarded areas can be edited.
	 * 
	 * @param breakGuarded
	 *        the break guarded property to set
	 */
	public void setBreakGuarded(final boolean breakGuarded) {
		this.m_breakGuarded = breakGuarded;
	}

	/** Add a named guarded section to the document. */
	private GuardedSection doAddGuardedSection(final G name,
			final int offset, final boolean isFooter)
			throws BadLocationException {
		for (GuardedSection gs : this.m_guards.values())
			if (gs.getStart().getOffset() < offset
				&& gs.getEnd().getOffset() > offset)
				throw new IllegalArgumentException(
					"Guarded sections may not overlap.");
		GuardedSection gs = this.m_guards.get(name);
		if (gs != null)
			throw new IllegalArgumentException(
				"Guarded section with name \"" + name
					+ "\" does already exist.");
		boolean orig = this.getBreakGuarded();
		this.setBreakGuarded(true);
		this.insertString(offset, " \n", null);
		this.setBreakGuarded(orig);

		GuardedSection guard = isFooter
			? GuardedSection.createFooter(this.createPosition(offset), this.createPosition(offset + 1), this)
			: GuardedSection.create(this.createPosition(offset), this.createPosition(offset + 1), this);
		this.m_guards.put(name, guard);

		return guard;
	}

}

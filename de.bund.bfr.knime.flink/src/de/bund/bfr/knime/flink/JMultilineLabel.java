package de.bund.bfr.knime.flink;

import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.text.Document;

public class JMultilineLabel extends JTextArea {
	private static final long serialVersionUID = 1L;

	public JMultilineLabel(String text) {
		super(text);
	}

	/**
	 * Initializes JMultilineLabel.
	 */
	public JMultilineLabel() {
		super();
	}

	/**
	 * Initializes JMultilineLabel.
	 * 
	 * @param doc
	 * @param text
	 * @param rows
	 * @param columns
	 */
	public JMultilineLabel(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);
	}

	/**
	 * Initializes JMultilineLabel.
	 * 
	 * @param doc
	 */
	public JMultilineLabel(Document doc) {
		super(doc);
	}

	/**
	 * Initializes JMultilineLabel.
	 * 
	 * @param rows
	 * @param columns
	 */
	public JMultilineLabel(int rows, int columns) {
		super(rows, columns);
	}

	/**
	 * Initializes JMultilineLabel.
	 * 
	 * @param text
	 * @param rows
	 * @param columns
	 */
	public JMultilineLabel(String text, int rows, int columns) {
		super(text, rows, columns);
	}

	{
		setEditable(false);
		setCursor(null);
		setOpaque(false);
		setFocusable(false);
		setFont(UIManager.getFont("Label.font"));
		setWrapStyleWord(true);
		setLineWrap(true);
	}
}
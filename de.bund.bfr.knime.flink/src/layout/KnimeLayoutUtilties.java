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
package layout;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.knime.core.node.NodeDialogPane;

import com.google.common.collect.Lists;

/**
 * 
 */
public class KnimeLayoutUtilties {

	private int leftMargin = 3, rightMargin = 3, topMargin = 3, bottomMargin = 3;

	private int xPad = 5, yPad = 5;

	public void beautify(NodeDialogPane dialog) {
		Container panel = getMainPanel(dialog);

		beautify(panel);
	}

	public static Container getMainPanel(NodeDialogPane dialog) {
		Container panel = (Container) ((JTabbedPane) (dialog.getPanel().getComponent(1))).getComponentAt(0);
		if (panel instanceof JScrollPane)
			panel = (Container) ((JScrollPane) panel).getViewport().getView();
		return panel;
	}

	public void beautify(Container panel) {
		SpringLayout layout = new SpringLayout();
		Map<Component, Boolean> spanToRight = new IdentityHashMap<>();
		List<List<Component>> grid = extractGrid(panel, spanToRight);

		int cols = 1;
		for (List<Component> row : grid)
			cols = Math.max(cols, row.size());

		// add empty stretchable line
		List<Component> lastRow = new ArrayList<>();
		for (int index = 0; index < cols; index++)
			lastRow.add(Box.createVerticalGlue());
		grid.add(lastRow);
		int rows = grid.size();

		Map<Component, int[]> spannedComponents = findSpannedComponents(spanToRight, grid, cols);

		readdComponents(panel, grid);
		panel.setLayout(layout);

		Spring x = alignColumns(layout, grid, cols, rows, spannedComponents);

		Spring y = alignRows(layout, grid, cols, rows);

		// Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(panel);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);

		// if(rows == 2) {
		// // SpringUtilities.makeCompactGrid(panel, rows, cols, leftMargin, topMargin, xPad, yPad);
		// x = alignColumns(layout, grid, cols, rows, spannedComponents);
		// y = alignRows(layout, grid, cols, rows);
		// pCons.setConstraint(SpringLayout.SOUTH, y);
		// pCons.setConstraint(SpringLayout.EAST, x);
		// }
	}

	/**
	 * Finds components that span adjacent cells in a row.
	 * 
	 * @param spanToRight
	 * @param grid
	 * @param cols
	 * @return
	 */
	private Map<Component, int[]> findSpannedComponents(Map<Component, Boolean> spanToRight,
			List<List<Component>> grid, int cols) {
		Map<Component, int[]> spannedComponents = new IdentityHashMap<>();
		for (List<Component> row : grid) {
			for (int index = 0; index < row.size(); index++) {
				if (spanToRight.containsKey(row.get(index))) {
					spannedComponents.put(row.get(index), new int[] { index, cols - 1 });
				} else if (index > 0 && row.get(index - 1) == row.get(index)) {
					int start = index - 1;
					while (start > 0 && row.get(start - 1) == row.get(index))
						start--;
					spannedComponents.put(row.get(index), new int[] { start, index });
				}
			}
			// fill up row with nulls
			row.addAll(Arrays.asList(new Component[cols - row.size()]));
		}
		return spannedComponents;
	}

	/**
	 * Removes all boxes and adds the children directly back to the dialog.
	 * 
	 * @param panel
	 * @param grid
	 */
	private void readdComponents(Container panel, List<List<Component>> grid) {
		panel.removeAll();
		Map<Component, Boolean> alreadyAdded = new IdentityHashMap<>();
		for (List<Component> row : grid) {
			for (Component cell : row) {
				if (cell != null && alreadyAdded.put(cell, Boolean.TRUE) == null)
					panel.add(cell);
			}
		}
	}

	/**
	 * Aligns the height of all cells in a row.
	 * 
	 * @param layout
	 * @param grid
	 * @param cols
	 * @param rows
	 * @return
	 */
	private Spring alignRows(SpringLayout layout, List<List<Component>> grid, int cols, int rows) {
		// Align all cells in each row and make them the same height.
		Spring y = Spring.constant(this.topMargin);
		int[] rowSpacing = new int[rows];
		Arrays.fill(rowSpacing, this.yPad);
		// no space above filler line
		rowSpacing[rows - 2] = 0;
		rowSpacing[rows - 1] = this.bottomMargin;
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(0);
			for (int c = 0; c < cols; c++)
				if (grid.get(r).get(c) != null)
					height = Spring.max(height, layout.getConstraints(grid.get(r).get(c)).getHeight());
			for (int c = 0; c < cols; c++)
				if (grid.get(r).get(c) != null) {
					SpringLayout.Constraints constraints = layout.getConstraints(grid.get(r).get(c));
					constraints.setY(y);
					constraints.setHeight(height);
				}
			y = Spring.sum(y, Spring.sum(height, Spring.constant(rowSpacing[r])));
		}
		return y;
	}

	/**
	 * Aligns the widths of all columns and handles the spanned components.
	 * 
	 * @param layout
	 * @param grid
	 * @param cols
	 * @param rows
	 * @param spannedComponents
	 * @return
	 */
	private Spring alignColumns(SpringLayout layout, List<List<Component>> grid, int cols, int rows,
			Map<Component, int[]> spannedComponents) {
		// Align all cells in each column and make them the same width.
		Spring x = Spring.constant(this.leftMargin);
		Spring[] colWidths = new Spring[cols], colXs = new Spring[cols];
		for (int c = 0; c < cols; c++) {
			colXs[c] = x;
			colWidths[c] = Spring.constant(0);
			for (int r = 0; r < rows; r++) {
				final Component cell = grid.get(r).get(c);
				if (cell != null && !spannedComponents.containsKey(cell))
					colWidths[c] = Spring.max(colWidths[c], layout.getConstraints(cell).getWidth());
			}

			for (int r = 0; r < rows; r++) {
				final Component cell = grid.get(r).get(c);
				if (cell != null && !spannedComponents.containsKey(cell)) {
					SpringLayout.Constraints constraints = layout.getConstraints(grid.get(r).get(c));
					constraints.setX(x);
					constraints.setWidth(colWidths[c]);
				}
			}
			x = Spring.sum(x, Spring.sum(colWidths[c], Spring.constant(c == cols - 1 ? this.rightMargin : this.xPad)));
		}
		for (Entry<Component, int[]> spans : spannedComponents.entrySet()) {
			SpringLayout.Constraints constraints = layout.getConstraints(spans.getKey());
			final int[] colRange = spans.getValue();
			constraints.setX(colXs[colRange[0]]);
			Spring width = colWidths[colRange[0]];
			for (int col = colRange[0] + 1; col <= colRange[1]; col++)
				width = Spring.sum(Spring.sum(width, colWidths[col]), Spring.constant(this.xPad));
			constraints.setWidth(width);
		}
		return x;
	}

	/**
	 * @param groups
	 * @param spanToRight
	 * @return
	 */
	private List<List<Component>> extractGrid(Container panel, Map<Component, Boolean> spanToRight) {
		Component[] groups = panel.getComponents();
		List<List<Component>> grid = new ArrayList<>();
		for (Component group : groups) {
			if (group instanceof Box) {
				// box layout as can happen in NodeDialogPanes
				Component[] rows = ((Box) group).getComponents();
				for (Component row : rows) {
					if (row instanceof JPanel) {
						addRowToGrid(((JPanel) row).getComponents(), grid, spanToRight);
					}
				}
			} else if (group instanceof JPanel) {
				// nested panel
				addRowToGrid(((JPanel) group).getComponents(), grid, spanToRight);
			} else {
				// we are already at the lowest level
				addRowToGrid(groups, grid, spanToRight);
				break;
			}
		}
		return grid;
	}

	/**
	 * @param row
	 * @param grid
	 * @param spanToRight
	 */
	private void addRowToGrid(Component[] row, List<List<Component>> grid, Map<Component, Boolean> spanToRight) {
		ArrayList<Component> children = Lists.newArrayList(row);
		Component first = children.get(0);
		if (children.size() == 1 && first instanceof JPanel &&
			((JPanel) children.get(0)).getBorder() instanceof TitledBorder) {
			// group like box
			beautify((JPanel) first);
			spanToRight.put(first, Boolean.TRUE);
		} else if (first instanceof JLabel)
			((JLabel) first).setHorizontalAlignment(SwingConstants.RIGHT);
		else
			children.add(0, null);
		grid.add(children);
	}
}

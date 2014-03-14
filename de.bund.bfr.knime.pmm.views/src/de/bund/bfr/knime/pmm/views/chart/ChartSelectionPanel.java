/*******************************************************************************
 * PMM-Lab � 2012, Federal Institute for Risk Assessment (BfR), Germany
 * 
 * PMM-Lab is a set of KNIME-Nodes and KNIME workflows running within the KNIME software plattform (http://www.knime.org.).
 * 
 * PMM-Lab � 2012, Federal Institute for Risk Assessment (BfR), Germany
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
package de.bund.bfr.knime.pmm.views.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import de.bund.bfr.knime.pmm.core.ColorAndShapeCreator;
import de.bund.bfr.knime.pmm.core.Utilities;
import de.bund.bfr.knime.pmm.core.data.TimeSeries;
import de.bund.bfr.knime.pmm.core.models.ParameterValue;
import de.bund.bfr.knime.pmm.core.ui.FormattedDoubleTextField;
import de.bund.bfr.knime.pmm.core.ui.StandardFileFilter;
import de.bund.bfr.knime.pmm.core.ui.StringTextField;
import de.bund.bfr.knime.pmm.core.ui.TimeSeriesTable;
import de.bund.bfr.knime.pmm.core.ui.UI;

public class ChartSelectionPanel extends JPanel implements ActionListener,
		CellEditorListener, ListSelectionListener {

	private static final String ID = "ID";
	private static final String SELECTED = "Selected";
	public static final String COLOR = "Color";
	public static final String SHAPE = "Shape";
	public static final String DATA = "Data Points";
	public static final String PARAMETERS = "Parameters";
	public static final String FORMULA = "Formula";

	private static final long serialVersionUID = 1L;

	private static final int MIN_COLUMN_WIDTH = 15;
	private static final int MAX_COLUMN_WIDTH = 2147483647;
	private static final int PREFERRED_COLUMN_WIDTH = 75;

	private List<SelectionListener> listeners;

	private ColorAndShapeCreator colorAndShapes;

	private List<String> ids;
	private boolean selectionExlusive;
	private List<TimeSeries> data;
	private List<String> formulas;
	private List<Map<String, ParameterValue>> parameters;
	private Map<String, List<String>> stringColumns;
	private Map<String, List<Double>> qualityColumns;
	private Map<String, List<Double>> conditionValues;
	private Map<String, List<Double>> conditionMinValues;
	private Map<String, List<Double>> conditionMaxValues;
	private Map<String, List<String>> conditionUnits;
	private Map<String, List<ParameterValue>> parameterValues;

	private Map<String, String> conditionStandardUnits;
	private Set<String> visualizationColumns;
	private Set<String> miscellaneousColumns;

	private JTable selectTable;

	private JScrollPane tableScrollPane;
	private JButton selectAllButton;
	private JButton unselectAllButton;
	private JButton invertSelectionButton;
	private JButton customizeColumnsButton;
	private JButton resizeColumnsButton;
	private JButton saveAsButton;
	private Map<String, JComboBox<String>> comboBoxes;

	private boolean hasConditionRanges;

	public ChartSelectionPanel(List<String> ids, boolean selectionsExclusive,
			Map<String, List<String>> stringColumns,
			Map<String, List<Double>> qualityColumns,
			Map<String, List<Double>> conditionValues,
			Map<String, List<Double>> conditionMinValues,
			Map<String, List<Double>> conditionMaxValues,
			Map<String, List<String>> conditionUnits,
			Map<String, List<ParameterValue>> parameterValues,
			Set<String> filterableColumns, List<TimeSeries> data,
			List<Map<String, ParameterValue>> parameters, List<String> formulas) {
		this(ids, selectionsExclusive, stringColumns, qualityColumns,
				conditionValues, conditionMinValues, conditionMaxValues,
				conditionUnits, parameterValues, filterableColumns, data,
				parameters, formulas, null);
	}

	public ChartSelectionPanel(List<String> ids, boolean selectionsExclusive,
			Map<String, List<String>> stringColumns,
			Map<String, List<Double>> qualityColumns,
			Map<String, List<Double>> conditionValues,
			Map<String, List<Double>> conditionMinValues,
			Map<String, List<Double>> conditionMaxValues,
			Map<String, List<String>> conditionUnits,
			Map<String, List<ParameterValue>> parameterValues,
			Set<String> filterableColumns, List<TimeSeries> data,
			List<Map<String, ParameterValue>> parameters,
			List<String> formulas, List<Integer> colorCounts) {
		if (stringColumns == null) {
			stringColumns = new LinkedHashMap<String, List<String>>();
		}

		if (qualityColumns == null) {
			qualityColumns = new LinkedHashMap<String, List<Double>>();
		}

		if (conditionValues == null) {
			conditionValues = new LinkedHashMap<String, List<Double>>();
		}

		if (parameterValues == null) {
			parameterValues = new LinkedHashMap<String, List<ParameterValue>>();
		}

		this.ids = ids;
		this.selectionExlusive = selectionsExclusive;
		this.data = data;
		this.formulas = formulas;
		this.parameters = parameters;
		this.stringColumns = stringColumns;
		this.qualityColumns = qualityColumns;
		this.conditionValues = conditionValues;
		this.conditionMinValues = conditionMinValues;
		this.conditionMaxValues = conditionMaxValues;
		this.conditionUnits = conditionUnits;
		this.parameterValues = parameterValues;

		conditionStandardUnits = new LinkedHashMap<String, String>();

		for (String condition : conditionUnits.keySet()) {
			String standardUnit = null;
			boolean multipleUnits = false;

			for (String unit : conditionUnits.get(condition)) {
				if (standardUnit == null) {
					standardUnit = unit;
				} else if (unit == null || unit.equals(standardUnit)) {
					// Do nothing
				} else {
					standardUnit = null;
					multipleUnits = true;
					break;
				}
			}

			if (!multipleUnits) {
				conditionStandardUnits.put(condition, standardUnit);
			} else {
				conditionStandardUnits = null;
				break;
			}
		}

		visualizationColumns = new LinkedHashSet<String>();
		visualizationColumns.add(COLOR);
		visualizationColumns.add(SHAPE);
		miscellaneousColumns = new LinkedHashSet<String>();

		if (data != null) {
			miscellaneousColumns.add(DATA);
		}

		if (formulas != null) {
			miscellaneousColumns.add(FORMULA);
		}

		if (parameters != null) {
			miscellaneousColumns.add(PARAMETERS);
		}

		miscellaneousColumns.addAll(stringColumns.keySet());

		if (conditionMinValues != null && conditionMaxValues != null) {
			hasConditionRanges = true;
		} else {
			hasConditionRanges = false;
		}

		listeners = new ArrayList<SelectionListener>();

		JPanel optionsPanel = new JPanel();

		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

		if (filterableColumns != null && !filterableColumns.isEmpty()) {
			JPanel optionsPanel1 = new JPanel();
			JPanel filterPanel = new JPanel();

			optionsPanel1.setLayout(new BoxLayout(optionsPanel1,
					BoxLayout.X_AXIS));
			filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
			filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			comboBoxes = new LinkedHashMap<String, JComboBox<String>>();

			for (String column : filterableColumns) {
				List<String> values = new ArrayList<String>();
				Set<String> valueSet = new LinkedHashSet<String>(
						stringColumns.get(column));

				valueSet.remove(null);
				values.add("");
				values.addAll(valueSet);
				Collections.sort(values);

				JComboBox<String> box = new JComboBox<String>(
						values.toArray(new String[0]));

				box.addActionListener(this);
				filterPanel.add(new JLabel(column + ":"));
				filterPanel.add(box);
				comboBoxes.put(column, box);
			}

			optionsPanel1.add(filterPanel);
			optionsPanel.add(UI.createWestPanel(optionsPanel1));
		}

		JPanel optionsPanel2 = new JPanel();

		optionsPanel2.setLayout(new BoxLayout(optionsPanel2, BoxLayout.X_AXIS));

		if (!selectionsExclusive) {
			JPanel selectPanel = new JPanel();

			selectAllButton = new JButton("All");
			selectAllButton.addActionListener(this);
			unselectAllButton = new JButton("None");
			unselectAllButton.addActionListener(this);
			invertSelectionButton = new JButton("Invert");
			invertSelectionButton.addActionListener(this);

			selectPanel.setBorder(BorderFactory.createTitledBorder("Select"));
			selectPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			selectPanel.add(selectAllButton);
			selectPanel.add(unselectAllButton);
			selectPanel.add(invertSelectionButton);
			optionsPanel2.add(selectPanel);
		}

		JPanel columnPanel = new JPanel();

		customizeColumnsButton = new JButton("Customize");
		customizeColumnsButton.addActionListener(this);
		resizeColumnsButton = new JButton("Set Optimal Width");
		resizeColumnsButton.addActionListener(this);
		columnPanel.setBorder(BorderFactory.createTitledBorder("Columns"));
		columnPanel.add(customizeColumnsButton);
		columnPanel.add(resizeColumnsButton);
		optionsPanel2.add(columnPanel);

		JPanel otherPanel = new JPanel();

		saveAsButton = new JButton("Save As");
		saveAsButton.addActionListener(this);
		otherPanel.setBorder(BorderFactory.createTitledBorder("Other"));
		otherPanel.add(saveAsButton);
		optionsPanel2.add(otherPanel);

		optionsPanel.add(UI.createWestPanel(optionsPanel2));

		SelectTableModel model;

		if (colorCounts == null) {
			colorAndShapes = new ColorAndShapeCreator(ids.size());
			model = new SelectTableModel(colorAndShapes.getColorList(),
					colorAndShapes.getShapeNameList(), false);
		} else {
			List<List<Color>> colorLists = new ArrayList<List<Color>>();
			List<List<String>> shapeLists = new ArrayList<List<String>>();

			colorAndShapes = new ColorAndShapeCreator(
					Collections.max(colorCounts));

			for (int n : colorCounts) {
				ArrayList<Color> colors = new ArrayList<Color>();
				ArrayList<String> shapes = new ArrayList<String>();

				for (int i = 0; i < n; i++) {
					colors.add(colorAndShapes.getColorList().get(i));
					shapes.add(colorAndShapes.getShapeNameList().get(i));
				}

				colorLists.add(colors);
				shapeLists.add(shapes);
			}

			model = new SelectTableModel(colorLists, shapeLists, true);
		}

		selectTable = new JTable(model);
		selectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectTable.getSelectionModel().addListSelectionListener(this);
		selectTable
				.setRowHeight((new JComboBox<String>()).getPreferredSize().height);
		selectTable.setRowSorter(new SelectTableRowSorter(model, null));
		selectTable.getColumn(ID).setMinWidth(0);
		selectTable.getColumn(ID).setMaxWidth(0);
		selectTable.getColumn(ID).setPreferredWidth(0);
		selectTable.getColumn(SELECTED).setCellEditor(new CheckBoxEditor());
		selectTable.getColumn(SELECTED).setCellRenderer(new CheckBoxRenderer());
		selectTable.getColumn(SELECTED).getCellEditor()
				.addCellEditorListener(this);

		if (colorCounts == null) {
			selectTable.getColumn(COLOR).setCellEditor(new ColorEditor());
			selectTable.getColumn(COLOR).setCellRenderer(new ColorRenderer());
			selectTable.getColumn(SHAPE).setCellEditor(
					new DefaultCellEditor(new JComboBox<String>(
							ColorAndShapeCreator.SHAPE_NAMES)));
		} else {
			selectTable.getColumn(COLOR).setCellEditor(new ColorListEditor());
			selectTable.getColumn(COLOR).setCellRenderer(
					new ColorListRenderer());
			selectTable.getColumn(SHAPE).setCellEditor(new ShapeListEditor());
			selectTable.getColumn(SHAPE).setCellRenderer(
					new ShapeListRenderer());
		}

		selectTable.getColumn(COLOR).getCellEditor()
				.addCellEditorListener(this);
		selectTable.getColumn(SHAPE).getCellEditor()
				.addCellEditorListener(this);
		selectTable.getColumn(DATA).setCellEditor(new TimeSeriesEditor());
		selectTable.getColumn(DATA).setCellRenderer(new ViewRenderer());
		selectTable.getColumn(FORMULA).setCellEditor(new FormulaEditor());
		selectTable.getColumn(FORMULA).setCellRenderer(new ViewRenderer());
		selectTable.getColumn(PARAMETERS).setCellEditor(new ParameterEditor());
		selectTable.getColumn(PARAMETERS).setCellRenderer(new ViewRenderer());
		selectTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		tableScrollPane = new JScrollPane(selectTable,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		setLayout(new BorderLayout());
		add(optionsPanel, BorderLayout.NORTH);
		add(tableScrollPane, BorderLayout.CENTER);
	}

	public String getFocusedID() {
		int row = selectTable.getSelectedRow();

		if (row != -1) {
			return (String) selectTable.getValueAt(row, 0);
		}

		return null;
	}

	public List<String> getSelectedIDs() {
		List<String> selectedIDs = new ArrayList<String>();

		for (int i = 0; i < selectTable.getRowCount(); i++) {
			if ((Boolean) selectTable.getValueAt(i, 1)) {
				selectedIDs.add((String) selectTable.getValueAt(i, 0));
			}
		}

		return selectedIDs;
	}

	public void setSelectedIDs(List<String> selectedIDs) {
		Set<String> idSet = new LinkedHashSet<String>(selectedIDs);

		for (int i = 0; i < selectTable.getRowCount(); i++) {
			if (idSet.contains(selectTable.getValueAt(i, 0))) {
				selectTable.setValueAt(true, i, 1);
			} else {
				selectTable.setValueAt(false, i, 1);
			}
		}

		fireSelectionChanged();
	}

	public void selectAllIDs() {
		for (int i = 0; i < selectTable.getRowCount(); i++) {
			selectTable.setValueAt(true, i, 1);
		}

		fireSelectionChanged();
	}

	public String getFilter(String column) {
		if (comboBoxes.containsKey(column)) {
			return (String) comboBoxes.get(column).getSelectedItem();
		} else {
			return null;
		}
	}

	public void setFilter(String column, String filter) {
		if (comboBoxes.containsKey(column)) {
			if (filter != null) {
				comboBoxes.get(column).setSelectedItem(filter);
			} else {
				comboBoxes.get(column).setSelectedItem("");
			}

			applyFilters();
		}
	}

	public Map<String, Color> getColors() {
		Map<String, Color> paints = new LinkedHashMap<String, Color>(
				selectTable.getRowCount());

		for (int i = 0; i < selectTable.getRowCount(); i++) {
			paints.put((String) selectTable.getValueAt(i, 0),
					(Color) selectTable.getValueAt(i, 2));
		}

		return paints;
	}

	public void setColors(Map<String, Color> colors) {
		for (int i = 0; i < selectTable.getRowCount(); i++) {
			Color color = colors.get(selectTable.getValueAt(i, 0));

			if (color != null) {
				selectTable.setValueAt(color, i, 2);
			}
		}
	}

	public Map<String, Shape> getShapes() {
		Map<String, Shape> shapes = new LinkedHashMap<String, Shape>(
				selectTable.getRowCount());
		Map<String, Shape> shapeMap = colorAndShapes.getShapeByNameMap();

		for (int i = 0; i < selectTable.getRowCount(); i++) {
			shapes.put((String) selectTable.getValueAt(i, 0),
					shapeMap.get(selectTable.getValueAt(i, 3)));
		}

		return shapes;
	}

	public void setShapes(Map<String, Shape> shapes) {
		Map<Shape, String> shapeMap = colorAndShapes.getNameByShapeMap();

		for (int i = 0; i < selectTable.getRowCount(); i++) {
			Shape shape = shapes.get(selectTable.getValueAt(i, 0));

			if (shape != null) {
				selectTable.setValueAt(shapeMap.get(shape), i, 3);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<Color>> getColorLists() {
		Map<String, List<Color>> paints = new LinkedHashMap<String, List<Color>>(
				selectTable.getRowCount());

		for (int i = 0; i < selectTable.getRowCount(); i++) {
			paints.put((String) selectTable.getValueAt(i, 0),
					(List<Color>) selectTable.getValueAt(i, 2));
		}

		return paints;
	}

	public void setColorLists(Map<String, List<Color>> colorLists) {
		for (int i = 0; i < selectTable.getRowCount(); i++) {
			List<Color> colorList = colorLists
					.get(selectTable.getValueAt(i, 0));

			if (colorList != null) {
				selectTable.setValueAt(colorList, i, 2);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<Shape>> getShapeLists() {
		Map<String, List<Shape>> shapes = new LinkedHashMap<String, List<Shape>>(
				selectTable.getRowCount());
		Map<String, Shape> shapeMap = colorAndShapes.getShapeByNameMap();

		for (int i = 0; i < selectTable.getRowCount(); i++) {
			List<Shape> list = new ArrayList<Shape>();

			for (String name : (List<String>) selectTable.getValueAt(i, 3)) {
				list.add(shapeMap.get(name));
			}

			shapes.put((String) selectTable.getValueAt(i, 0), list);
		}

		return shapes;
	}

	public void setShapeLists(Map<String, List<Shape>> shapeLists) {
		Map<Shape, String> shapeMap = colorAndShapes.getNameByShapeMap();

		for (int i = 0; i < selectTable.getRowCount(); i++) {
			List<Shape> shapeList = shapeLists
					.get(selectTable.getValueAt(i, 0));

			if (shapeList != null) {
				List<String> list = new ArrayList<String>();

				for (Shape shape : shapeList) {
					list.add(shapeMap.get(shape));
				}

				selectTable.setValueAt(list, i, 3);
			}
		}
	}

	public Set<String> getVisibleColumns() {
		Set<String> visibleColumns = new LinkedHashSet<String>();
		Set<String> columns = new LinkedHashSet<String>();

		columns.addAll(Arrays.asList(COLOR, SHAPE, DATA, FORMULA, PARAMETERS));
		columns.addAll(stringColumns.keySet());
		columns.addAll(qualityColumns.keySet());

		for (String column : columns) {
			if (selectTable.getColumn(column).getMaxWidth() != 0) {
				visibleColumns.add(column);
			}
		}

		for (String column : conditionValues.keySet()) {
			if (conditionStandardUnits != null) {
				String unit = conditionStandardUnits.get(column);

				if (!hasConditionRanges
						&& selectTable.getColumn(column + " (" + unit + ")")
								.getMaxWidth() != 0) {
					visibleColumns.add(column);
				} else if (hasConditionRanges
						&& selectTable.getColumn(
								"Min " + column + " (" + unit + ")")
								.getMaxWidth() != 0) {
					visibleColumns.add(column);
				}
			} else {
				if (!hasConditionRanges
						&& selectTable.getColumn(column).getMaxWidth() != 0) {
					visibleColumns.add(column);
				} else if (hasConditionRanges
						&& selectTable.getColumn("Min " + column).getMaxWidth() != 0) {
					visibleColumns.add(column);
				}
			}
		}

		for (String column : parameterValues.keySet()) {
			if (selectTable.getColumn(column).getMaxWidth() != 0) {
				visibleColumns.add(column);
			}
		}

		return visibleColumns;
	}

	public void setVisibleColumns(Set<String> visibleColumns) {
		List<String> columns = new ArrayList<String>();

		columns.addAll(Arrays.asList(COLOR, SHAPE, DATA, FORMULA, PARAMETERS));
		columns.addAll(stringColumns.keySet());
		columns.addAll(qualityColumns.keySet());

		for (String column : columns) {
			setColumnVisible(column, visibleColumns.contains(column));
		}

		if (!hasConditionRanges) {
			for (String column : conditionValues.keySet()) {
				if (conditionStandardUnits != null) {
					String unit = conditionStandardUnits.get(column);

					setColumnVisible(column + " (" + unit + ")",
							visibleColumns.contains(column));
				} else {
					setColumnVisible(column, visibleColumns.contains(column));
					setColumnVisible(column + " Unit",
							visibleColumns.contains(column));
				}
			}
		} else {
			for (String column : conditionValues.keySet()) {
				if (conditionStandardUnits != null) {
					String unit = conditionStandardUnits.get(column);

					setColumnVisible("Min " + column + " (" + unit + ")",
							visibleColumns.contains(column));
					setColumnVisible("Max " + column + " (" + unit + ")",
							visibleColumns.contains(column));
				} else {
					setColumnVisible("Min " + column,
							visibleColumns.contains(column));
					setColumnVisible("Max " + column,
							visibleColumns.contains(column));
					setColumnVisible(column + " Unit",
							visibleColumns.contains(column));
				}
			}
		}

		for (String column : parameterValues.keySet()) {
			setColumnVisible(column, visibleColumns.contains(column));
		}
	}

	public void addSelectionListener(SelectionListener listener) {
		listeners.add(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	public void fireSelectionChanged() {
		for (SelectionListener listener : listeners) {
			listener.selectionChanged();
		}
	}

	public void fireInfoSelectionChanged() {
		for (SelectionListener listener : listeners) {
			listener.focusChanged();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == selectAllButton) {
			for (int i = 0; i < selectTable.getRowCount(); i++) {
				selectTable.setValueAt(true, i, 1);
			}
		} else if (e.getSource() == unselectAllButton) {
			for (int i = 0; i < selectTable.getRowCount(); i++) {
				selectTable.setValueAt(false, i, 1);
			}
		} else if (e.getSource() == invertSelectionButton) {
			for (int i = 0; i < selectTable.getRowCount(); i++) {
				selectTable.setValueAt(!(Boolean) selectTable.getValueAt(i, 1),
						i, 1);
			}
		} else if (e.getSource() == customizeColumnsButton) {
			ColumnSelectionDialog dialog = new ColumnSelectionDialog(
					getVisibleColumns());

			dialog.setVisible(true);

			if (dialog.isApproved()) {
				setVisibleColumns(dialog.getSelection());
			}
		} else if (e.getSource() == resizeColumnsButton) {
			packColumns();
		} else if (e.getSource() == saveAsButton) {
			JFileChooser fileChooser = new JFileChooser();

			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.addChoosableFileFilter(new StandardFileFilter(".xls",
					"Excel Spreadsheat (*.xls)"));

			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				String fileName = fileChooser.getSelectedFile().getName();
				String path = fileChooser.getSelectedFile().getAbsolutePath();

				if (!fileName.toLowerCase().endsWith(".xls")) {
					path += ".xls";
				}

				try {
					Workbook wb = new HSSFWorkbook();
					Sheet sheet = wb.createSheet("Data");
					int rIndex = 0;
					int cIndex = 0;
					Row row = sheet.createRow(rIndex);

					rIndex++;

					for (int c = 2; c < selectTable.getColumnCount(); c++) {
						String columnName = selectTable.getColumnName(c);

						if (selectTable.getColumn(columnName).getMaxWidth() != 0) {
							Cell cell = row.createCell(cIndex);

							cell.setCellValue(columnName);
							cIndex++;
						}
					}

					for (int r = 0; r < selectTable.getRowCount(); r++) {
						if ((Boolean) selectTable.getValueAt(r, 1)) {
							row = sheet.createRow(rIndex);
							rIndex++;
							cIndex = 0;

							for (int c = 2; c < selectTable.getColumnCount(); c++) {
								String columnName = selectTable
										.getColumnName(c);

								if (selectTable.getColumn(columnName)
										.getMaxWidth() != 0) {
									Cell cell = row.createCell(cIndex);

									cell.setCellValue(selectTable.getValueAt(r,
											c).toString());
									cIndex++;
								}
							}
						}
					}

					FileOutputStream out = new FileOutputStream(path);

					wb.write(out);
					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} else {
			applyFilters();
		}

		fireSelectionChanged();
	}

	@Override
	public void editingStopped(ChangeEvent e) {
		fireSelectionChanged();
	}

	@Override
	public void editingCanceled(ChangeEvent e) {
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		fireInfoSelectionChanged();
	}

	private void applyFilters() {
		Map<String, String> filters = new LinkedHashMap<String, String>();

		for (String column : comboBoxes.keySet()) {
			JComboBox<String> box = comboBoxes.get(column);

			if (!box.getSelectedItem().equals("")) {
				filters.put(column, (String) box.getSelectedItem());
			}
		}

		selectTable.setRowSorter(new SelectTableRowSorter(
				(SelectTableModel) selectTable.getModel(), filters));
	}

	private void setColumnVisible(String column, boolean value) {
		if (value) {
			selectTable.getColumn(column).setMinWidth(MIN_COLUMN_WIDTH);
			selectTable.getColumn(column).setMaxWidth(MAX_COLUMN_WIDTH);
			selectTable.getColumn(column).setPreferredWidth(
					PREFERRED_COLUMN_WIDTH);
		} else {
			selectTable.getColumn(column).setMinWidth(0);
			selectTable.getColumn(column).setMaxWidth(0);
			selectTable.getColumn(column).setPreferredWidth(0);
		}
	}

	private void packColumns() {

		for (int c = 0; c < selectTable.getColumnCount(); c++) {
			TableColumn col = selectTable.getColumnModel().getColumn(c);

			if (col.getPreferredWidth() == 0) {
				continue;
			}

			TableCellRenderer renderer = col.getHeaderRenderer();
			Component comp = selectTable
					.getTableHeader()
					.getDefaultRenderer()
					.getTableCellRendererComponent(selectTable,
							col.getHeaderValue(), false, false, 0, 0);
			int width = comp.getPreferredSize().width;

			for (int r = 0; r < selectTable.getRowCount(); r++) {
				renderer = selectTable.getCellRenderer(r, c);
				comp = renderer.getTableCellRendererComponent(selectTable,
						selectTable.getValueAt(r, c), false, false, r, c);
				width = Math.max(width, comp.getPreferredSize().width);
			}

			col.setPreferredWidth(width += 5);
		}

		revalidate();
	}

	public static interface SelectionListener {

		public void selectionChanged();

		public void focusChanged();
	}

	private abstract class AbstractSelectTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private static final int FIXED_COLUMNS = 7;

		private boolean listBased;

		private List<Boolean> selections;
		private List<Color> colors;
		private List<List<Color>> colorLists;
		private List<String> shapes;
		private List<List<String>> shapeLists;

		private Map<Integer, String> stringByIndex;
		private Map<Integer, String> qualityByIndex;
		private Map<Integer, String> conditionByIndex;
		private Map<Integer, String> conditionUnitByIndex;
		private Map<Integer, String> conditionMinByIndex;
		private Map<Integer, String> conditionMaxByIndex;
		private Map<Integer, String> parameterByIndex;

		@SuppressWarnings("unchecked")
		public AbstractSelectTableModel(List<?> colors, List<?> shapes,
				boolean listBased) {
			this.listBased = listBased;

			if (!listBased) {
				this.colors = (List<Color>) colors;
				this.shapes = (List<String>) shapes;
			} else {
				this.colorLists = (List<List<Color>>) colors;
				this.shapeLists = (List<List<String>>) shapes;
			}

			selections = new ArrayList<Boolean>(Collections.nCopies(ids.size(),
					false));
			stringByIndex = new LinkedHashMap<Integer, String>();
			qualityByIndex = new LinkedHashMap<Integer, String>();
			conditionByIndex = new LinkedHashMap<Integer, String>();
			conditionUnitByIndex = new LinkedHashMap<Integer, String>();
			conditionMinByIndex = new LinkedHashMap<Integer, String>();
			conditionMaxByIndex = new LinkedHashMap<Integer, String>();
			parameterByIndex = new LinkedHashMap<Integer, String>();

			int columnIndex = FIXED_COLUMNS;

			for (String column : stringColumns.keySet()) {
				stringByIndex.put(columnIndex, column);
				columnIndex++;
			}

			for (String column : qualityColumns.keySet()) {
				qualityByIndex.put(columnIndex, column);
				columnIndex++;
			}

			for (String column : conditionValues.keySet()) {
				if (!hasConditionRanges) {
					if (conditionStandardUnits != null) {
						conditionByIndex.put(columnIndex, column);
						conditionUnitByIndex.put(columnIndex, column);
						columnIndex++;
					} else {
						conditionByIndex.put(columnIndex, column);
						columnIndex++;
						conditionUnitByIndex.put(columnIndex, column);
						columnIndex++;
					}
				} else {
					if (conditionStandardUnits != null) {
						conditionMinByIndex.put(columnIndex, column);
						conditionUnitByIndex.put(columnIndex, column);
						columnIndex++;
						conditionMaxByIndex.put(columnIndex, column);
						conditionUnitByIndex.put(columnIndex, column);
						columnIndex++;
					} else {
						conditionMinByIndex.put(columnIndex, column);
						columnIndex++;
						conditionMaxByIndex.put(columnIndex, column);
						columnIndex++;
						conditionUnitByIndex.put(columnIndex, column);
						columnIndex++;
					}
				}
			}

			for (String column : parameterValues.keySet()) {
				parameterByIndex.put(columnIndex, column);
				columnIndex++;
			}
		}

		@Override
		public int getColumnCount() {
			int conditionCount = conditionValues.size();

			if (!hasConditionRanges) {
				conditionCount *= 2;
			} else {
				conditionCount *= 3;
			}

			if (conditionStandardUnits != null) {
				conditionCount -= conditionValues.size();
			}

			return FIXED_COLUMNS + stringColumns.size() + qualityColumns.size()
					+ conditionCount + parameterValues.size();
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return ID;
			case 1:
				return SELECTED;
			case 2:
				return COLOR;
			case 3:
				return SHAPE;
			case 4:
				return DATA;
			case 5:
				return FORMULA;
			case 6:
				return PARAMETERS;
			default:
				if (stringByIndex.containsKey(column)) {
					return stringByIndex.get(column);
				} else if (qualityByIndex.containsKey(column)) {
					return qualityByIndex.get(column);
				} else if (conditionByIndex.containsKey(column)
						&& conditionUnitByIndex.containsKey(column)) {
					return conditionByIndex.get(column)
							+ " ("
							+ conditionStandardUnits.get(conditionByIndex
									.get(column)) + ")";
				} else if (conditionByIndex.containsKey(column)) {
					return conditionByIndex.get(column);
				} else if (conditionMinByIndex.containsKey(column)
						&& conditionUnitByIndex.containsKey(column)) {
					return "Min "
							+ conditionMinByIndex.get(column)
							+ " ("
							+ conditionStandardUnits.get(conditionMinByIndex
									.get(column)) + ")";
				} else if (conditionMaxByIndex.containsKey(column)
						&& conditionUnitByIndex.containsKey(column)) {
					return "Max "
							+ conditionMaxByIndex.get(column)
							+ " ("
							+ conditionStandardUnits.get(conditionMaxByIndex
									.get(column)) + ")";
				} else if (conditionMinByIndex.containsKey(column)) {
					return "Min " + conditionMinByIndex.get(column);
				} else if (conditionMaxByIndex.containsKey(column)) {
					return "Max " + conditionMaxByIndex.get(column);
				} else if (conditionUnitByIndex.containsKey(column)) {
					return conditionByIndex.get(column) + " Unit";
				} else if (parameterByIndex.containsKey(column)) {
					return parameterByIndex.get(column);
				}

				return null;
			}
		}

		@Override
		public int getRowCount() {
			return ids.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			switch (column) {
			case 0:
				return ids.get(row);
			case 1:
				return selections.get(row);
			case 2:
				if (!listBased) {
					return colors.get(row);
				} else {
					return colorLists.get(row);
				}
			case 3:
				if (!listBased) {
					return shapes.get(row);
				} else {
					return shapeLists.get(row);
				}
			case 4:
				return data != null ? data.get(row) : null;
			case 5:
				return formulas != null ? formulas.get(row) : null;
			case 6:
				return parameters != null ? parameters.get(row) : null;
			default:
				if (stringByIndex.containsKey(column)) {
					return stringColumns.get(stringByIndex.get(column))
							.get(row);
				} else if (qualityByIndex.containsKey(column)) {
					return qualityColumns.get(qualityByIndex.get(column)).get(
							row);
				} else if (conditionByIndex.containsKey(column)) {
					return conditionValues.get(conditionByIndex.get(column))
							.get(row);
				} else if (conditionMinByIndex.containsKey(column)) {
					return conditionMinValues.get(
							conditionMinByIndex.get(column)).get(row);
				} else if (conditionMaxByIndex.containsKey(column)) {
					return conditionMaxValues.get(
							conditionMaxByIndex.get(column)).get(row);
				} else if (conditionUnitByIndex.containsKey(column)) {
					return conditionUnits.get(conditionUnitByIndex.get(column))
							.get(row);
				} else if (parameterByIndex.containsKey(column)) {
					return parameterValues.get(parameterByIndex.get(column))
							.get(row).getValue();
				}

				return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int column) {
			switch (column) {
			case 0:
				return String.class;
			case 1:
				return Boolean.class;
			case 2:
				if (!listBased) {
					return Color.class;
				} else {
					return List.class;
				}
			case 3:
				if (!listBased) {
					return String.class;
				} else {
					return List.class;
				}
			case 4:
				return List.class;
			case 5:
				return String.class;
			case 6:
				return Map.class;
			default:
				if (stringByIndex.containsKey(column)) {
					return String.class;
				} else if (qualityByIndex.containsKey(column)) {
					return Double.class;
				} else if (conditionByIndex.containsKey(column)) {
					return Double.class;
				} else if (conditionMinByIndex.containsKey(column)) {
					return Double.class;
				} else if (conditionMaxByIndex.containsKey(column)) {
					return Double.class;
				} else if (conditionUnitByIndex.containsKey(column)) {
					return String.class;
				} else if (parameterByIndex.containsKey(column)) {
					return Double.class;
				}

				return null;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void setValueAt(Object value, int row, int column) {
			switch (column) {
			case 1:
				selections.set(row, (Boolean) value);
				break;
			case 2:
				if (!listBased) {
					colors.set(row, (Color) value);
				} else {
					colorLists.set(row, (List<Color>) value);
				}
				break;
			case 3:
				if (!listBased) {
					shapes.set(row, (String) value);
				} else {
					shapeLists.set(row, (List<String>) value);
				}
				break;
			default:
				// Do nothing
			}
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return column == 1 || column == 2 || column == 3 || column == 4
					|| column == 5 || column == 6;
		}
	}

	private class SelectTableModel extends AbstractSelectTableModel {

		private static final long serialVersionUID = 1L;

		public SelectTableModel(List<?> colors, List<?> shapes,
				boolean listBased) {
			super(colors, shapes, listBased);
		}

		@Override
		public void setValueAt(Object value, int row, int column) {
			super.setValueAt(value, row, column);

			if (selectionExlusive && column == 1 && value.equals(Boolean.TRUE)) {
				for (int i = 0; i < getRowCount(); i++) {
					if (i != row) {
						super.setValueAt(false, i, 1);
						fireTableCellUpdated(i, 1);
					}
				}
			}

			fireTableCellUpdated(row, column);
		}
	}

	private class ViewRenderer implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return new JButton("View");
		}
	}

	private class ColorRenderer extends JLabel implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		public ColorRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object color, boolean isSelected, boolean hasFocus, int row,
				int column) {
			setBackground((Color) color);

			return this;
		}
	}

	private class ColorEditor extends AbstractCellEditor implements
			TableCellEditor {

		private static final long serialVersionUID = 1L;

		private JButton colorButton;

		public ColorEditor() {
			colorButton = new JButton();
			colorButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					Color newColor = JColorChooser.showDialog(colorButton,
							"Choose Color", colorButton.getBackground());

					if (newColor != null) {
						colorButton.setBackground(newColor);
						stopCellEditing();
					}
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			colorButton.setBackground((Color) value);

			return colorButton;
		}

		@Override
		public Object getCellEditorValue() {
			return colorButton.getBackground();
		}

	}

	private class ColorListRenderer extends JComponent implements
			TableCellRenderer {

		private static final long serialVersionUID = 1L;

		private List<Color> colorList;

		public ColorListRenderer() {
			colorList = new ArrayList<Color>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object color, boolean isSelected, boolean hasFocus, int row,
				int column) {
			colorList = (List<Color>) color;

			return this;
		}

		@Override
		protected void paintComponent(Graphics g) {
			if (colorList.isEmpty()) {
				super.paintComponent(g);
			} else {
				double w = (double) getWidth() / (double) colorList.size();

				for (int i = 0; i < colorList.size(); i++) {
					g.setColor(colorList.get(i));
					g.fillRect((int) (i * w), 0, (int) Math.max(w, 1),
							getHeight());
				}
			}
		}
	}

	private class ColorListEditor extends AbstractCellEditor implements
			TableCellEditor {

		private static final long serialVersionUID = 1L;

		private JButton button;
		private List<Color> colorList;

		public ColorListEditor() {
			button = new JButton();
			colorList = new ArrayList<Color>();
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					ColorListDialog dialog = new ColorListDialog(colorList);

					dialog.setVisible(true);

					if (dialog.isApproved()) {
						colorList = dialog.getColorList();
						stopCellEditing();
					}
				}
			});
		}

		@SuppressWarnings("unchecked")
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			colorList = (List<Color>) value;

			return button;
		}

		@Override
		public Object getCellEditorValue() {
			return colorList;
		}
	}

	private class ShapeListRenderer extends JLabel implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		public ShapeListRenderer() {
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object color, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return this;
		}
	}

	private class ShapeListEditor extends AbstractCellEditor implements
			TableCellEditor {

		private static final long serialVersionUID = 1L;

		private JButton button;
		private List<String> shapeList;

		public ShapeListEditor() {
			button = new JButton();
			shapeList = new ArrayList<String>();
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					ShapeListDialog dialog = new ShapeListDialog(shapeList);

					dialog.setVisible(true);

					if (dialog.isApproved()) {
						shapeList = dialog.getShapeList();
						stopCellEditing();
					}
				}
			});
		}

		@SuppressWarnings("unchecked")
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			shapeList = (List<String>) value;

			return button;
		}

		@Override
		public Object getCellEditorValue() {
			return shapeList;
		}
	}

	private class TimeSeriesEditor extends AbstractCellEditor implements
			TableCellEditor, ActionListener {

		private static final long serialVersionUID = 1L;

		private JButton button;
		private TimeSeries timeSeries;

		public TimeSeriesEditor() {
			button = new JButton("View");
			button.addActionListener(this);
			timeSeries = null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			timeSeries = (TimeSeries) value;

			return button;
		}

		@Override
		public Object getCellEditorValue() {
			return timeSeries;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			TimeSeriesDialog dialog = new TimeSeriesDialog(button, timeSeries);

			dialog.setVisible(true);
		}
	}

	private class FormulaEditor extends AbstractCellEditor implements
			TableCellEditor, ActionListener {

		private static final long serialVersionUID = 1L;

		private JButton button;
		private String formula;

		public FormulaEditor() {
			button = new JButton("View");
			button.addActionListener(this);
			formula = "";
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			formula = (String) value;

			return button;
		}

		@Override
		public Object getCellEditorValue() {
			return formula;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			FormulaDialog dialog = new FormulaDialog(formula);

			dialog.setVisible(true);
		}
	}

	private class ParameterEditor extends AbstractCellEditor implements
			TableCellEditor, ActionListener {

		private static final long serialVersionUID = 1L;

		private JButton button;
		private Map<String, ParameterValue> parameters;

		public ParameterEditor() {
			button = new JButton("View");
			button.addActionListener(this);
			parameters = new LinkedHashMap<String, ParameterValue>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			parameters = (Map<String, ParameterValue>) value;

			return button;
		}

		@Override
		public Object getCellEditorValue() {
			return parameters;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ParameterDialog dialog = new ParameterDialog(parameters);

			dialog.setVisible(true);
		}
	}

	private class CheckBoxRenderer extends JCheckBox implements
			TableCellRenderer {

		private static final long serialVersionUID = -8337460338388283099L;

		public CheckBoxRenderer() {
			super();
			setHorizontalAlignment(SwingConstants.CENTER);
			setBorderPainted(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			int statusColumn = -1;

			for (int i = 0; i < table.getColumnCount(); i++) {
				if (table.getColumnName(i).equals(ChartConstants.STATUS)) {
					statusColumn = i;
					break;
				}
			}

			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else if (statusColumn != -1) {
				String statusValue = (String) table.getValueAt(row,
						statusColumn);

				if (statusValue.equals(ChartConstants.OK)) {
					setForeground(table.getForeground());
					setBackground(table.getBackground());
				} else if (statusValue.equals(ChartConstants.FAILED)) {
					setForeground(Color.RED);
					setBackground(Color.RED);
				} else if (statusValue.equals(ChartConstants.OUT_OF_LIMITS)) {
					setForeground(Color.YELLOW);
					setBackground(Color.YELLOW);
				} else if (statusValue.equals(ChartConstants.NO_COVARIANCE)) {
					setForeground(Color.YELLOW);
					setBackground(Color.YELLOW);
				} else if (statusValue.equals(ChartConstants.NOT_SIGNIFICANT)) {
					setForeground(Color.YELLOW);
					setBackground(Color.YELLOW);
				}
			} else {
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}

			setSelected((value != null && ((Boolean) value).booleanValue()));

			if (hasFocus) {
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			} else {
				setBorder(new EmptyBorder(1, 1, 1, 1));
			}

			return this;
		}
	}

	private class CheckBoxEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 1L;

		public CheckBoxEditor() {
			super(new JCheckBox());
			((JCheckBox) getComponent())
					.setHorizontalAlignment(SwingConstants.CENTER);
		}
	}

	private class SelectTableRowSorter extends TableRowSorter<SelectTableModel> {

		private Map<Integer, String> filters;

		public SelectTableRowSorter(SelectTableModel model,
				Map<String, String> filters) {
			super(model);
			this.filters = new LinkedHashMap<Integer, String>();

			if (filters != null) {
				for (String column : filters.keySet()) {
					for (int i = 0; i < model.getColumnCount(); i++) {
						if (column.equals(model.getColumnName(i))) {
							this.filters.put(i, filters.get(column));
						}
					}
				}

				addFilters();
			}
		}

		@Override
		public void toggleSortOrder(int column) {
			List<? extends SortKey> sortKeys = getSortKeys();

			if (sortKeys.size() > 0) {
				if (sortKeys.get(0).getColumn() == column
						&& sortKeys.get(0).getSortOrder() == SortOrder.DESCENDING) {
					setSortKeys(null);
					return;
				}
			}

			super.toggleSortOrder(column);
		}

		private void addFilters() {
			setRowFilter(new RowFilter<SelectTableModel, Object>() {

				@Override
				public boolean include(
						javax.swing.RowFilter.Entry<? extends SelectTableModel, ? extends Object> entry) {
					for (int column : filters.keySet()) {
						if (!entry.getStringValue(column).equals(
								filters.get(column))) {
							return false;
						}
					}

					return true;
				}
			});
		}
	}

	private class ColorListDialog extends JDialog implements ActionListener {

		private static final long serialVersionUID = 1L;

		private boolean approved;
		private List<Color> colorList;

		private List<JButton> colorButtons;

		private JButton okButton;
		private JButton cancelButton;

		public ColorListDialog(List<Color> initialColors) {
			super(SwingUtilities.getWindowAncestor(ChartSelectionPanel.this),
					"Color Palette", DEFAULT_MODALITY_TYPE);

			approved = false;
			colorList = null;

			colorButtons = new ArrayList<JButton>();
			okButton = new JButton("OK");
			okButton.addActionListener(this);
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(this);

			JPanel centerPanel = new JPanel();
			JPanel bottomPanel = new JPanel();

			centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			centerPanel
					.setLayout(new GridLayout(initialColors.size(), 1, 5, 5));

			for (Color color : initialColors) {
				JButton button = new JButton();

				button.setBackground(color);
				button.setPreferredSize(new Dimension(
						button.getPreferredSize().width, 20));
				button.addActionListener(this);
				colorButtons.add(button);
				centerPanel.add(button);
			}

			bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			bottomPanel.add(okButton);
			bottomPanel.add(cancelButton);

			JPanel scrollPanel = new JPanel();

			scrollPanel.setLayout(new BorderLayout());
			scrollPanel.add(centerPanel, BorderLayout.NORTH);

			setLayout(new BorderLayout());
			add(new JScrollPane(scrollPanel), BorderLayout.CENTER);
			add(bottomPanel, BorderLayout.SOUTH);
			pack();

			setLocationRelativeTo(ChartSelectionPanel.this);
			UI.adjustDialog(this);
		}

		public boolean isApproved() {
			return approved;
		}

		public List<Color> getColorList() {
			return colorList;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == okButton) {
				approved = true;
				colorList = new ArrayList<Color>();

				for (JButton button : colorButtons) {
					colorList.add(button.getBackground());
				}

				dispose();
			} else if (e.getSource() == cancelButton) {
				dispose();
			} else {
				JButton button = (JButton) e.getSource();
				Color newColor = JColorChooser.showDialog(button,
						"Choose Color", button.getBackground());

				if (newColor != null) {
					button.setBackground(newColor);
				}
			}
		}
	}

	private class ShapeListDialog extends JDialog implements ActionListener {

		private static final long serialVersionUID = 1L;

		private boolean approved;
		private List<String> shapeList;

		private List<JComboBox<String>> shapeBoxes;

		private JButton okButton;
		private JButton cancelButton;

		public ShapeListDialog(List<String> initialShapes) {
			super(SwingUtilities.getWindowAncestor(ChartSelectionPanel.this),
					"Shape Palette", DEFAULT_MODALITY_TYPE);

			approved = false;
			shapeList = null;

			shapeBoxes = new ArrayList<JComboBox<String>>();
			okButton = new JButton("OK");
			okButton.addActionListener(this);
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(this);

			JPanel centerPanel = new JPanel();
			JPanel bottomPanel = new JPanel();

			centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			centerPanel
					.setLayout(new GridLayout(initialShapes.size(), 1, 5, 5));

			for (String shape : initialShapes) {
				JComboBox<String> box = new JComboBox<String>(
						ColorAndShapeCreator.SHAPE_NAMES);

				box.setSelectedItem(shape);
				shapeBoxes.add(box);
				centerPanel.add(box);
			}

			bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			bottomPanel.add(okButton);
			bottomPanel.add(cancelButton);

			JPanel scrollPanel = new JPanel();

			scrollPanel.setLayout(new BorderLayout());
			scrollPanel.add(centerPanel, BorderLayout.NORTH);

			setLayout(new BorderLayout());
			add(new JScrollPane(scrollPanel), BorderLayout.CENTER);
			add(bottomPanel, BorderLayout.SOUTH);
			pack();

			setLocationRelativeTo(ChartSelectionPanel.this);
			UI.adjustDialog(this);
		}

		public boolean isApproved() {
			return approved;
		}

		public List<String> getShapeList() {
			return shapeList;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == okButton) {
				approved = true;
				shapeList = new ArrayList<String>();

				for (JComboBox<String> box : shapeBoxes) {
					shapeList.add((String) box.getSelectedItem());
				}

				dispose();
			} else if (e.getSource() == cancelButton) {
				dispose();
			}
		}
	}

	private class FormulaDialog extends JDialog implements ActionListener {

		private static final long serialVersionUID = 1L;

		private JButton okButton;

		public FormulaDialog(String formula) {
			super(SwingUtilities.getWindowAncestor(ChartSelectionPanel.this),
					"Formula", DEFAULT_MODALITY_TYPE);

			okButton = new JButton("OK");
			okButton.addActionListener(this);

			StringTextField field = new StringTextField(true);

			field.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			field.setValue(formula);
			field.setEditable(false);
			field.setPreferredSize(new Dimension(
					field.getPreferredSize().width + 10, field
							.getPreferredSize().height));

			JPanel mainPanel = new JPanel();

			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(field, BorderLayout.NORTH);

			JPanel bottomPanel = new JPanel();

			bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			bottomPanel.add(okButton);

			setLayout(new BorderLayout());
			add(new JScrollPane(mainPanel), BorderLayout.CENTER);
			add(bottomPanel, BorderLayout.SOUTH);
			pack();

			setLocationRelativeTo(ChartSelectionPanel.this);
			UI.adjustDialog(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

	private class ParameterDialog extends JDialog implements ActionListener {

		private static final long serialVersionUID = 1L;

		private JButton okButton;

		public ParameterDialog(Map<String, ParameterValue> parameters) {
			super(SwingUtilities.getWindowAncestor(ChartSelectionPanel.this),
					"Parameters", DEFAULT_MODALITY_TYPE);

			okButton = new JButton("OK");
			okButton.addActionListener(this);

			JPanel leftPanel = new JPanel();
			JPanel rightPanel = new JPanel();

			leftPanel.setLayout(new GridLayout(parameters.size() * 4, 1, 5, 5));
			rightPanel
					.setLayout(new GridLayout(parameters.size() * 4, 1, 5, 5));

			for (String param : parameters.keySet()) {
				ParameterValue paramValue = parameters.get(param);
				FormattedDoubleTextField valueField = new FormattedDoubleTextField(
						true);
				FormattedDoubleTextField errorField = new FormattedDoubleTextField(
						true);
				FormattedDoubleTextField tField = new FormattedDoubleTextField(
						true);
				FormattedDoubleTextField pField = new FormattedDoubleTextField(
						true);

				valueField.setValue(paramValue.getValue());
				valueField.setEditable(false);
				errorField.setValue(paramValue.getError());
				errorField.setEditable(false);
				tField.setValue(paramValue.getT());
				tField.setEditable(false);
				pField.setValue(paramValue.getP());
				pField.setEditable(false);

				leftPanel.add(new JLabel(param + ":"));
				leftPanel.add(new JLabel(Utilities.getErrorName(param) + ":"));
				leftPanel.add(new JLabel(Utilities.getTName(param) + ":"));
				leftPanel.add(new JLabel(Utilities.getPName(param) + ":"));
				rightPanel.add(valueField);
				rightPanel.add(errorField);
				rightPanel.add(tField);
				rightPanel.add(pField);
			}

			JPanel panel = new JPanel();

			panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			panel.setLayout(new BorderLayout(5, 5));
			panel.add(leftPanel, BorderLayout.WEST);
			panel.add(rightPanel, BorderLayout.CENTER);

			JPanel mainPanel = new JPanel();

			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(panel, BorderLayout.NORTH);

			JPanel bottomPanel = new JPanel();

			bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			bottomPanel.add(okButton);

			setLayout(new BorderLayout());
			add(new JScrollPane(mainPanel), BorderLayout.CENTER);
			add(bottomPanel, BorderLayout.SOUTH);
			pack();

			setLocationRelativeTo(ChartSelectionPanel.this);
			UI.adjustDialog(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

	private class ColumnSelectionDialog extends JDialog implements
			ActionListener {

		private static final long serialVersionUID = 1L;

		private boolean approved;
		private Set<String> selection;
		private Map<String, JCheckBox> selectionBoxes;
		private JCheckBox visualizationBox;
		private JCheckBox miscellaneousBox;
		private JCheckBox qualityBox;
		private JCheckBox conditionsBox;
		private JCheckBox parametersBox;

		private JButton okButton;
		private JButton cancelButton;

		public ColumnSelectionDialog(Set<String> initialSelection) {
			super(SwingUtilities.getWindowAncestor(ChartSelectionPanel.this),
					"Column Selection", DEFAULT_MODALITY_TYPE);
			approved = false;
			selection = null;
			selectionBoxes = new LinkedHashMap<String, JCheckBox>();

			JPanel visualizationPanel = new JPanel();

			visualizationPanel.setLayout(new GridLayout(visualizationColumns
					.size() + 1, 1, 5, 5));
			visualizationBox = new JCheckBox("All");
			visualizationBox.addActionListener(this);
			visualizationPanel.add(visualizationBox);

			for (String column : visualizationColumns) {
				JCheckBox box = new JCheckBox(column);

				if (initialSelection.contains(column)) {
					box.setSelected(true);
				} else {
					box.setSelected(false);
				}

				box.addActionListener(this);
				selectionBoxes.put(column, box);
				visualizationPanel.add(box);
			}

			JPanel miscellaneousPanel = new JPanel();

			miscellaneousPanel.setLayout(new GridLayout(miscellaneousColumns
					.size() + 1, 1, 5, 5));
			miscellaneousBox = new JCheckBox("All");
			miscellaneousBox.addActionListener(this);
			miscellaneousPanel.add(miscellaneousBox);

			for (String column : miscellaneousColumns) {
				JCheckBox box = new JCheckBox(column);

				if (initialSelection.contains(column)) {
					box.setSelected(true);
				} else {
					box.setSelected(false);
				}

				box.addActionListener(this);
				selectionBoxes.put(column, box);
				miscellaneousPanel.add(box);
			}

			JPanel qualityPanel = new JPanel();

			qualityPanel.setLayout(new GridLayout(qualityColumns.size() + 1, 1,
					5, 5));
			qualityBox = new JCheckBox("All");
			qualityBox.addActionListener(this);
			qualityPanel.add(qualityBox);

			for (String column : qualityColumns.keySet()) {
				JCheckBox box = new JCheckBox(column);

				if (initialSelection.contains(column)) {
					box.setSelected(true);
				} else {
					box.setSelected(false);
				}

				box.addActionListener(this);
				selectionBoxes.put(column, box);
				qualityPanel.add(box);
			}

			JPanel conditionsPanel = new JPanel();

			conditionsPanel.setLayout(new GridLayout(
					conditionValues.size() + 1, 1, 5, 5));
			conditionsBox = new JCheckBox("All");
			conditionsBox.addActionListener(this);
			conditionsPanel.add(conditionsBox);

			for (String column : conditionValues.keySet()) {
				JCheckBox box = new JCheckBox(column);

				if (initialSelection.contains(column)) {
					box.setSelected(true);
				} else {
					box.setSelected(false);
				}

				box.addActionListener(this);
				selectionBoxes.put(column, box);
				conditionsPanel.add(box);
			}

			JPanel parameterPanel = new JPanel();

			parameterPanel.setLayout(new GridLayout(parameterValues.size() + 1,
					1, 5, 5));
			parametersBox = new JCheckBox("All");
			parametersBox.addActionListener(this);
			parameterPanel.add(parametersBox);

			for (String column : parameterValues.keySet()) {
				JCheckBox box = new JCheckBox(column);

				if (initialSelection.contains(column)) {
					box.setSelected(true);
				} else {
					box.setSelected(false);
				}

				box.addActionListener(this);
				selectionBoxes.put(column, box);
				parameterPanel.add(box);
			}

			updateCheckBoxes();

			JPanel centerPanel = new JPanel();

			centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));

			centerPanel.add(createNorthPanel(visualizationPanel,
					"Visualization"));

			if (!miscellaneousColumns.isEmpty()) {
				centerPanel.add(createNorthPanel(miscellaneousPanel,
						"Miscellaneous"));
			}

			if (!qualityColumns.isEmpty()) {
				centerPanel.add(createNorthPanel(qualityPanel,
						"Quality Criteria"));
			}

			if (!conditionValues.isEmpty()) {
				centerPanel
						.add(createNorthPanel(conditionsPanel, "Conditions"));
			}

			if (!parameterValues.isEmpty()) {
				centerPanel.add(createNorthPanel(parameterPanel, "Parameters"));
			}

			okButton = new JButton("OK");
			okButton.addActionListener(this);
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(this);

			JPanel bottomPanel = new JPanel();

			bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			bottomPanel.add(okButton);
			bottomPanel.add(cancelButton);

			setLayout(new BorderLayout());
			add(centerPanel, BorderLayout.CENTER);
			add(bottomPanel, BorderLayout.SOUTH);
			pack();

			setLocationRelativeTo(ChartSelectionPanel.this);
			UI.adjustDialog(this);
		}

		public boolean isApproved() {
			return approved;
		}

		public Set<String> getSelection() {
			return selection;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == okButton) {
				approved = true;
				selection = new LinkedHashSet<String>();

				for (String column : selectionBoxes.keySet()) {
					if (selectionBoxes.get(column).isSelected()) {
						selection.add(column);
					}
				}

				dispose();
			} else if (e.getSource() == cancelButton) {
				dispose();
			} else if (e.getSource() == visualizationBox) {
				if (visualizationBox.isSelected()) {
					setColumnsTo(visualizationColumns, true);
				} else {
					setColumnsTo(visualizationColumns, false);
				}
			} else if (e.getSource() == miscellaneousBox) {
				if (miscellaneousBox.isSelected()) {
					setColumnsTo(miscellaneousColumns, true);
				} else {
					setColumnsTo(miscellaneousColumns, false);
				}
			} else if (e.getSource() == qualityBox) {
				if (qualityBox.isSelected()) {
					setColumnsTo(qualityColumns.keySet(), true);
				} else {
					setColumnsTo(qualityColumns.keySet(), false);
				}
			} else if (e.getSource() == conditionsBox) {
				if (conditionsBox.isSelected()) {
					setColumnsTo(conditionValues.keySet(), true);
				} else {
					setColumnsTo(conditionValues.keySet(), false);
				}
			} else if (e.getSource() == parametersBox) {
				if (parametersBox.isSelected()) {
					setColumnsTo(parameterValues.keySet(), true);
				} else {
					setColumnsTo(parameterValues.keySet(), false);
				}
			} else {
				updateCheckBoxes();
			}
		}

		private JComponent createNorthPanel(JComponent comp, String name) {
			JPanel panel = UI.createNorthPanel(comp);

			panel.setBorder(BorderFactory.createTitledBorder(name));
			panel.setPreferredSize(new Dimension(Math.max(
					panel.getPreferredSize().width, 100), panel
					.getPreferredSize().height));

			return new JScrollPane(panel,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		}

		private void updateCheckBoxes() {
			boolean allVisualizationSelected = true;
			boolean allMiscellaneousSelected = true;
			boolean allQualitySelected = true;
			boolean allConditionsSelected = true;
			boolean allParametersSelected = true;

			for (String column : visualizationColumns) {
				if (!selectionBoxes.get(column).isSelected()) {
					allVisualizationSelected = false;
					break;
				}
			}

			for (String column : miscellaneousColumns) {
				if (!selectionBoxes.get(column).isSelected()) {
					allMiscellaneousSelected = false;
					break;
				}
			}

			for (String column : qualityColumns.keySet()) {
				if (!selectionBoxes.get(column).isSelected()) {
					allQualitySelected = false;
					break;
				}
			}

			for (String column : conditionValues.keySet()) {
				if (!selectionBoxes.get(column).isSelected()) {
					allConditionsSelected = false;
					break;
				}
			}

			for (String column : parameterValues.keySet()) {
				if (!selectionBoxes.get(column).isSelected()) {
					allParametersSelected = false;
					break;
				}
			}

			if (allVisualizationSelected) {
				visualizationBox.setSelected(true);
			} else {
				visualizationBox.setSelected(false);
			}

			if (allMiscellaneousSelected) {
				miscellaneousBox.setSelected(true);
			} else {
				miscellaneousBox.setSelected(false);
			}

			if (allQualitySelected) {
				qualityBox.setSelected(true);
			} else {
				qualityBox.setSelected(false);
			}

			if (allConditionsSelected) {
				conditionsBox.setSelected(true);
			} else {
				conditionsBox.setSelected(false);
			}

			if (allParametersSelected) {
				parametersBox.setSelected(true);
			} else {
				parametersBox.setSelected(false);
			}
		}

		private void setColumnsTo(Set<String> columns, boolean value) {
			for (String column : columns) {
				selectionBoxes.get(column).setSelected(value);
			}
		}
	}

	private class TimeSeriesDialog extends JDialog implements ActionListener {

		private static final long serialVersionUID = 1L;

		public TimeSeriesDialog(JComponent owner, TimeSeries timeSeries) {
			super(SwingUtilities.getWindowAncestor(owner), "Data Points",
					DEFAULT_MODALITY_TYPE);

			JButton okButton = new JButton("OK");
			JPanel bottomPanel = new JPanel();

			okButton.addActionListener(this);
			bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			bottomPanel.add(okButton);

			setLayout(new BorderLayout());
			add(new JScrollPane(new TimeSeriesTable(timeSeries)),
					BorderLayout.CENTER);
			add(bottomPanel, BorderLayout.SOUTH);
			pack();

			setResizable(true);
			setLocationRelativeTo(owner);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}

	}

}

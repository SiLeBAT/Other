package de.bund.bfr.knime.pmm.io.xls.datareader;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

import de.bund.bfr.knime.pmm.core.Utilities;
import de.bund.bfr.knime.pmm.core.common.QuantityType;
import de.bund.bfr.knime.pmm.core.common.Unit;
import de.bund.bfr.knime.pmm.core.data.ConditionParameter;
import de.bund.bfr.knime.pmm.core.data.Matrix;
import de.bund.bfr.knime.pmm.core.data.Organism;
import de.bund.bfr.knime.pmm.core.ui.FilePanel;
import de.bund.bfr.knime.pmm.core.ui.StandardFileFilter;
import de.bund.bfr.knime.pmm.io.DefaultDB;
import de.bund.bfr.knime.pmm.io.xls.XlsReader;

/**
 * <code>NodeDialog</code> for the "XlsDataReader" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christian Thoens
 */
public class XlsDataReaderNodeDialog extends NodeDialogPane implements
		ItemListener, FilePanel.FileListener {

	private static final String DO_NOT_USE = "Do Not Use";
	private static final String OTHER_PARAMETER = "Select Other";

	private XlsDataReaderSettings set;
	private XlsReader xlsReader;

	private JPanel mainPanel;

	private FilePanel filePanel;
	private JComboBox<String> sheetBox;
	private List<String> fileSheetList;
	private List<String> fileColumnList;

	private JPanel organismPanel;
	private JComboBox<String> organismBox;
	private JComboBox<String> organismButton;
	private Map<String, JComboBox<String>> organismButtons;

	private JPanel matrixPanel;
	private JComboBox<String> matrixBox;
	private JComboBox<String> matrixButton;
	private Map<String, JComboBox<String>> matrixButtons;

	private JPanel columnsPanel;
	private Map<String, JComboBox<String>> columnBoxes;
	private Map<String, JComboBox<String>> columnUnitBoxes;

	private JLabel noLabel;

	/**
	 * New pane for configuring the XlsDataReader node.
	 */
	protected XlsDataReaderNodeDialog() {
		xlsReader = new XlsReader();

		filePanel = new FilePanel("XLS File", FilePanel.OPEN_DIALOG);
		filePanel.setAcceptAllFiles(false);
		filePanel.addFileFilter(new StandardFileFilter(".xls",
				"Excel Spreadsheat (*.xls)"));
		filePanel.addFileListener(this);
		sheetBox = new JComboBox<String>();
		sheetBox.addItemListener(this);
		fileSheetList = new ArrayList<String>();
		fileColumnList = new ArrayList<String>();

		noLabel = new JLabel();
		noLabel.setPreferredSize(new Dimension(100, 50));
		organismPanel = new JPanel();
		organismPanel.setBorder(BorderFactory
				.createTitledBorder(Utilities.ORGANISM));
		organismPanel.setLayout(new BorderLayout());
		organismPanel.add(noLabel, BorderLayout.CENTER);
		organismButtons = new LinkedHashMap<String, JComboBox<String>>();
		matrixPanel = new JPanel();
		matrixPanel.setBorder(BorderFactory
				.createTitledBorder(Utilities.MATRIX));
		matrixPanel.setLayout(new BorderLayout());
		matrixPanel.add(noLabel, BorderLayout.CENTER);
		matrixButtons = new LinkedHashMap<String, JComboBox<String>>();
		columnsPanel = new JPanel();
		columnsPanel.setBorder(BorderFactory
				.createTitledBorder("XLS Column -> PMM-Lab assignments"));
		columnsPanel.setLayout(new BorderLayout());
		columnsPanel.add(noLabel, BorderLayout.CENTER);
		columnBoxes = new LinkedHashMap<String, JComboBox<String>>();
		columnUnitBoxes = new LinkedHashMap<String, JComboBox<String>>();

		JPanel optionsPanel = new JPanel();

		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
		optionsPanel.add(organismPanel);
		optionsPanel.add(matrixPanel);
		optionsPanel.add(columnsPanel);

		JPanel sheetPanel = new JPanel();

		sheetPanel.setBorder(BorderFactory.createTitledBorder("Sheet"));
		sheetPanel.setLayout(new BorderLayout());
		sheetPanel.add(sheetBox, BorderLayout.NORTH);

		JPanel fileSheetPanel = new JPanel();

		fileSheetPanel.setLayout(new BorderLayout());
		fileSheetPanel.add(filePanel, BorderLayout.CENTER);
		fileSheetPanel.add(sheetPanel, BorderLayout.EAST);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(fileSheetPanel, BorderLayout.NORTH);
		mainPanel.add(optionsPanel, BorderLayout.CENTER);

		addTab("Options", mainPanel);
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings,
			DataTableSpec[] specs) throws NotConfigurableException {
		set = new XlsDataReaderSettings();
		set.load(settings);

		filePanel.removeFileListener(this);
		filePanel.setFileName(set.getFileName());
		filePanel.addFileListener(this);

		try {
			xlsReader.setFile(set.getFileName());
			fileSheetList = xlsReader.getSheets();
		} catch (Exception e) {
			fileSheetList = new ArrayList<String>();
		}

		sheetBox.removeItemListener(this);
		sheetBox.removeAllItems();

		for (String sheet : fileSheetList) {
			sheetBox.addItem(sheet);
		}

		sheetBox.setSelectedItem(set.getSheetName());
		sheetBox.addItemListener(this);

		try {
			xlsReader.setSheet(set.getSheetName());
			fileColumnList = xlsReader.getColumns();
		} catch (Exception e) {
			fileColumnList = new ArrayList<String>();
		}

		if (set.getOrganismColumn() == null) {
			if (set.getOrganism() != null) {
				set.setOrganismColumn(OTHER_PARAMETER);
			} else {
				set.setOrganismColumn(DO_NOT_USE);
			}
		}

		if (set.getMatrixColumn() == null) {
			if (set.getMatrix() != null) {
				set.setMatrixColumn(OTHER_PARAMETER);
			} else {
				set.setMatrixColumn(DO_NOT_USE);
			}
		}

		updateAgentPanel();
		updateMatrixPanel();
		updateColumnsPanel();
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		cleanMaps();

		if (filePanel.getFileName() == null) {
			throw new InvalidSettingsException("No file is specfied");
		}

		if (sheetBox.getSelectedItem() == null) {
			throw new InvalidSettingsException("No sheet is selected");
		}

		if (fileColumnList.isEmpty()) {
			throw new InvalidSettingsException("Specified file is invalid");
		}

		if (set.getOrganismColumn() != null
				&& set.getOrganismColumn().equals(OTHER_PARAMETER)
				&& set.getOrganism() == null) {
			throw new InvalidSettingsException("No assignment for "
					+ Utilities.ORGANISM);
		}

		if (set.getMatrixColumn() != null
				&& set.getMatrixColumn().equals(OTHER_PARAMETER)
				&& set.getMatrix() == null) {
			throw new InvalidSettingsException("No assignment for "
					+ Utilities.MATRIX);
		}

		boolean idColumnMissing = true;
		Set<Object> assignments = new LinkedHashSet<Object>();

		for (String column : set.getColumnMappings().keySet()) {
			String assignment = set.getColumnMappings().get(column);

			if (assignment == null) {
				throw new InvalidSettingsException("Column \"" + column
						+ "\" has no assignment");
			} else if (assignment.equals(XlsReader.ID_COLUMN)) {
				idColumnMissing = false;
			} else if (assignment.equals(XlsReader.CONDITION_COLUMN)) {
				assignment = set.getConditions().get(column).getName();
			}

			if (!assignments.add(assignment)) {
				throw new InvalidSettingsException("\"" + assignments
						+ "\" can only be assigned once");
			}
		}

		if (idColumnMissing) {
			throw new InvalidSettingsException("\"" + XlsReader.ID_COLUMN
					+ "\" is unassigned");
		}

		if (set.getOrganismColumn() != null
				&& !set.getOrganismColumn().equals(OTHER_PARAMETER)) {
			set.setOrganism(null);
		}

		if (set.getMatrixColumn() != null
				&& !set.getMatrixColumn().equals(OTHER_PARAMETER)) {
			set.setMatrix(null);
		}

		if (set.getOrganismColumn() != null
				&& (set.getOrganismColumn().equals(OTHER_PARAMETER) || set
						.getOrganismColumn().equals(DO_NOT_USE))) {
			set.setOrganismColumn(null);
		}

		if (set.getMatrixColumn() != null
				&& (set.getMatrixColumn().equals(OTHER_PARAMETER) || set
						.getMatrixColumn().equals(DO_NOT_USE))) {
			set.setMatrixColumn(null);
		}

		set.save(settings);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() != ItemEvent.SELECTED) {
			return;
		}

		if (e.getSource() == organismButton) {
			for (Organism organism : DefaultDB.getInstance().getOrganisms()) {
				if (organism.getName().equals(organismButton.getSelectedItem())) {
					set.setOrganism(organism);
					break;
				}
			}
		} else if (e.getSource() == matrixButton) {
			for (Matrix matrix : DefaultDB.getInstance().getMatrices()) {
				if (matrix.getName().equals(matrixButton.getSelectedItem())) {
					set.setMatrix(matrix);
					break;
				}
			}
		} else if (e.getSource() == sheetBox) {
			try {
				fileColumnList = xlsReader.getColumns();
			} catch (Exception ex) {
				fileColumnList = new ArrayList<String>();
			}

			updateColumnsPanel();
			updateAgentPanel();
			updateMatrixPanel();
			mainPanel.revalidate();
		} else if (e.getSource() == organismBox) {
			set.setOrganismColumn((String) organismBox.getSelectedItem());
			updateAgentPanel();
		} else if (e.getSource() == matrixBox) {
			set.setMatrixColumn((String) matrixBox.getSelectedItem());
			updateMatrixPanel();
		} else {
			for (String column : columnBoxes.keySet()) {
				if (e.getSource() == columnBoxes.get(column)) {
					String selected = (String) columnBoxes.get(column)
							.getSelectedItem();

					if (selected.equals(XlsReader.ID_COLUMN)
							|| selected.equals(XlsReader.TIME_COLUMN)
							|| selected.equals(XlsReader.CONCENTRATION_COLUMN)) {
						set.getColumnMappings().put(column, selected);
					} else if (selected.equals(DO_NOT_USE)) {
						set.getColumnMappings().remove(column);
					} else {
						for (ConditionParameter param : DefaultDB.getInstance()
								.getConditionParameters()) {
							if (param.getName().equals(selected)) {
								set.getColumnMappings().put(column,
										XlsReader.CONDITION_COLUMN);
								set.getConditions().put(column, param);
								break;
							}
						}
					}

					updateColumnsPanel();
					break;
				}
			}

			for (String column : columnUnitBoxes.keySet()) {
				if (e.getSource() == columnUnitBoxes.get(column)) {
					String unitName = (String) columnUnitBoxes.get(column)
							.getSelectedItem();

					if (set.getColumnMappings().containsKey(column)) {
						String mapping = set.getColumnMappings().get(column);

						if (mapping.equals(XlsReader.TIME_COLUMN)) {
							for (Unit unit : DefaultDB.getInstance()
									.getTimeQuantity().getUnits()) {
								if (unit.getName().equals(unitName)) {
									set.setTimeUnit(unit);
									break;
								}
							}
						} else if (mapping
								.equals(XlsReader.CONCENTRATION_COLUMN)) {
							for (QuantityType quantity : DefaultDB
									.getInstance().getConcentrationQuantities()) {
								boolean found = false;

								for (Unit unit : quantity.getUnits()) {
									if (unit.getName().equals(unitName)) {
										set.setConcentrationUnit(unit);
										found = true;
										break;
									}
								}

								if (found) {
									break;
								}
							}
						} else if (mapping.equals(XlsReader.CONDITION_COLUMN)) {
							ConditionParameter condition = set.getConditions()
									.get(column);

							for (QuantityType quantity : condition
									.getQuantityTypes()) {
								boolean found = false;

								for (Unit unit : quantity.getUnits()) {
									if (unit.getName().equals(unitName)) {
										set.getConditionUnits().put(column,
												unit);
										found = true;
										break;
									}
								}

								if (found) {
									break;
								}
							}
						}
					}

					break;
				}
			}

			for (String value : organismButtons.keySet()) {
				if (e.getSource() == organismButtons.get(value)) {
					String organismName = (String) organismButtons.get(value)
							.getSelectedItem();

					for (Organism organism : DefaultDB.getInstance()
							.getOrganisms()) {
						if (organism.getName().equals(organismName)) {
							set.getOrganismMappings().put(value, organism);
							break;
						}
					}

					break;
				}
			}

			for (String value : matrixButtons.keySet()) {
				if (e.getSource() == matrixButtons.get(value)) {
					String matrixName = (String) matrixButtons.get(value)
							.getSelectedItem();

					for (Matrix matrix : DefaultDB.getInstance().getMatrices()) {
						if (matrix.getName().equals(matrixName)) {
							set.getMatrixMappings().put(value, matrix);
							break;
						}
					}

					break;
				}
			}
		}
	}

	@Override
	public void fileChanged(FilePanel source) {
		set.setFileName(filePanel.getFileName());

		try {
			xlsReader.setFile(set.getFileName());
			fileSheetList = xlsReader.getSheets();
		} catch (Exception e) {
			fileSheetList = new ArrayList<String>();
		}

		sheetBox.removeItemListener(this);
		sheetBox.removeAllItems();

		for (String sheet : fileSheetList) {
			sheetBox.addItem(sheet);
		}

		if (!fileSheetList.isEmpty()) {
			sheetBox.setSelectedIndex(0);
		}

		set.setSheetName((String) sheetBox.getSelectedItem());
		sheetBox.addItemListener(this);

		try {
			xlsReader.setSheet(set.getSheetName());
			fileColumnList = xlsReader.getColumns();
		} catch (Exception e) {
			fileColumnList = new ArrayList<String>();
		}

		updateColumnsPanel();
		updateAgentPanel();
		updateMatrixPanel();
		mainPanel.revalidate();
	}

	private void updateAgentPanel() {
		List<String> organismNames = new ArrayList<String>();

		for (Organism organism : DefaultDB.getInstance().getOrganisms()) {
			organismNames.add(organism.getName());
		}

		organismButtons.clear();
		organismBox = new JComboBox<String>(new String[] { DO_NOT_USE,
				OTHER_PARAMETER });
		organismButton = new JComboBox<String>(
				organismNames.toArray(new String[0]));

		for (String column : fileColumnList) {
			organismBox.addItem(column);
		}

		organismBox.setSelectedItem(set.getOrganismColumn());

		if (set.getOrganism() != null) {
			organismButton.setSelectedItem(set.getOrganism().getName());
		} else {
			organismButton.setSelectedItem(null);
		}

		organismBox.addItemListener(this);
		organismButton.addItemListener(this);

		JPanel northPanel = new JPanel();

		northPanel.setLayout(new GridBagLayout());
		northPanel.add(new JLabel("XLS Column:"), createConstraints(0, 0));
		northPanel.add(organismBox, createConstraints(1, 0));

		if (organismBox.getSelectedItem().equals(DO_NOT_USE)) {
			// Do nothing
		} else if (organismBox.getSelectedItem().equals(OTHER_PARAMETER)) {
			northPanel.add(organismButton, createConstraints(1, 1));
		} else {
			int row = 1;
			String column = (String) organismBox.getSelectedItem();

			try {
				Set<String> values = xlsReader.getValuesInColumn(column);

				for (String value : values) {
					JComboBox<String> button = new JComboBox<String>(
							organismNames.toArray(new String[0]));

					if (set.getOrganismMappings().containsKey(value)) {
						button.setSelectedItem(set.getOrganismMappings()
								.get(value).getName());
					} else {
						button.setSelectedItem(null);
					}

					button.addItemListener(this);
					organismButtons.put(value, button);

					northPanel.add(new JLabel(value + ":"),
							createConstraints(0, row));
					northPanel.add(button, createConstraints(1, row));
					row++;
				}
			} catch (Exception e) {
			}
		}

		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout());
		panel.add(northPanel, BorderLayout.NORTH);

		organismPanel.removeAll();
		organismPanel.add(new JScrollPane(panel), BorderLayout.CENTER);
	}

	private void updateMatrixPanel() {
		List<String> matrixNames = new ArrayList<String>();

		for (Matrix matrix : DefaultDB.getInstance().getMatrices()) {
			matrixNames.add(matrix.getName());
		}

		matrixButtons.clear();
		matrixBox = new JComboBox<String>(new String[] { DO_NOT_USE,
				OTHER_PARAMETER });
		matrixButton = new JComboBox<String>(matrixNames.toArray(new String[0]));

		for (String column : fileColumnList) {
			matrixBox.addItem(column);
		}

		matrixBox.setSelectedItem(set.getMatrixColumn());

		if (set.getMatrix() != null) {
			matrixButton.setSelectedItem(set.getMatrix().getName());
		} else {
			matrixButton.setSelectedItem(null);
		}

		matrixBox.addItemListener(this);
		matrixButton.addItemListener(this);

		JPanel northPanel = new JPanel();

		northPanel.setLayout(new GridBagLayout());
		northPanel.add(new JLabel("XLS Column:"), createConstraints(0, 0));
		northPanel.add(matrixBox, createConstraints(1, 0));

		if (matrixBox.getSelectedItem().equals(DO_NOT_USE)) {
			// Do nothing
		} else if (matrixBox.getSelectedItem().equals(OTHER_PARAMETER)) {
			northPanel.add(matrixButton, createConstraints(1, 1));
		} else {
			int row = 1;
			String column = (String) matrixBox.getSelectedItem();

			try {
				Set<String> values = xlsReader.getValuesInColumn(column);

				for (String value : values) {
					JComboBox<String> button = new JComboBox<String>(
							matrixNames.toArray(new String[0]));

					if (set.getMatrixMappings().containsKey(value)) {
						button.setSelectedItem(set.getMatrixMappings()
								.get(value).getName());
					} else {
						button.setSelectedItem(null);
					}

					button.addItemListener(this);
					matrixButtons.put(value, button);

					northPanel.add(new JLabel(value + ":"),
							createConstraints(0, row));
					northPanel.add(button, createConstraints(1, row));
					row++;
				}
			} catch (Exception e) {
			}
		}

		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout());
		panel.add(northPanel, BorderLayout.NORTH);

		matrixPanel.removeAll();
		matrixPanel.add(new JScrollPane(panel), BorderLayout.CENTER);
	}

	private void updateColumnsPanel() {
		if (!fileColumnList.isEmpty()) {
			List<String> conditionNames = new ArrayList<String>();

			for (ConditionParameter condition : DefaultDB.getInstance()
					.getConditionParameters()) {
				conditionNames.add(condition.getName());
			}

			columnBoxes.clear();
			columnUnitBoxes.clear();

			JPanel northPanel = new JPanel();
			int row = 0;

			northPanel.setLayout(new GridBagLayout());

			for (String column : fileColumnList) {
				List<String> items = new ArrayList<String>();

				items.addAll(Arrays.asList(DO_NOT_USE, XlsReader.ID_COLUMN,
						XlsReader.TIME_COLUMN, XlsReader.CONCENTRATION_COLUMN));
				items.addAll(conditionNames);

				JComboBox<String> box = new JComboBox<String>(
						items.toArray(new String[0]));
				Object mapping = set.getColumnMappings().get(column);

				if (mapping == null) {
					box.setSelectedItem(DO_NOT_USE);
				} else if (mapping.equals(XlsReader.TIME_COLUMN)) {
					List<String> unitNames = new ArrayList<String>();

					for (Unit unit : DefaultDB.getInstance().getTimeQuantity()
							.getUnits()) {
						unitNames.add(unit.getName());
					}

					JComboBox<String> unitBox = new JComboBox<String>(
							unitNames.toArray(new String[0]));

					if (set.getTimeUnit() != null) {
						unitBox.setSelectedItem(set.getTimeUnit().getName());
					} else {
						unitBox.setSelectedItem(null);
					}

					unitBox.addItemListener(this);
					columnUnitBoxes.put(column, unitBox);
					northPanel.add(unitBox, createConstraints(2, row));
					box.setSelectedItem(mapping);
				} else if (mapping.equals(XlsReader.CONCENTRATION_COLUMN)) {
					List<String> unitNames = new ArrayList<String>();

					for (QuantityType quantity : DefaultDB.getInstance()
							.getConcentrationQuantities()) {
						for (Unit unit : quantity.getUnits()) {
							unitNames.add(unit.getName());
						}
					}

					JComboBox<String> unitBox = new JComboBox<String>(
							unitNames.toArray(new String[0]));

					if (set.getConcentrationUnit() != null) {
						unitBox.setSelectedItem(set.getConcentrationUnit()
								.getName());
					} else {
						unitBox.setSelectedItem(null);
					}

					unitBox.addItemListener(this);
					columnUnitBoxes.put(column, unitBox);
					northPanel.add(unitBox, createConstraints(2, row));
					box.setSelectedItem(mapping);
				} else if (mapping.equals(XlsReader.CONDITION_COLUMN)) {
					ConditionParameter condition = set.getConditions().get(
							column);
					List<String> unitNames = new ArrayList<String>();

					for (QuantityType quantity : condition.getQuantityTypes()) {
						for (Unit unit : quantity.getUnits()) {
							unitNames.add(unit.getName());
						}
					}

					box.setSelectedItem(condition.getName());

					JComboBox<String> unitBox = new JComboBox<String>(
							unitNames.toArray(new String[0]));

					if (set.getConditionUnits().get(column) != null) {
						unitBox.setSelectedItem(set.getConditionUnits()
								.get(column).getName());
					} else {
						unitBox.setSelectedItem(null);
					}

					unitBox.addItemListener(this);
					columnUnitBoxes.put(column, unitBox);
					northPanel.add(unitBox, createConstraints(2, row));
				} else {
					box.setSelectedItem(mapping);
				}

				box.addItemListener(this);
				columnBoxes.put(column, box);

				northPanel.add(new JLabel(column + ":"),
						createConstraints(0, row));
				northPanel.add(box, createConstraints(1, row));

				row++;
			}

			JPanel panel = new JPanel();

			panel.setLayout(new BorderLayout());
			panel.add(northPanel, BorderLayout.NORTH);

			columnsPanel.removeAll();
			columnsPanel.add(new JScrollPane(panel), BorderLayout.CENTER);
		} else {
			columnsPanel.removeAll();
			columnsPanel.add(noLabel, BorderLayout.CENTER);
		}
	}

	private GridBagConstraints createConstraints(int x, int y) {
		return new GridBagConstraints(x, y, 1, 1, 0, 0,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				new Insets(2, 2, 2, 2), 0, 0);
	}

	private void cleanMaps() {
		Map<String, Organism> newOrganismMappings = new LinkedHashMap<String, Organism>();
		Map<String, Matrix> newMatrixMappings = new LinkedHashMap<String, Matrix>();
		Map<String, String> newColumnMappings = new LinkedHashMap<String, String>();

		for (String organism : organismButtons.keySet()) {
			newOrganismMappings.put(organism,
					set.getOrganismMappings().get(organism));
		}

		for (String matrix : matrixButtons.keySet()) {
			newMatrixMappings.put(matrix, set.getMatrixMappings().get(matrix));
		}

		for (String column : fileColumnList) {
			if (set.getColumnMappings().containsKey(column)) {
				newColumnMappings.put(column,
						set.getColumnMappings().get(column));
			}
		}

		set.setOrganismMappings(newOrganismMappings);
		set.setMatrixMappings(newMatrixMappings);
		set.setColumnMappings(newColumnMappings);
	}

}

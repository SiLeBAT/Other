package de.bund.bfr.knime.pmm.io.formulareader;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

import de.bund.bfr.knime.pmm.core.models.ModelFormula;
import de.bund.bfr.knime.pmm.core.models.PrimaryModelFormula;
import de.bund.bfr.knime.pmm.core.models.SecondaryModelFormula;
import de.bund.bfr.knime.pmm.io.DefaultDB;

/**
 * <code>NodeDialog</code> for the "ModelReader" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christian Thoens
 */
public class FormulaReaderNodeDialog extends NodeDialogPane implements
		ActionListener {

	private FormulaReaderSettings set;

	private JComboBox<String> typeBox;
	private JPanel modelPanel;
	private Map<String, JCheckBox> boxes;

	/**
	 * New pane for configuring the ModelReader node.
	 */
	protected FormulaReaderNodeDialog() {
		typeBox = new JComboBox<String>(new String[] {
				FormulaReaderSettings.PRIMARY_TYPE,
				FormulaReaderSettings.SECONDARY_TYPE });
		typeBox.addActionListener(this);
		modelPanel = new JPanel();
		modelPanel.setLayout(new GridLayout(0, 1));
		boxes = new LinkedHashMap<String, JCheckBox>();

		JPanel mainPanel = new JPanel();

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(modelPanel, BorderLayout.NORTH);

		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout());
		panel.add(typeBox, BorderLayout.NORTH);
		panel.add(new JScrollPane(mainPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),
				BorderLayout.CENTER);

		addTab("Options", panel);
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings,
			DataTableSpec[] specs) throws NotConfigurableException {
		set = new FormulaReaderSettings();
		set.load(settings);

		typeBox.setSelectedItem(set.getModelType());
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		set.setModelType((String) typeBox.getSelectedItem());
		set.getPrimaryModels().clear();
		set.getSecondaryModels().clear();

		if (set.getModelType().equals(FormulaReaderSettings.PRIMARY_TYPE)) {
			for (PrimaryModelFormula model : DefaultDB.getInstance()
					.getPrimaryModels()) {
				if (boxes.get(model.getId()).isSelected()) {
					set.getPrimaryModels().add(model);
				}
			}
		} else if (set.getModelType().equals(
				FormulaReaderSettings.SECONDARY_TYPE)) {
			for (SecondaryModelFormula model : DefaultDB.getInstance()
					.getSecondaryModels()) {
				if (boxes.get(model.getId()).isSelected()) {
					set.getSecondaryModels().add(model);
				}
			}
		}

		set.save(settings);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		updateModelPanel();
	}

	private void updateModelPanel() {
		modelPanel.removeAll();
		boxes.clear();

		if (typeBox.getSelectedItem()
				.equals(FormulaReaderSettings.PRIMARY_TYPE)) {
			for (ModelFormula model : DefaultDB.getInstance()
					.getPrimaryModels()) {
				JCheckBox box = new JCheckBox(model.getName());

				modelPanel.add(box);
				boxes.put(model.getId(), box);
			}

			for (JCheckBox box : boxes.values()) {
				box.setSelected(false);
			}

			for (ModelFormula model : set.getPrimaryModels()) {
				boxes.get(model.getId()).setSelected(true);
			}
		} else if (typeBox.getSelectedItem().equals(
				FormulaReaderSettings.SECONDARY_TYPE)) {
			for (ModelFormula model : DefaultDB.getInstance()
					.getSecondaryModels()) {
				JCheckBox box = new JCheckBox(model.getName());

				modelPanel.add(box);
				boxes.put(model.getId(), box);
			}

			for (JCheckBox box : boxes.values()) {
				box.setSelected(false);
			}

			for (ModelFormula model : set.getSecondaryModels()) {
				boxes.get(model.getId()).setSelected(true);
			}
		}

		modelPanel.revalidate();
	}
}

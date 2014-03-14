package de.bund.bfr.knime.pmm.util.join;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.knime.core.node.DataAwareNodeDialogPane;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObject;

import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.core.port.PmmPortObjectSpec;

/**
 * <code>NodeDialog</code> for the "PmmJoiner" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christian Thoens
 */
public class JoinerNodeDialog extends DataAwareNodeDialogPane implements
		ActionListener {

	private JComboBox<String> joinerBox;
	private JPanel joinerPanel;

	private Joiner joiner;

	private PmmPortObject input1;
	private PmmPortObject input2;

	private JoinerSettings set;

	/**
	 * New pane for configuring the PmmJoiner node.
	 */
	protected JoinerNodeDialog() {
		JPanel panel = new JPanel();
		JPanel upperPanel = new JPanel();

		joinerBox = new JComboBox<String>(new String[] {
				JoinerSettings.PRIMARY_JOIN, JoinerSettings.SECONDARY_JOIN });
		joinerBox.addActionListener(this);
		joinerPanel = new JPanel();
		joinerPanel.setBorder(BorderFactory.createTitledBorder("Join Options"));
		joinerPanel.setLayout(new BorderLayout());

		upperPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		upperPanel.add(joinerBox);

		panel.setLayout(new BorderLayout());
		panel.add(upperPanel, BorderLayout.NORTH);
		panel.add(joinerPanel, BorderLayout.CENTER);
		addTab("Options", panel);
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, PortObject[] input)
			throws NotConfigurableException {
		input1 = (PmmPortObject) input[0];
		input2 = (PmmPortObject) input[1];
		set = new JoinerSettings();
		set.loadSettings(settings);

		if (set.getJoinType() == null) {
			set.setJoinType(JoinerSettings.PRIMARY_JOIN);
		}

		initGUI();
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		if (joiner == null || !joiner.isValid()) {
			throw new InvalidSettingsException("Invalid Join Type");
		}

		set.setAssignments(joiner.getAssignments());
		set.saveSettings(settings);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		set.setJoinType((String) joinerBox.getSelectedItem());
		initGUI();
	}

	private void initGUI() {
		String error = "";

		joinerBox.removeActionListener(this);
		joinerBox.setSelectedItem(set.getJoinType());
		joinerBox.addActionListener(this);
		joiner = null;

		String type1 = input1.getType();
		String type2 = input2.getType();

		if (JoinerSettings.PRIMARY_JOIN.equals(set.getJoinType())) {
			if (type1.equals(PmmPortObjectSpec.PRIMARY_MODEL_FORMULA_TYPE)
					&& type2.equals(PmmPortObjectSpec.DATA_TYPE)) {
				joiner = new PrimaryJoiner(input1, input2);
			} else if (type2
					.equals(PmmPortObjectSpec.PRIMARY_MODEL_FORMULA_TYPE)
					&& type1.equals(PmmPortObjectSpec.DATA_TYPE)) {
				error = "Please switch the ports!";
			} else {
				error = "Wrong input!";
			}
		} else if (JoinerSettings.SECONDARY_JOIN.equals(set.getJoinType())) {
			if (type1.equals(PmmPortObjectSpec.SECONDARY_MODEL_FORMULA_TYPE)
					&& type2.equals(PmmPortObjectSpec.PRIMARY_MODEL_TYPE)) {
				joiner = new SecondaryJoiner(input1, input2);
			} else if (type2
					.equals(PmmPortObjectSpec.SECONDARY_MODEL_FORMULA_TYPE)
					&& type1.equals(PmmPortObjectSpec.PRIMARY_MODEL_TYPE)) {
				error = "Please switch the ports!";
			} else {
				error = "Wrong input!";
			}
		}

		joinerPanel.removeAll();

		if (joiner != null) {
			joinerPanel.add(joiner.createPanel(set.getAssignments()),
					BorderLayout.CENTER);
			joinerPanel.revalidate();
		} else {
			if (joinerBox.isValid()) {
				JOptionPane.showMessageDialog(joinerBox,
						"Data is not valid for " + set.getJoinType());
			}

			joinerPanel.add(new JLabel(error), BorderLayout.CENTER);
		}
	}
}

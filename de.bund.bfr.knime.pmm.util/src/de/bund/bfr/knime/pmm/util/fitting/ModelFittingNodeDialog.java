package de.bund.bfr.knime.pmm.util.fitting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.knime.core.node.DataAwareNodeDialogPane;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObject;

import de.bund.bfr.knime.pmm.core.CombineUtilities;
import de.bund.bfr.knime.pmm.core.models.Model;
import de.bund.bfr.knime.pmm.core.models.ModelFormula;
import de.bund.bfr.knime.pmm.core.models.Parameter;
import de.bund.bfr.knime.pmm.core.models.PrimaryModel;
import de.bund.bfr.knime.pmm.core.models.SecondaryModel;
import de.bund.bfr.knime.pmm.core.port.PmmPortObject;
import de.bund.bfr.knime.pmm.core.port.PmmPortObjectSpec;
import de.bund.bfr.knime.pmm.core.ui.DoubleTextField;
import de.bund.bfr.knime.pmm.core.ui.IntTextField;

/**
 * <code>NodeDialog</code> for the "ModelFitting" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christian Thoens
 */
public class ModelFittingNodeDialog extends DataAwareNodeDialogPane implements
		ActionListener {

	private PmmPortObject input;
	private ModelFittingSettings set;

	private JComboBox<String> fittingBox;
	private JCheckBox limitsBox;
	private JCheckBox expertBox;

	private Map<String, String> modelNames;
	private Map<String, List<String>> modelParams;
	private Map<String, Map<String, Double>> modelMinValues;
	private Map<String, Map<String, Double>> modelMaxValues;

	private JPanel fittingPanel;
	private JPanel expertSettingsPanel;

	private IntTextField nParamSpaceField;
	private IntTextField nLevenbergField;
	private JCheckBox stopWhenSuccessBox;
	private JButton modelRangeButton;
	private JButton rangeButton;
	private JButton clearButton;
	private Map<String, Map<String, DoubleTextField>> minimumFields;
	private Map<String, Map<String, DoubleTextField>> maximumFields;

	/**
	 * New pane for configuring the ModelFitting node.
	 */
	protected ModelFittingNodeDialog() {
		fittingBox = new JComboBox<String>(new String[] {
				ModelFittingSettings.PRIMARY_FITTING,
				ModelFittingSettings.SECONDARY_FITTING,
				ModelFittingSettings.TERTIARY_FITTING });

		JPanel fittingTypePanel = new JPanel();

		fittingTypePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fittingTypePanel.add(fittingBox);

		fittingPanel = new JPanel();
		fittingPanel.setLayout(new BorderLayout());

		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout());
		panel.add(fittingTypePanel, BorderLayout.NORTH);
		panel.add(fittingPanel, BorderLayout.CENTER);

		addTab("Options", panel);
	}

	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, PortObject[] input)
			throws NotConfigurableException {
		this.input = (PmmPortObject) input[0];
		set = new ModelFittingSettings();
		set.loadSettings(settings);

		if (PmmPortObjectSpec.PRIMARY_MODEL_TYPE.equals(this.input.getType())) {
			set.setFittingType(ModelFittingSettings.PRIMARY_FITTING);
		} else if (PmmPortObjectSpec.SECONDARY_MODEL_TYPE.equals(this.input
				.getType())) {
			if (set.getFittingType() == null
					|| set.getFittingType().equals(
							ModelFittingSettings.PRIMARY_FITTING)) {
				set.setFittingType(ModelFittingSettings.SECONDARY_FITTING);
			}
		}

		fittingBox.setSelectedItem(set.getFittingType());
		fittingBox.addActionListener(this);
		initGUI();
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		if (!nParamSpaceField.isValueValid() || !nLevenbergField.isValueValid()
				|| minimumFields == null || maximumFields == null) {
			throw new InvalidSettingsException("");
		}

		Map<String, Map<String, Point2D.Double>> guessMap = new LinkedHashMap<String, Map<String, java.awt.geom.Point2D.Double>>();

		for (String modelId : modelParams.keySet()) {
			Map<String, Point2D.Double> guesses = new LinkedHashMap<String, java.awt.geom.Point2D.Double>();

			for (String param : modelParams.get(modelId)) {
				Double min = minimumFields.get(modelId).get(param).getValue();
				Double max = maximumFields.get(modelId).get(param).getValue();

				if (min == null) {
					min = Double.NaN;
				}

				if (max == null) {
					max = Double.NaN;
				}

				guesses.put(param, new Point2D.Double(min, max));
			}

			guessMap.put(modelId, guesses);
		}

		set.setFittingType((String) fittingBox.getSelectedItem());
		set.setnParameterSpace(nParamSpaceField.getValue());
		set.setnLevenberg(nLevenbergField.getValue());
		set.setEnforceLimits(limitsBox.isSelected());
		set.setExpertSettings(expertBox.isSelected());
		set.setStopWhenSuccessful(stopWhenSuccessBox.isSelected());
		set.setParameterGuesses(guessMap);
		set.saveSettings(settings);
	}

	private void readTable(List<? extends Model> dataModels) {
		modelNames = new LinkedHashMap<String, String>();
		modelParams = new LinkedHashMap<String, List<String>>();
		modelMinValues = new LinkedHashMap<String, Map<String, Double>>();
		modelMaxValues = new LinkedHashMap<String, Map<String, Double>>();

		for (Model dataModel : dataModels) {
			ModelFormula model = dataModel.getModelFormula();
			String id = model.getId();

			if (!modelNames.containsKey(id)) {
				List<String> paramNames = new ArrayList<String>();
				Map<String, Double> min = new LinkedHashMap<String, Double>();
				Map<String, Double> max = new LinkedHashMap<String, Double>();

				for (Parameter param : model.getParams()) {
					paramNames.add(param.getName());
					min.put(param.getName(), param.getMin());
					max.put(param.getName(), param.getMax());
				}

				modelNames.put(id, model.getName());
				modelParams.put(id, paramNames);
				modelMinValues.put(id, min);
				modelMaxValues.put(id, max);
			}
		}
	}

	private JComponent createOptionsPanel() {
		limitsBox = new JCheckBox("Enforce limits of Formula Definition");
		limitsBox.setSelected(set.isEnforceLimits());
		expertBox = new JCheckBox("Expert Settings");
		expertBox.setSelected(set.isExpertSettings());
		expertBox.addActionListener(this);

		JPanel limitsPanel = new JPanel();

		limitsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		limitsPanel.add(limitsBox);

		JPanel expertPanel = new JPanel();

		expertPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		expertPanel.add(expertBox);

		JPanel optionsPanel = new JPanel();

		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.add(limitsPanel);
		optionsPanel.add(expertPanel);

		return optionsPanel;
	}

	private JComponent createRegressionPanel() {
		nParamSpaceField = new IntTextField(0, 1000000, false);
		nParamSpaceField.setPreferredSize(new Dimension(100, nParamSpaceField
				.getPreferredSize().height));
		nParamSpaceField.setValue(set.getnParameterSpace());
		nLevenbergField = new IntTextField(1, 100, false);
		nLevenbergField.setPreferredSize(new Dimension(100, nLevenbergField
				.getPreferredSize().height));
		nLevenbergField.setValue(set.getnLevenberg());
		stopWhenSuccessBox = new JCheckBox("Stop When Regression Successful");
		stopWhenSuccessBox.setSelected(set.isStopWhenSuccessful());

		JPanel leftRegressionPanel = new JPanel();

		leftRegressionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5,
				5));
		leftRegressionPanel.setLayout(new GridLayout(3, 1, 5, 5));
		leftRegressionPanel.add(new JLabel(
				"Maximal Evaluations to Find Start Values"));
		leftRegressionPanel.add(new JLabel(
				"Maximal Executions of the Levenberg Algorithm"));
		leftRegressionPanel.add(stopWhenSuccessBox);

		JPanel rightRegressionPanel = new JPanel();

		rightRegressionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5,
				5));
		rightRegressionPanel.setLayout(new GridLayout(3, 1, 5, 5));
		rightRegressionPanel.add(nParamSpaceField);
		rightRegressionPanel.add(nLevenbergField);
		rightRegressionPanel.add(new JLabel());

		JPanel regressionPanel = new JPanel();

		regressionPanel.setBorder(new TitledBorder(
				"Nonlinear Regression Parameters"));
		regressionPanel.setLayout(new BorderLayout());
		regressionPanel.add(leftRegressionPanel, BorderLayout.WEST);
		regressionPanel.add(rightRegressionPanel, BorderLayout.EAST);

		return regressionPanel;
	}

	private JComponent createRangePanel() {
		modelRangeButton = new JButton("Use Range from Formula Definition");
		modelRangeButton.addActionListener(this);
		rangeButton = new JButton("Fill Empty Fields");
		rangeButton.addActionListener(this);
		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		minimumFields = new LinkedHashMap<String, Map<String, DoubleTextField>>();
		maximumFields = new LinkedHashMap<String, Map<String, DoubleTextField>>();

		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(modelRangeButton);
		buttonPanel.add(rangeButton);
		buttonPanel.add(clearButton);

		JPanel northRangePanel = new JPanel();

		northRangePanel.setLayout(new BoxLayout(northRangePanel,
				BoxLayout.Y_AXIS));

		for (String id : modelNames.keySet()) {
			JPanel modelPanel = new JPanel();
			JPanel leftPanel = new JPanel();
			JPanel rightPanel = new JPanel();
			List<String> params = modelParams.get(id);
			Map<String, DoubleTextField> minFields = new LinkedHashMap<String, DoubleTextField>();
			Map<String, DoubleTextField> maxFields = new LinkedHashMap<String, DoubleTextField>();
			Map<String, Point2D.Double> guesses = set.getParameterGuesses()
					.get(id);

			leftPanel.setLayout(new GridLayout(params.size(), 1));
			rightPanel.setLayout(new GridLayout(params.size(), 1));

			if (guesses == null) {
				guesses = new LinkedHashMap<String, Point2D.Double>();
			}

			for (String param : params) {
				JPanel labelPanel = new JPanel();
				JPanel minMaxPanel = new JPanel();
				DoubleTextField minField = new DoubleTextField(true);
				DoubleTextField maxField = new DoubleTextField(true);
				Double min = modelMinValues.get(id).get(param);
				Double max = modelMaxValues.get(id).get(param);

				minField.setPreferredSize(new Dimension(100, minField
						.getPreferredSize().height));
				maxField.setPreferredSize(new Dimension(100, maxField
						.getPreferredSize().height));

				if (guesses.containsKey(param)) {
					Point2D.Double range = guesses.get(param);

					if (!Double.isNaN(range.x)) {
						minField.setValue(range.x);
					}

					if (!Double.isNaN(range.y)) {
						maxField.setValue(range.y);
					}
				} else {
					minField.setValue(min);
					maxField.setValue(max);
				}

				String rangeString;

				if (min != null && max != null) {
					rangeString = " (" + min + " to " + max + "):";
				} else if (min != null) {
					rangeString = " (" + min + " to ):";
				} else if (max != null) {
					rangeString = " ( to " + max + "):";
				} else {
					rangeString = ":";
				}

				labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				labelPanel.add(new JLabel(param + rangeString));

				minMaxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				minMaxPanel.add(minField);
				minMaxPanel.add(new JLabel("to"));
				minMaxPanel.add(maxField);

				minFields.put(param, minField);
				maxFields.put(param, maxField);
				leftPanel.add(labelPanel);
				rightPanel.add(minMaxPanel);
			}

			minimumFields.put(id, minFields);
			maximumFields.put(id, maxFields);

			modelPanel.setBorder(BorderFactory.createTitledBorder(modelNames
					.get(id)));
			modelPanel.setLayout(new BorderLayout());
			modelPanel.add(leftPanel, BorderLayout.WEST);
			modelPanel.add(rightPanel, BorderLayout.EAST);

			northRangePanel.add(modelPanel);
		}

		JPanel rangePanel = new JPanel();

		rangePanel.setLayout(new BorderLayout());
		rangePanel.add(northRangePanel, BorderLayout.NORTH);

		JPanel panel = new JPanel();

		panel.setBorder(BorderFactory
				.createTitledBorder("Specific Start Values for Fitting Procedure - Optional"));
		panel.setLayout(new BorderLayout());
		panel.add(buttonPanel, BorderLayout.NORTH);
		panel.add(new JScrollPane(rangePanel), BorderLayout.CENTER);

		return panel;
	}

	private void initGUI() {
		boolean correctInput = true;

		if (ModelFittingSettings.PRIMARY_FITTING.equals(fittingBox
				.getSelectedItem())) {
			if (input.getType().equals(PmmPortObjectSpec.PRIMARY_MODEL_TYPE)) {
				readTable(input.getData(new ArrayList<PrimaryModel>()));
			} else {
				correctInput = false;
			}
		} else if (ModelFittingSettings.SECONDARY_FITTING.equals(fittingBox
				.getSelectedItem())) {
			if (input.getType().equals(PmmPortObjectSpec.SECONDARY_MODEL_TYPE)) {
				readTable(input.getData(new ArrayList<SecondaryModel>()));
			} else {
				correctInput = false;
			}
		} else if (ModelFittingSettings.TERTIARY_FITTING.equals(fittingBox
				.getSelectedItem())) {
			if (input.getType().equals(PmmPortObjectSpec.SECONDARY_MODEL_TYPE)) {
				readTable(CombineUtilities.combine(input
						.getData(new ArrayList<SecondaryModel>())));
			} else {
				correctInput = false;
			}
		}

		if (correctInput) {
			expertSettingsPanel = new JPanel();
			expertSettingsPanel.setLayout(new BorderLayout());
			expertSettingsPanel
					.add(createRegressionPanel(), BorderLayout.NORTH);
			expertSettingsPanel.add(createRangePanel(), BorderLayout.CENTER);

			fittingPanel.removeAll();
			fittingPanel.add(createOptionsPanel(), BorderLayout.NORTH);
			fittingPanel.add(expertSettingsPanel, BorderLayout.CENTER);
			fittingPanel.revalidate();
			fittingPanel.repaint();

			Dimension preferredSize = fittingPanel.getPreferredSize();

			if (expertBox.isSelected()) {
				expertSettingsPanel.setVisible(true);
			} else {
				expertSettingsPanel.setVisible(false);
			}

			fittingPanel.setPreferredSize(preferredSize);
		} else {
			JLabel label = new JLabel("Data is not valid for "
					+ fittingBox.getSelectedItem());

			label.setHorizontalAlignment(SwingConstants.CENTER);

			fittingPanel.removeAll();
			fittingPanel.add(label, BorderLayout.CENTER);
			fittingPanel.revalidate();

			if (fittingBox.isValid()) {
				JOptionPane
						.showMessageDialog(fittingBox, "Data is not valid for "
								+ fittingBox.getSelectedItem());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fittingBox) {
			initGUI();
		} else if (e.getSource() == expertBox) {
			if (expertBox.isSelected()) {
				expertSettingsPanel.setVisible(true);
			} else {
				expertSettingsPanel.setVisible(false);
			}
		} else if (e.getSource() == modelRangeButton) {
			for (String id : modelParams.keySet()) {
				for (String param : modelParams.get(id)) {
					minimumFields.get(id).get(param)
							.setValue(modelMinValues.get(id).get(param));
					maximumFields.get(id).get(param)
							.setValue(modelMaxValues.get(id).get(param));
				}
			}
		} else if (e.getSource() == rangeButton) {
			for (String id : modelParams.keySet()) {
				for (String param : modelParams.get(id)) {
					Double min = minimumFields.get(id).get(param).getValue();
					Double max = maximumFields.get(id).get(param).getValue();

					if (min == null && max == null) {
						minimumFields.get(id).get(param).setValue(-1000000.0);
						maximumFields.get(id).get(param).setValue(1000000.0);
					}
				}
			}
		} else if (e.getSource() == clearButton) {
			for (String id : modelParams.keySet()) {
				for (String param : modelParams.get(id)) {
					minimumFields.get(id).get(param).setValue(null);
					maximumFields.get(id).get(param).setValue(null);
				}
			}
		}
	}
}

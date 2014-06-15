package de.bund.bfr.knime.paroa.strat;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ConvenientComboBoxRenderer;

import de.bund.bfr.knime.paroa.strat.StratosphereNodeModel.EXEC;
import de.bund.bfr.knime.paroa.strat.StratosphereNodeModel.METHODS;

/**
 * <code>NodeDialog</code> for the "Stratosphere" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Markus Freitag
 */
public class StratosphereNodeDialog extends NodeDialogPane {

    static final String MSG_USE_VARIABLE = "Use variable";

    private String m_strat_location;
    private String m_strat_configuration;
    private String m_jar_location;
    private String m_method;
    private Boolean m_exec;
    private String m_input_sales_location;
    private String m_input_outbreaks_location;
    private String m_input_coordinates_location;
    private Integer m_num_scenarios;

    private final int SPACE_HORIZ = 15;
    private final int ITEM_HEIGHT = 20; // height for any atomic element, e.g.
					// the browse buttons
    private final int PANEL_WIDTH = 600; // total width for the whole panel/tab
					 // AND for each subcategory
    private final int PANEL_MAIN_HEIGHT = 250; // total height for the whole
					       // panel/tab

    /*
     * change these values to adapt the sizes of the elements within the
     * subcategories
     */
    private final Dimension buttonDimension = new Dimension(80, ITEM_HEIGHT);
    private final Dimension checkboxFieldDimension = new Dimension(250,
	    ITEM_HEIGHT);
    private final Dimension pathBoxDimensionDimension = new Dimension(250,
	    ITEM_HEIGHT);

    private JComboBox<String> m_methodChoiceBox;
    private JComboBox<String> m_stratExecChoiceBox;
    private SpinnerNumberModel m_scenariosChoiceField;

    private JCheckBox m_defaultJarCheckbox;
    private JCheckBox m_defaultStratLocationCheckbox;
    private JCheckBox m_defaultStratConfigurationCheckbox;
    private JCheckBox m_defaultInputSalesCheckbox;
    private JCheckBox m_defaultInputOutbreaksCheckbox;
    private JCheckBox m_defaultInputCoordinatesCheckbox;

    // the follwing fields need to be able to be disabled
    private JButton m_inputCoordinatesBrowseButton;
    private JSpinner m_spinner;
    private JPanel m_scenariosPanel;
    private JPanel m_inputCoordinatesPanel;
    private JPanel m_stratosphereLocationPanel;
    private JPanel m_stratosphereConfigurationPanel;

    private JComboBox<String> m_strat_location_selectBox;
    private JComboBox<String> m_strat_configuration_selectBox;
    private JComboBox<String> m_jar_location_selectBox;
    private JComboBox<String> m_input_sales_selectBox;
    private JComboBox<String> m_input_outbreaks_selectBox;
    private JComboBox<String> m_input_coordinates_selectBox;

    private JTextField m_stratRemoteAddressField;

    protected String m_stratRemoteAddress;

    protected StratosphereNodeDialog() {
	super();
	JPanel dataSettingsTab = new JPanel();
	dataSettingsTab.setLayout(new GridLayout(5, 1, 5, 5));

	JPanel stratosphereOptionsTab = new JPanel();
	stratosphereOptionsTab.setLayout(new GridLayout(5, 1, 5, 5));

	dataSettingsTab.setMinimumSize(new Dimension(PANEL_WIDTH,
		PANEL_MAIN_HEIGHT));
	dataSettingsTab.setPreferredSize(new Dimension(PANEL_WIDTH,
		PANEL_MAIN_HEIGHT));
	dataSettingsTab.setMaximumSize(new Dimension(PANEL_WIDTH,
		PANEL_MAIN_HEIGHT));

	dataSettingsTab.add(createMethodPanel());
	dataSettingsTab.add(createScenariosPanel());
	dataSettingsTab.add(createInputPanel());
	dataSettingsTab.add(createInputOutbreakPanel());
	dataSettingsTab.add(createInputCoordinatesPanel());

	stratosphereOptionsTab.add(createStratExecPanel());
	stratosphereOptionsTab.add(createStratosphereLocationPanel());
	stratosphereOptionsTab.add(createStratosphereConfigurationPanel());
	stratosphereOptionsTab.add(createJarPanel());

	super.addTab("Data Settings", dataSettingsTab);
	super.addTab("Stratosphere Options", stratosphereOptionsTab);
    }

    /** {@inheritDoc} */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings,
	    final PortObjectSpec[] specs) throws NotConfigurableException {
	try {
	    m_input_sales_location = settings
		    .getString(StratosphereNodeModel.CFGKEY_INPUT_SALES);
	    m_input_outbreaks_location = settings
		    .getString(StratosphereNodeModel.CFGKEY_INPUT_OUTBREAKS);
	    m_input_coordinates_location = settings
		    .getString(StratosphereNodeModel.CFGKEY_INPUT_COORDINATES);
	    m_strat_location = settings
		    .getString(StratosphereNodeModel.CFGKEY_STRAT_PATH);
	    m_strat_configuration = settings
		    .getString(StratosphereNodeModel.CFGKEY_STRAT_CONF);
	    m_jar_location = settings
		    .getString(StratosphereNodeModel.CFGKEY_JAR);
	    m_method = settings.getString(StratosphereNodeModel.CFGKEY_METHODS);
	    m_exec = settings.getBoolean(StratosphereNodeModel.CFGKEY_EXEC);
	    m_stratRemoteAddress = settings.getString(StratosphereNodeModel.CFGKEY_ADDRESS);
	    m_num_scenarios = settings
		    .getInt(StratosphereNodeModel.CFGKEY_SCENARIOS);
	} catch (InvalidSettingsException e) {
	    e.printStackTrace();
	}
	updatePanelValues();
    }

    private void updatePanelValues() {
	if (m_input_sales_location != null && !m_input_sales_location.equals(StratosphereNodeModel.DEFAULT_EMPTYSTRING)) {
	    m_defaultInputSalesCheckbox.setSelected(false);
	    m_input_sales_selectBox.setSelectedItem(m_input_sales_location);
	}
	else
	    m_input_sales_selectBox.setEnabled(false);

	if (m_input_outbreaks_location != null
		&& !m_input_outbreaks_location.equals(StratosphereNodeModel.DEFAULT_EMPTYSTRING)) {
	    m_defaultInputOutbreaksCheckbox.setSelected(false);
	    m_input_outbreaks_selectBox
		    .setSelectedItem(m_input_outbreaks_location);
	}
	else
	    m_input_outbreaks_selectBox.setEnabled(false);

	if (m_input_coordinates_location != null
		&& !m_input_coordinates_location.equals(StratosphereNodeModel.DEFAULT_EMPTYSTRING)) {
	    m_defaultInputCoordinatesCheckbox.setSelected(false);
	    m_input_coordinates_selectBox
		    .setSelectedItem(m_input_coordinates_location);
	}
	else
	    m_defaultInputCoordinatesCheckbox.setSelected(true);

	if (m_strat_location.equals(StratosphereNodeModel.DEFAULT_STRAT_PATH))
	    m_defaultStratLocationCheckbox.setSelected(true);
	else
	    m_defaultStratLocationCheckbox.setSelected(false);

	m_strat_location_selectBox.setSelectedItem(m_strat_location);

	if (m_strat_configuration != null && !m_strat_configuration.equals(StratosphereNodeModel.DEFAULT_EMPTYSTRING)) {
	    m_defaultStratConfigurationCheckbox.setSelected(false);
	    m_strat_configuration_selectBox.setSelectedItem(m_strat_configuration);
	}
	else
	    m_strat_configuration_selectBox.setEnabled(false);

	if (m_jar_location != null && !m_jar_location.equals(StratosphereNodeModel.DEFAULT_EMPTYSTRING)) {
	    m_defaultJarCheckbox.setSelected(false);
	    m_jar_location_selectBox.setSelectedItem(m_jar_location);
	}
	else
	    m_jar_location_selectBox.setEnabled(false);

	if (m_method != null)
	    m_methodChoiceBox.setSelectedItem(m_method);
	else
	    m_methodChoiceBox.setSelectedItem(StratosphereNodeModel.DEFAULT_METHOD);

	if (m_exec)
	    m_stratExecChoiceBox.setSelectedItem(EXEC.LOCAL.name());
	else
	    m_stratExecChoiceBox.setSelectedItem(EXEC.REMOTE.name());

	if (m_stratRemoteAddress != null && !m_stratRemoteAddress.equals(StratosphereNodeModel.DEFAULT_EMPTYSTRING))
	    m_stratRemoteAddressField.setText(m_stratRemoteAddress);

	if (m_num_scenarios != null)
	    m_scenariosChoiceField.setValue(m_num_scenarios);
    }

    /** {@inheritDoc} */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings)
	    throws InvalidSettingsException {
	if (m_input_sales_location != null
		&& !m_defaultInputSalesCheckbox.isSelected())
	    settings.addString(StratosphereNodeModel.CFGKEY_INPUT_SALES,
		    m_input_sales_location);
	else
	    settings.addString(StratosphereNodeModel.CFGKEY_INPUT_SALES,
		    StratosphereNodeModel.DEFAULT_EMPTYSTRING);

	if (m_input_outbreaks_location != null
		&& !m_defaultInputOutbreaksCheckbox.isSelected())
	    settings.addString(StratosphereNodeModel.CFGKEY_INPUT_OUTBREAKS,
		    m_input_outbreaks_location);
	else
	    settings.addString(StratosphereNodeModel.CFGKEY_INPUT_OUTBREAKS,
		    StratosphereNodeModel.DEFAULT_EMPTYSTRING);

	if (m_input_coordinates_location != null
		&& !m_defaultInputCoordinatesCheckbox.isSelected())
	    settings.addString(StratosphereNodeModel.CFGKEY_INPUT_COORDINATES,
		    m_input_coordinates_location);
	else
	    settings.addString(StratosphereNodeModel.CFGKEY_INPUT_COORDINATES,
		    StratosphereNodeModel.DEFAULT_EMPTYSTRING);

	settings.addString(StratosphereNodeModel.CFGKEY_METHODS, m_method);
	settings.addBoolean(StratosphereNodeModel.CFGKEY_EXEC, m_exec);
	settings.addString(StratosphereNodeModel.CFGKEY_ADDRESS, m_stratRemoteAddress);

	settings.addString(StratosphereNodeModel.CFGKEY_STRAT_PATH,
		m_strat_location);

	if (m_strat_configuration != null && !m_defaultStratConfigurationCheckbox.isSelected())
	    settings.addString(StratosphereNodeModel.CFGKEY_STRAT_CONF, m_strat_configuration);
	else
	    settings.addString(StratosphereNodeModel.CFGKEY_STRAT_CONF, StratosphereNodeModel.DEFAULT_EMPTYSTRING);

	if (m_jar_location != null && !m_defaultJarCheckbox.isSelected())
	    settings.addString(StratosphereNodeModel.CFGKEY_JAR, m_jar_location);
	else
	    settings.addString(StratosphereNodeModel.CFGKEY_JAR,
		    StratosphereNodeModel.DEFAULT_EMPTYSTRING);

	if (m_num_scenarios != null)
	    settings.addInt(StratosphereNodeModel.CFGKEY_SCENARIOS,
		    m_num_scenarios);
	else
	    settings.addInt(StratosphereNodeModel.CFGKEY_SCENARIOS,
		    StratosphereNodeModel.DEFAULT_SCENARIOS);
    }

    private JPanel createStratosphereConfigurationPanel() {
	m_stratosphereConfigurationPanel = createGridLayoutPanel("Stratosphere Configuration File");
	final JFileChooser locationChooser = createFileChooser(
		m_strat_configuration, JFileChooser.DIRECTORIES_ONLY);

	final JButton locationBrowseButton = new JButton("Browse...");
	locationBrowseButton.setEnabled(false);

	locationBrowseButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		locationChooser.showOpenDialog(getPanel().getParent());
		String currentLocation = locationChooser.getSelectedFile()
			.getAbsolutePath();
		if (currentLocation != null) {
		    // m_strat_location_selectBox.addItem(currentLocation);
		    m_strat_configuration_selectBox.setSelectedItem(currentLocation);
		}
	    }
	});

	m_strat_configuration_selectBox = createPathSelectBox();
	m_strat_configuration_selectBox.addItemListener(new ItemListener() {

	    @Override
	    public void itemStateChanged(ItemEvent e) {
		m_strat_configuration = e.getItem().toString();
		locationChooser.setCurrentDirectory(new File(e.getItem()
			.toString()));
	    }
	});

	m_defaultStratConfigurationCheckbox = createNamedCheckbox(MSG_USE_VARIABLE);
	m_defaultStratConfigurationCheckbox.addItemListener(new ItemListener() {

	    @Override
	    public void itemStateChanged(final ItemEvent e) {
		boolean selected = m_defaultStratConfigurationCheckbox.isSelected();
		locationBrowseButton.setEnabled(!selected);
		m_strat_configuration_selectBox.setEnabled(!selected);
	    }
	});

	m_stratosphereConfigurationPanel.add(m_defaultStratConfigurationCheckbox);
	m_stratosphereConfigurationPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	m_stratosphereConfigurationPanel.add(m_strat_configuration_selectBox);
	m_stratosphereConfigurationPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	m_stratosphereConfigurationPanel.add(locationBrowseButton);

	return m_stratosphereConfigurationPanel;
    }

    private JPanel createStratosphereLocationPanel() {
	m_stratosphereLocationPanel = createGridLayoutPanel("Stratosphere Location");
	final JFileChooser locationChooser = createFileChooser(
		m_strat_location, JFileChooser.DIRECTORIES_ONLY);

	final JButton locationBrowseButton = new JButton("Browse...");
	locationBrowseButton.setEnabled(false);

	locationBrowseButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		locationChooser.showOpenDialog(getPanel().getParent());
		String currentLocation = locationChooser.getSelectedFile()
			.getAbsolutePath();
		if (currentLocation != null) {
		    // m_strat_location_selectBox.addItem(currentLocation);
		    m_strat_location_selectBox.setSelectedItem(currentLocation);
		}
	    }
	});

	m_strat_location_selectBox = createPathSelectBox();
	m_strat_location_selectBox.addItemListener(new ItemListener() {

	    @Override
	    public void itemStateChanged(ItemEvent e) {
		m_strat_location = e.getItem().toString();
		locationChooser.setCurrentDirectory(new File(e.getItem()
			.toString()));
	    }
	});

	m_defaultStratLocationCheckbox = createNamedCheckbox("Use default (Linux) location");
	m_defaultStratLocationCheckbox.addChangeListener(new ChangeListener() {

	    @Override
	    public void stateChanged(ChangeEvent arg0) {
		boolean selected = m_defaultStratLocationCheckbox.isSelected();
		locationBrowseButton.setEnabled(!selected);
		m_strat_location_selectBox.setEnabled(!selected);
		if (selected)
		    m_strat_location_selectBox
			    .setSelectedItem(StratosphereNodeModel.DEFAULT_STRAT_PATH);
	    }
	});

	m_stratosphereLocationPanel.add(m_defaultStratLocationCheckbox);
	m_stratosphereLocationPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	m_stratosphereLocationPanel.add(m_strat_location_selectBox);
	m_stratosphereLocationPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	m_stratosphereLocationPanel.add(locationBrowseButton);

	return m_stratosphereLocationPanel;
    }

    private JPanel createJarPanel() {
	JPanel jarPanel = createGridLayoutPanel("Stratosphere plan jar");
	final JFileChooser jarChooser = createFileChooser(m_strat_location,
		JFileChooser.FILES_ONLY);

	FileNameExtensionFilter onlyJarFilter = new FileNameExtensionFilter(
		"JAR (Java Archive) files", "jar", "JAR");
	jarChooser.setFileFilter(onlyJarFilter);

	final JButton jarBrowseButton = new JButton("Browse...");
	jarBrowseButton.setEnabled(false);

	jarBrowseButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		jarChooser.showOpenDialog(getPanel().getParent());
		String currentLocation = jarChooser.getSelectedFile()
			.getAbsolutePath();
		if (currentLocation != null) {
		    m_jar_location_selectBox.addItem(currentLocation);
		    m_jar_location_selectBox.setSelectedItem(currentLocation);
		}
	    }
	});

	m_jar_location_selectBox = createPathSelectBox();
	m_jar_location_selectBox.addItemListener(new ItemListener() {

	    @Override
	    public void itemStateChanged(ItemEvent e) {
		m_jar_location = e.getItem().toString();
		jarChooser
			.setCurrentDirectory(new File(e.getItem().toString()));
	    }
	});

	m_defaultJarCheckbox = createNamedCheckbox(MSG_USE_VARIABLE);
	m_defaultJarCheckbox.addItemListener(new ItemListener() {

	    @Override
	    public void itemStateChanged(final ItemEvent e) {
		boolean selected = m_defaultJarCheckbox.isSelected();
		jarBrowseButton.setEnabled(!selected);
		m_jar_location_selectBox.setEnabled(!selected);
	    }
	});

	jarPanel.add(m_defaultJarCheckbox);
	jarPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	jarPanel.add(m_jar_location_selectBox);
	jarPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	jarPanel.add(jarBrowseButton);

	return jarPanel;
    }

    private JPanel createInputPanel() {
	JPanel inputPanel = createGridLayoutPanel("Input sales file");
	final JFileChooser inputChooser = createFileChooser(m_strat_location,
		JFileChooser.FILES_ONLY);

	final JButton inputBrowseButton = new JButton("Browse...");
	inputBrowseButton.setEnabled(false);

	inputBrowseButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		inputChooser.showOpenDialog(getPanel().getParent());
		String currentLocation = inputChooser.getSelectedFile()
			.getAbsolutePath();
		if (currentLocation != null) {
		    m_input_sales_selectBox.addItem(currentLocation);
		    m_input_sales_selectBox.setSelectedItem(currentLocation);
		}
	    }
	});
	m_input_sales_selectBox = createPathSelectBox();
	m_input_sales_selectBox.addItemListener(new ItemListener() {
	    @Override
	    public void itemStateChanged(ItemEvent e) {
		m_input_sales_location = e.getItem().toString();
		inputChooser.setCurrentDirectory(new File(e.getItem()
			.toString()));
	    }
	});

	m_defaultInputSalesCheckbox = createNamedCheckbox(MSG_USE_VARIABLE);
	m_defaultInputSalesCheckbox.addItemListener(new ItemListener() {

	    @Override
	    public void itemStateChanged(final ItemEvent e) {
		boolean selected = m_defaultInputSalesCheckbox.isSelected();
		inputBrowseButton.setEnabled(!selected);
		m_input_sales_selectBox.setEnabled(!selected);
	    }
	});

	inputPanel.add(m_defaultInputSalesCheckbox);
	inputPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	inputPanel.add(m_input_sales_selectBox);
	inputPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	inputPanel.add(inputBrowseButton);

	return inputPanel;
    }

    private JPanel createInputOutbreakPanel() {
	JPanel inputOutbreakPanel = createGridLayoutPanel("Input outbreaks file");
	final JFileChooser inputOutbreakChooser = createFileChooser(
		m_strat_location, JFileChooser.FILES_ONLY);

	final JButton inputOutbreakBrowseButton = new JButton("Browse...");
	inputOutbreakBrowseButton.setEnabled(false);

	inputOutbreakBrowseButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		inputOutbreakChooser.showOpenDialog(getPanel().getParent());
		String currentLocation = inputOutbreakChooser.getSelectedFile()
			.getAbsolutePath();
		if (currentLocation != null) {
		    m_input_outbreaks_selectBox.addItem(currentLocation);
		    m_input_outbreaks_selectBox
			    .setSelectedItem(currentLocation);
		}
	    }
	});

	m_input_outbreaks_selectBox = createPathSelectBox();
	m_input_outbreaks_selectBox.addItemListener(new ItemListener() {
	    @Override
	    public void itemStateChanged(ItemEvent e) {
		m_input_outbreaks_location = e.getItem().toString();
		inputOutbreakChooser.setCurrentDirectory(new File(e.getItem()
			.toString()));
	    }
	});

	m_defaultInputOutbreaksCheckbox = createNamedCheckbox(MSG_USE_VARIABLE);
	m_defaultInputOutbreaksCheckbox.addItemListener(new ItemListener() {

	    @Override
	    public void itemStateChanged(final ItemEvent e) {
		boolean selected = m_defaultInputOutbreaksCheckbox.isSelected();
		inputOutbreakBrowseButton.setEnabled(!selected);
		m_input_outbreaks_selectBox.setEnabled(!selected);
	    }
	});

	inputOutbreakPanel.add(m_defaultInputOutbreaksCheckbox);
	inputOutbreakPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	inputOutbreakPanel.add(m_input_outbreaks_selectBox);
	inputOutbreakPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	inputOutbreakPanel.add(inputOutbreakBrowseButton);

	return inputOutbreakPanel;
    }

    private JPanel createInputCoordinatesPanel() {
	m_inputCoordinatesPanel = createGridLayoutPanel("Input coordinates file");
	final JFileChooser inputCoordinatesChooser = createFileChooser(
		m_input_coordinates_location, JFileChooser.FILES_ONLY);

	m_inputCoordinatesBrowseButton = new JButton("Browse...");

	m_inputCoordinatesBrowseButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(final ActionEvent e) {
		inputCoordinatesChooser.showOpenDialog(getPanel().getParent());
		String currentLocation = inputCoordinatesChooser
			.getSelectedFile().getAbsolutePath();
		if (currentLocation != null) {
		    m_input_coordinates_selectBox.addItem(currentLocation);
		    m_input_coordinates_selectBox
			    .setSelectedItem(currentLocation);
		}
	    }
	});

	m_input_coordinates_selectBox = createPathSelectBox();
	m_input_coordinates_selectBox.addItemListener(new ItemListener() {
	    @Override
	    public void itemStateChanged(ItemEvent e) {
		m_input_coordinates_location = e.getItem().toString();
		inputCoordinatesChooser.setCurrentDirectory(new File(e
			.getItem().toString()));
	    }
	});

	m_defaultInputCoordinatesCheckbox = createNamedCheckbox(MSG_USE_VARIABLE);
	m_defaultInputCoordinatesCheckbox.addChangeListener(new ChangeListener() {

	    @Override
	    public void stateChanged(ChangeEvent arg0) {
		boolean selected = m_defaultInputCoordinatesCheckbox
			.isSelected();
		m_inputCoordinatesBrowseButton.setEnabled(!selected);
		m_input_coordinates_selectBox.setEnabled(!selected);
	    }
	});

	m_defaultInputCoordinatesCheckbox.setEnabled(false);
	m_input_coordinates_selectBox.setEnabled(false);
	m_inputCoordinatesBrowseButton.setEnabled(false);
	m_inputCoordinatesPanel.setEnabled(false);

	m_inputCoordinatesPanel.add(m_defaultInputCoordinatesCheckbox);
	m_inputCoordinatesPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	m_inputCoordinatesPanel.add(m_input_coordinates_selectBox);
	m_inputCoordinatesPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	m_inputCoordinatesPanel.add(m_inputCoordinatesBrowseButton);

	return m_inputCoordinatesPanel;
    }

    private JPanel createMethodPanel() {
	JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	methodPanel.setBorder(BorderFactory.createTitledBorder("Method(s)"));
	m_methodChoiceBox = new JComboBox<String>();
	m_methodChoiceBox.setPreferredSize(buttonDimension);
	m_methodChoiceBox.addItem(METHODS.LBM.name());
	m_methodChoiceBox.addItem(METHODS.SYRJALA.name());

	m_methodChoiceBox.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		String choice = (String) m_methodChoiceBox.getSelectedItem();
		m_method = choice;
		if (m_method.equals(METHODS.SYRJALA.name())) {
		    switchPanel(m_inputCoordinatesPanel, true);
		    switchPanel(m_scenariosPanel, false);
		    updatePanelValues();
		} else {
		    switchPanel(m_inputCoordinatesPanel, false);
		    switchPanel(m_scenariosPanel, true);
		    updatePanelValues();
		}
	    }
	});
	methodPanel.add(m_methodChoiceBox);
	return methodPanel;
    }

    private JPanel createStratExecPanel() {
	JPanel execPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	final JPanel addressPanel = new JPanel();

	execPanel.setBorder(BorderFactory.createTitledBorder("Environment"));
	m_stratExecChoiceBox = new JComboBox<String>();
	m_stratExecChoiceBox.setPreferredSize(buttonDimension);
	m_stratExecChoiceBox.addItem(EXEC.LOCAL.name());
	m_stratExecChoiceBox.addItem(EXEC.REMOTE.name());

	m_stratExecChoiceBox.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		String choice = (String) m_stratExecChoiceBox.getSelectedItem();
		if (choice.equals(EXEC.LOCAL.name())) {
		    m_exec = true;
		    switchPanel(m_stratosphereLocationPanel, true);
		    switchPanel(m_stratosphereConfigurationPanel, false);
		    switchPanel(addressPanel, false);
		    updatePanelValues();
		}
		else {
		    m_exec = false;
		    switchPanel(m_stratosphereLocationPanel, false);
		    switchPanel(m_stratosphereConfigurationPanel, true);
		    switchPanel(addressPanel, true);
		    updatePanelValues();
		}
	    }
	});

	m_stratRemoteAddressField = new JTextField("address:port");
	m_stratRemoteAddressField.setPreferredSize(pathBoxDimensionDimension);
	m_stratRemoteAddressField.addKeyListener(new KeyListener() {

	    @Override
	    public void keyTyped(KeyEvent arg0) {
		m_stratRemoteAddress = m_stratRemoteAddressField.getText();
	    }

	    @Override
	    public void keyReleased(KeyEvent arg0) {
	    }

	    @Override
	    public void keyPressed(KeyEvent arg0) {
	    }
	});

	addressPanel.add(m_stratRemoteAddressField);
	execPanel.add(m_stratExecChoiceBox);
	execPanel.add(Box.createHorizontalStrut(SPACE_HORIZ));
	execPanel.add(addressPanel);
	return execPanel;
    }

    private JPanel createScenariosPanel() {
	m_scenariosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	m_spinner = new JSpinner();

	m_scenariosPanel.setBorder(BorderFactory
		.createTitledBorder("Number of Generated Outbreak Scenarios"));
	m_scenariosChoiceField = new SpinnerNumberModel();
	m_scenariosChoiceField.setMinimum(0);
	m_scenariosChoiceField.setStepSize(1);
	m_scenariosChoiceField.setValue(1);

	m_scenariosChoiceField.addChangeListener(new ChangeListener() {

	    @Override
	    public void stateChanged(ChangeEvent arg0) {
		int choice = m_scenariosChoiceField.getNumber().intValue();
		m_num_scenarios = choice;
	    }
	});
	m_spinner.setModel(m_scenariosChoiceField);
	m_scenariosPanel.add(m_spinner);
	return m_scenariosPanel;
    }

    private JPanel createGridLayoutPanel(String title) {
	JPanel gridPanel = new JPanel(new GridBagLayout());
	gridPanel.setBorder(BorderFactory.createTitledBorder(title));
	return gridPanel;
    }

    private JFileChooser createFileChooser(String start_location,
	    int fileChooserInstruction) {
	JFileChooser fileChooser = new JFileChooser(start_location);
	fileChooser.setMultiSelectionEnabled(false);
	fileChooser.setFileSelectionMode(fileChooserInstruction);
	fileChooser.setEnabled(false);
	return fileChooser;
    }

    @SuppressWarnings("unchecked")
    private JComboBox<String> createPathSelectBox() {
	JComboBox<String> selectBox = new JComboBox<String>();
	selectBox.setPreferredSize(pathBoxDimensionDimension);
	selectBox.setEditable(true);
	selectBox.setRenderer(new ConvenientComboBoxRenderer());
	return selectBox;
    }

    private JCheckBox createNamedCheckbox(String title) {
	JCheckBox checkbox = new JCheckBox(title);
	checkbox.setPreferredSize(checkboxFieldDimension);
	checkbox.setAlignmentY(0.0f);
	checkbox.setBounds(0, 0, 100, 20);
	checkbox.setSelected(true);
	return checkbox;
    }

    protected void switchPanel(JPanel somePanel, boolean active) {
	for (int componentIndex = 0; componentIndex < somePanel.getComponentCount(); componentIndex++) {
	    somePanel.getComponent(componentIndex).setEnabled(active);
	}
	somePanel.setEnabled(active);
    }
}

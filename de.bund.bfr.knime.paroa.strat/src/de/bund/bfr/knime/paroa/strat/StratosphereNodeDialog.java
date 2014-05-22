package de.bund.bfr.knime.paroa.strat;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSpinner;
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

	private String m_strat_location;
	private String m_jar_location;
	private String m_method;
	private String m_input_sales_location;
	private String m_input_outbreaks_location;
	private String m_input_coordinates_location;
	private Integer m_num_scenarios;
	
	private final int SPACE_HORIZ = 15;
	private final int ITEM_HEIGHT = 20;		// height for any atomic element, e.g. the browse buttons
	private final int PANEL_WIDTH = 720;	// total width for the whole panel/tab AND for each subcategory
	private final int PANEL_MAIN_HEIGHT = 350; // total height for the whole panel/tab
	
	/* change these values to adapt the sizes of the elements within the subcategories */
	private final Dimension buttonDimension = new Dimension(80, ITEM_HEIGHT);
	private final Dimension checkboxFieldDimension = new Dimension(300, ITEM_HEIGHT);
	private final Dimension pathBoxDimensionDimension = new Dimension(250, ITEM_HEIGHT);
	
	private JComboBox<String> m_methodChoiceBox;
	private SpinnerNumberModel m_scenariosChoiceField;

	private JCheckBox m_defaultJarCheckbox;
	private JCheckBox m_defaultStratLocationCheckbox;
	private JCheckBox m_defaultInputSalesCheckbox;
	private JCheckBox m_defaultInputOutbreaksCheckbox;
	private JCheckBox m_defaultInputCoordinatesCheckbox;
	
	// the follwing fields need to be able to be disabled
	private JButton m_inputCoordinatesBrowseButton;
	private JPanel m_scenariosPanel;
	private JPanel m_inputCoordinatesPanel;
	private JSpinner m_spinner;
	
    private JComboBox<String> m_strat_location_selectBox;
    private JComboBox<String> m_jar_location_selectBox;
    private JComboBox<String> m_input_sales_selectBox;
    private JComboBox<String> m_input_outbreaks_selectBox;
    private JComboBox<String> m_input_coordinates_selectBox;
	
    protected StratosphereNodeDialog() {
        super();
    	JPanel stratosphereTab = new JPanel();
    	stratosphereTab.setLayout(new GridLayout(7, 1, 5, 5));

    	stratosphereTab.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_MAIN_HEIGHT));
    	stratosphereTab.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_MAIN_HEIGHT));
    	stratosphereTab.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_MAIN_HEIGHT));
        stratosphereTab.add(createMethodPanel());
        stratosphereTab.add(createScenariosPanel());
        stratosphereTab.add(createStratosphereLocationPanel());
        stratosphereTab.add(createJarPanel());
        stratosphereTab.add(createInputPanel());
        stratosphereTab.add(createInputOutbreakPanel());   	
        stratosphereTab.add(createInputCoordinatesPanel());   	
        
        super.addTab("Settings", stratosphereTab);
    }

    /** {@inheritDoc} */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings,
            final PortObjectSpec[] specs) throws NotConfigurableException {
    		try {
				m_input_sales_location = settings.getString(StratosphereNodeModel.CFGKEY_INPUT_SALES);
				m_input_outbreaks_location = settings.getString(StratosphereNodeModel.CFGKEY_INPUT_OUTBREAKS);
				m_input_coordinates_location = settings.getString(StratosphereNodeModel.CFGKEY_INPUT_COORDINATES);
				m_strat_location = settings.getString(StratosphereNodeModel.CFGKEY_STRAT_PATH);
				m_jar_location = settings.getString(StratosphereNodeModel.CFGKEY_JAR);
				m_method = settings.getString(StratosphereNodeModel.CFGKEY_METHODS);
				m_num_scenarios = settings.getInt(StratosphereNodeModel.CFGKEY_SCENARIOS);
			} catch (InvalidSettingsException e) {
				e.printStackTrace();
			}
    		updatePanelValues();
    }


    private void updatePanelValues() {
    	if(m_input_sales_location != null) {
    		m_defaultInputSalesCheckbox.setSelected(false);
    		m_input_sales_selectBox.setSelectedItem(m_input_sales_location);
    	}
    	if(m_input_outbreaks_location != null){
    		m_defaultInputOutbreaksCheckbox.setSelected(false);
    		m_input_outbreaks_selectBox.setSelectedItem(m_input_outbreaks_location);
    	}
    	if(m_input_coordinates_location != null) {
    		m_defaultInputCoordinatesCheckbox.setSelected(false);
    		m_input_coordinates_selectBox.setSelectedItem(m_input_coordinates_location);
    	}
    	if(m_strat_location.equals(StratosphereNodeModel.DEFAULT_STRAT_PATH))
    		m_defaultStratLocationCheckbox.setSelected(true);
    	else 
    		m_defaultStratLocationCheckbox.setSelected(false);
    	m_strat_location_selectBox.setSelectedItem(m_strat_location);
    	if(m_jar_location != null) {
    		m_defaultJarCheckbox.setSelected(false);
    		m_jar_location_selectBox.setSelectedItem(m_jar_location);
    	}
    	if(m_method != null)
    		m_methodChoiceBox.setSelectedItem(m_method);		
    	if(m_num_scenarios != null)
    		m_scenariosChoiceField.setValue(m_num_scenarios);		
	}

	/** {@inheritDoc} */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings)
            throws InvalidSettingsException {
    	if(m_input_sales_location != null && !m_defaultInputSalesCheckbox.isSelected())
    		settings.addString(StratosphereNodeModel.CFGKEY_INPUT_SALES, m_input_sales_location);
    	else
    		settings.addString(StratosphereNodeModel.CFGKEY_INPUT_SALES, StratosphereNodeModel.DEFAULT_EMPTYSTRING);
    
    	if(m_input_outbreaks_location != null && !m_defaultInputOutbreaksCheckbox.isSelected())
    		settings.addString(StratosphereNodeModel.CFGKEY_INPUT_OUTBREAKS, m_input_outbreaks_location);
    	else
    		settings.addString(StratosphereNodeModel.CFGKEY_INPUT_OUTBREAKS, StratosphereNodeModel.DEFAULT_EMPTYSTRING);
 
    	if(m_input_coordinates_location != null && !m_defaultInputCoordinatesCheckbox.isSelected())
    		settings.addString(StratosphereNodeModel.CFGKEY_INPUT_COORDINATES, m_input_coordinates_location);
    	else
    		settings.addString(StratosphereNodeModel.CFGKEY_INPUT_COORDINATES, StratosphereNodeModel.DEFAULT_EMPTYSTRING);
    	
   		settings.addString(StratosphereNodeModel.CFGKEY_STRAT_PATH, m_strat_location);
    	
    	if(m_methodChoiceBox.getSelectedItem() != null)
        	settings.addString(StratosphereNodeModel.CFGKEY_METHODS, (String)m_methodChoiceBox.getSelectedItem());
    	else
        	settings.addString(StratosphereNodeModel.CFGKEY_METHODS, StratosphereNodeModel.DEFAULT_METHODS);
    	
       	if(m_jar_location != null && !m_defaultJarCheckbox.isSelected())
       		settings.addString(StratosphereNodeModel.CFGKEY_JAR, m_jar_location);    
       	else
       		settings.addString(StratosphereNodeModel.CFGKEY_JAR, StratosphereNodeModel.DEFAULT_EMPTYSTRING);
       	
       	if(m_num_scenarios != null) 
       		settings.addInt(StratosphereNodeModel.CFGKEY_SCENARIOS, m_num_scenarios);    
       	else
        	settings.addInt(StratosphereNodeModel.CFGKEY_SCENARIOS, StratosphereNodeModel.DEFAULT_SCENARIOS);
    }
    
    private JPanel createStratosphereLocationPanel() {
    	JPanel locationPanel = createGridLayoutPanel("Stratosphere Location");
        final JFileChooser locationChooser = createFileChooser(m_strat_location, JFileChooser.DIRECTORIES_ONLY);

        final JButton locationBrowseButton = new JButton("Browse...");
        locationBrowseButton.setEnabled(false);
        
        locationBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
            	locationChooser.showOpenDialog(getPanel().getParent());
                String currentLocation = locationChooser.getSelectedFile().getAbsolutePath();
                if (currentLocation != null) {
//                	m_strat_location_selectBox.addItem(currentLocation);
                	m_strat_location_selectBox.setSelectedItem(currentLocation);
                }
            }
        });
    	
        m_strat_location_selectBox = createPathSelectBox();
        m_strat_location_selectBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
            	m_strat_location = e.getItem().toString();
            	System.out.println(m_strat_location);
            	locationChooser.setCurrentDirectory(new File(e.getItem().toString()));
			}
		});
        
    	m_defaultStratLocationCheckbox = createNamedCheckbox("Use default (Linux) location");
        m_defaultStratLocationCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent e) {
                boolean selected = m_defaultStratLocationCheckbox.isSelected();
                locationBrowseButton.setEnabled(!selected);
                m_strat_location_selectBox.setEnabled(!selected);
                if(selected)
                	m_strat_location_selectBox.setSelectedItem(StratosphereNodeModel.DEFAULT_STRAT_PATH);
            }
        });
                
        locationPanel.add(m_defaultStratLocationCheckbox);
        locationPanel.add(Box.createHorizontalStrut(SPACE_HORIZ ));
        locationPanel.add(m_strat_location_selectBox);
        locationPanel.add(Box.createHorizontalStrut(SPACE_HORIZ ));
        locationPanel.add(locationBrowseButton);
        
        return locationPanel;
    }


	private JPanel createJarPanel() {
    	JPanel jarPanel = createGridLayoutPanel("Stratosphere plan jar");
        final JFileChooser jarChooser = createFileChooser(m_strat_location, JFileChooser.FILES_ONLY) ;
        
        FileNameExtensionFilter onlyJarFilter = new FileNameExtensionFilter("JAR (Java Archive) files", "jar", "JAR");
        jarChooser.setFileFilter(onlyJarFilter);
      
        final JButton jarBrowseButton = new JButton("Browse...");
        jarBrowseButton.setEnabled(false);
        
        jarBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
            	jarChooser.showOpenDialog(getPanel().getParent());
                String currentLocation = jarChooser.getSelectedFile().getAbsolutePath();
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
            	jarChooser.setCurrentDirectory(new File(e.getItem().toString()));
			}
		});
        
    	m_defaultJarCheckbox = createNamedCheckbox("Use default jar (ParOA)");
        m_defaultJarCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent e) {
                boolean selected = m_defaultJarCheckbox.isSelected();
                jarBrowseButton.setEnabled(!selected);
                m_jar_location_selectBox.setEnabled(!selected);
            }
        });
        
        jarPanel.add(m_defaultJarCheckbox);
        jarPanel.add(Box.createHorizontalStrut(SPACE_HORIZ ));
        jarPanel.add(m_jar_location_selectBox);
        jarPanel.add(Box.createHorizontalStrut(SPACE_HORIZ ));
        jarPanel.add(jarBrowseButton);
        
        return jarPanel;
    }
    
    private JPanel createInputPanel() {
    	JPanel inputPanel = createGridLayoutPanel("Input sales file");
        final JFileChooser inputChooser = createFileChooser(m_strat_location, JFileChooser.FILES_ONLY) ;
    	
    	final JButton inputBrowseButton = new JButton("Browse...");
    	inputBrowseButton.setEnabled(false);
    	
    	inputBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
            	inputChooser.showOpenDialog(getPanel().getParent());
                String currentLocation = inputChooser.getSelectedFile().getAbsolutePath();
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
            	inputChooser.setCurrentDirectory(new File(e.getItem().toString()));
			}
		});
    	
    	m_defaultInputSalesCheckbox = createNamedCheckbox("Use standard variable (\"sales\")");
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
        inputPanel.add(Box.createHorizontalStrut(SPACE_HORIZ ));
        inputPanel.add(inputBrowseButton);
        
    	return inputPanel;
    }
    
    private JPanel createInputOutbreakPanel() {
    	JPanel inputOutbreakPanel = createGridLayoutPanel("Input outbreaks file");
        final JFileChooser inputOutbreakChooser = createFileChooser(m_strat_location, JFileChooser.FILES_ONLY) ;
    	
    	final JButton inputOutbreakBrowseButton = new JButton("Browse...");
    	inputOutbreakBrowseButton.setEnabled(false);
    	
    	inputOutbreakBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
            	inputOutbreakChooser.showOpenDialog(getPanel().getParent());
                String currentLocation = inputOutbreakChooser.getSelectedFile().getAbsolutePath();
                if (currentLocation != null) {
                	m_input_outbreaks_selectBox.addItem(currentLocation);
                	m_input_outbreaks_selectBox.setSelectedItem(currentLocation);
                }
            }
        });
    	
    	m_input_outbreaks_selectBox = createPathSelectBox();
    	m_input_outbreaks_selectBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				m_input_outbreaks_location = e.getItem().toString();
				inputOutbreakChooser.setCurrentDirectory(new File(e.getItem().toString()));
			}
		});
    	
    	m_defaultInputOutbreaksCheckbox = createNamedCheckbox("Use standard variable (\"outbreaks\")");
    	m_defaultInputOutbreaksCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent e) {
                boolean selected = m_defaultInputOutbreaksCheckbox.isSelected();
                inputOutbreakBrowseButton.setEnabled(!selected);
                m_input_outbreaks_selectBox.setEnabled(!selected);
            }
        });
        
        inputOutbreakPanel.add(m_defaultInputOutbreaksCheckbox);        
        inputOutbreakPanel.add(Box.createHorizontalStrut(SPACE_HORIZ ));
        inputOutbreakPanel.add(m_input_outbreaks_selectBox);
        inputOutbreakPanel.add(Box.createHorizontalStrut(SPACE_HORIZ ));
        inputOutbreakPanel.add(inputOutbreakBrowseButton);
        
    	return inputOutbreakPanel;
    }
    
    private JPanel createInputCoordinatesPanel() {
    	m_inputCoordinatesPanel = createGridLayoutPanel("Input coordinates file");
    	final JFileChooser inputCoordinatesChooser = createFileChooser(m_input_coordinates_location, JFileChooser.FILES_ONLY) ;
    	
    	m_inputCoordinatesBrowseButton = new JButton("Browse...");
    	m_inputCoordinatesBrowseButton.setEnabled(false);
    	
    	m_inputCoordinatesBrowseButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(final ActionEvent e) {
    			inputCoordinatesChooser.showOpenDialog(getPanel().getParent());
    			String currentLocation = inputCoordinatesChooser.getSelectedFile().getAbsolutePath();
    			if (currentLocation != null) {
    				m_input_coordinates_selectBox.addItem(currentLocation);
    				m_input_coordinates_selectBox.setSelectedItem(currentLocation);
    			}
    		}
    	});
    	
    	m_input_coordinates_selectBox = createPathSelectBox();
    	m_input_coordinates_selectBox.addItemListener(new ItemListener() {
    		@Override
    		public void itemStateChanged(ItemEvent e) {
    			m_input_coordinates_location = e.getItem().toString();
    			inputCoordinatesChooser.setCurrentDirectory(new File(e.getItem().toString()));
    		}
    	});
    	
    	m_defaultInputCoordinatesCheckbox = createNamedCheckbox("Use standard variable (\"coordinates\")");
    	m_defaultInputCoordinatesCheckbox.addItemListener(new ItemListener() {
    		
    		@Override
    		public void itemStateChanged(final ItemEvent e) {
    			boolean selected = m_defaultInputCoordinatesCheckbox.isSelected();
    			m_inputCoordinatesBrowseButton.setEnabled(!selected);
    			m_input_coordinates_selectBox.setEnabled(!selected);
    		}
    	});
    	
		m_defaultInputCoordinatesCheckbox.setEnabled(false);
		m_input_coordinates_selectBox.setEnabled(false);
		m_inputCoordinatesBrowseButton.setEnabled(false);
		m_inputCoordinatesPanel.setEnabled(false);
		
    	m_inputCoordinatesPanel.add(m_defaultInputCoordinatesCheckbox);        
    	m_inputCoordinatesPanel.add(Box.createHorizontalStrut(SPACE_HORIZ ));
    	m_inputCoordinatesPanel.add(m_input_coordinates_selectBox);
    	m_inputCoordinatesPanel.add(Box.createHorizontalStrut(SPACE_HORIZ ));
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
				String choice = (String)m_methodChoiceBox.getSelectedItem();
				m_method = choice;
				if(m_method.equals(METHODS.SYRJALA.name())) {
					m_defaultInputCoordinatesCheckbox.setEnabled(true);
					m_inputCoordinatesPanel.setEnabled(true);
					m_scenariosPanel.setEnabled(false);
					m_spinner.setEnabled(false);
				}
				else {
					m_defaultInputCoordinatesCheckbox.setEnabled(false);
					m_input_coordinates_selectBox.setEnabled(false);
					m_inputCoordinatesBrowseButton.setEnabled(false);
					m_inputCoordinatesPanel.setEnabled(false);
					m_scenariosPanel.setEnabled(true);
					m_spinner.setEnabled(true);
				}
			}
		});
        methodPanel.add(m_methodChoiceBox);
    	return methodPanel;
    }
    
    private JPanel createScenariosPanel() {
    	m_scenariosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	m_spinner = new JSpinner();
    	
    	m_scenariosPanel.setBorder(BorderFactory.createTitledBorder("Number of Generated Outbreak Scenarios"));
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
	private JComboBox<String> createPathSelectBox () {
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
}


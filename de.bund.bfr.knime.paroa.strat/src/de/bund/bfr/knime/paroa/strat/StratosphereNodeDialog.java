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
import javax.swing.filechooser.FileNameExtensionFilter;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ConvenientComboBoxRenderer;

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
	private String m_input_sales_location;
	private String m_method;
	private String m_input_outbreaks_location;
	
	private final int SPACE_HORIZ = 10;
	private final int ITEM_HEIGHT = 20;		// height for any atomic element, e.g. the browse buttons
	private final int PANEL_WIDTH = 560;	// total width for the whole panel/tab AND for each subcategory
	private final int PANEL_MAIN_HEIGHT = 250; // total height for the whole panel/tab
	
	/* change these values to adapt the sizes of the elements within the subcategories */
	private final Dimension buttonDimension = new Dimension(80, ITEM_HEIGHT);
	private final Dimension checkboxFieldDimension = new Dimension(210, ITEM_HEIGHT);
	private final Dimension pathBoxDimensionDimension = new Dimension(240, ITEM_HEIGHT);
	
	private JComboBox<String> m_methodChoiceBox;
	
	private JCheckBox m_defaultInputSalesCheckbox;
	private JCheckBox m_defaultJarCheckbox;
	private JCheckBox m_defaultStratLocationCheckbox;
	private JCheckBox m_defaultInputOutbreaksCheckbox;
	
    private JComboBox<String> m_strat_location_selectBox;
    private JComboBox<String> m_jar_location_selectBox;
    private JComboBox<String> m_input_sales_selectBox;
    private JComboBox<String> m_input_outbreaks_selectBox;
	
    protected StratosphereNodeDialog() {
        super();
    	JPanel stratosphereTab = new JPanel();
    	stratosphereTab.setLayout(new GridLayout(5, 1, 5, 5));

    	stratosphereTab.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_MAIN_HEIGHT));
    	stratosphereTab.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_MAIN_HEIGHT));
    	stratosphereTab.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_MAIN_HEIGHT));
        stratosphereTab.add(createMethodPanel());
        stratosphereTab.add(createStratosphereLocationPanel());
        stratosphereTab.add(createJarPanel());
        stratosphereTab.add(createInputPanel());
        stratosphereTab.add(createInputOutbreakPanel());   	
        
        super.addTab("Settings", stratosphereTab);
    }

    /** {@inheritDoc} */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings,
            final PortObjectSpec[] specs) throws NotConfigurableException {
    		try {
				m_input_sales_location = settings.getString(StratosphereNodeModel.CFGKEY_INPUT_SALES);
				m_input_outbreaks_location = settings.getString(StratosphereNodeModel.CFGKEY_INPUT_OUTBREAKS);
				m_strat_location = settings.getString(StratosphereNodeModel.CFGKEY_STRAT_PATH);
				m_jar_location = settings.getString(StratosphereNodeModel.CFGKEY_JAR);
				m_method = settings.getString(StratosphereNodeModel.CFGKEY_METHODS);
			} catch (InvalidSettingsException e) {
				e.printStackTrace();
			}
    		updatePanelValues();
    }


    private void updatePanelValues() {
    	if(m_input_sales_location != null)
    		m_defaultInputSalesCheckbox.setSelected(false);
    	if(m_input_outbreaks_location != null)
    		m_defaultInputOutbreaksCheckbox.setSelected(false);
    	if(m_strat_location != null)
    		m_defaultStratLocationCheckbox.setSelected(false);
    		m_strat_location_selectBox.setSelectedItem(m_strat_location);
    	if(m_jar_location != null)
    		m_defaultJarCheckbox.setSelected(false);
    	if(m_method != null)
    		m_methodChoiceBox.setSelectedItem(m_method);		
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
 
    	if(m_strat_location != null && !m_defaultStratLocationCheckbox.isSelected())
    		settings.addString(StratosphereNodeModel.CFGKEY_STRAT_PATH, m_strat_location);
    	else
    		settings.addString(StratosphereNodeModel.CFGKEY_STRAT_PATH, StratosphereNodeModel.DEFAULT_STRAT_PATH);   
    	
    	if(m_methodChoiceBox.getSelectedItem() != null)
        	settings.addString(StratosphereNodeModel.CFGKEY_METHODS, (String)m_methodChoiceBox.getSelectedItem());
    	else
        	settings.addString(StratosphereNodeModel.CFGKEY_METHODS, StratosphereNodeModel.DEFAULT_METHODS);
    	
       	if(m_jar_location != null && !m_defaultJarCheckbox.isSelected())
       		settings.addString(StratosphereNodeModel.CFGKEY_JAR, m_jar_location);    
       	else
       		settings.addString(StratosphereNodeModel.CFGKEY_JAR, StratosphereNodeModel.DEFAULT_EMPTYSTRING);
    }
    
    private JPanel createStratosphereLocationPanel() {
    	JPanel locationPanel = createGridLayoutPanel("Stratosphere Location");
        final JFileChooser locationChooser = createFileChooser(m_strat_location, JFileChooser.DIRECTORIES_ONLY);

        final JButton locationBrowseButton = new JButton("Browse...");
        locationBrowseButton.setPreferredSize(buttonDimension);
        locationBrowseButton.setEnabled(false);
        
        locationBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
            	locationChooser.showOpenDialog(getPanel().getParent());
                String currentLocation = locationChooser.getSelectedFile().getAbsolutePath();
                if (currentLocation != null) {
                	m_strat_location_selectBox.addItem(currentLocation);
                	m_strat_location_selectBox.setSelectedItem(currentLocation);
                }
            }
        });
    	
        m_strat_location_selectBox = createPathSelectBox();
        m_strat_location_selectBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
            	m_strat_location = e.getItem().toString();
            	locationChooser.setCurrentDirectory(new File(e.getItem().toString()));
			}
		});
        
    	m_defaultStratLocationCheckbox = createNamedCheckbox("use default location");
        m_defaultStratLocationCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent e) {
                boolean selected = m_defaultStratLocationCheckbox.isSelected();
                locationBrowseButton.setEnabled(!selected);
                m_strat_location_selectBox.setEnabled(!selected);
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
    
    private JPanel createMethodPanel() {
    	JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	methodPanel.setBorder(BorderFactory.createTitledBorder("Method(s)"));
        m_methodChoiceBox = new JComboBox<String>();
        m_methodChoiceBox.setPreferredSize(buttonDimension);
        m_methodChoiceBox.addItem("BOTH");
        m_methodChoiceBox.addItem("SPC");
        m_methodChoiceBox.addItem("LBM");
        
        m_methodChoiceBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String choice = (String)m_methodChoiceBox.getSelectedItem();
				m_method = choice;
				System.out.println(m_method);
			}
		});
        methodPanel.add(m_methodChoiceBox);
    	return methodPanel;
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


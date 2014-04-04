package de.bund.bfr.knime.paroa.strat;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;


/**
 * This is the model implementation of Stratosphere.
 * 
 *
 * @author Markus Freitag
 */
public class StratosphereNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(StratosphereNodeModel.class);
        
	static final String CFGKEY_METHODS = "methods";
	static final String CFGKEY_JAR = "jar";
	static final String CFGKEY_LOCAL = "local";
	static final String CFGKEY_INPUT = "input";
	static final String CFGKEY_OUTPUT = "output";

    /** initial default count value. */
    static final String DEFAULT_METHODS = "ALL";
    static final String DEFAULT_EMPTYSTRING = "";
    public static final String[] METHODS = { "BOTH", "SPC", "LBM"};
    public static final String[] LOCAL = { "LOCAL", "CLUSTER"};

  
    private final SettingsModelString m_methods =
    		new SettingsModelString(
    				StratosphereNodeModel.CFGKEY_METHODS,
    				StratosphereNodeModel.DEFAULT_METHODS
    				);

    private final SettingsModelString m_jar =
    		new SettingsModelString(
    				StratosphereNodeModel.CFGKEY_JAR,
    				StratosphereNodeModel.DEFAULT_EMPTYSTRING
    				);
    
    private final SettingsModelString m_local =
    		new SettingsModelString(
    				StratosphereNodeModel.CFGKEY_LOCAL,
    				StratosphereNodeModel.DEFAULT_EMPTYSTRING
    				);
    
    private final SettingsModelString m_input =
    		new SettingsModelString(
    				StratosphereNodeModel.CFGKEY_INPUT,
    				StratosphereNodeModel.DEFAULT_EMPTYSTRING
    				);
    
    private final SettingsModelString m_output =
            new SettingsModelString(
        			StratosphereNodeModel.CFGKEY_OUTPUT,
                    StratosphereNodeModel.DEFAULT_EMPTYSTRING
                    );
    

    /**
     * Constructor for the node model.
     */
    protected StratosphereNodeModel() {
    
        /* 
         * The ports:
         * in 1: sales data
         * in 2: outbreak data
         * out 1: result SPC
         * out 2: result LBM
         */
        super(2, 2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

    	String methodsChoice = m_methods.getStringValue();
    	String jarChoice = m_jar.getStringValue();
    	String localChoice = m_local.getStringValue();
    	String inputChoice = m_input.getStringValue();
    	String outputChoice = m_output.getStringValue();
    	
    	StratosphereConnection paroa_connection = getClusterConnection();
    	/*FileHandle paroa_data = setupStratosphereData(inputChoice, outputChoice);
    	FileHandle paroa_jars = setupStratosphereOperations(jarChoice);
    	runParoa(paroa_connection, paroa_jars, paroa_data);*/
    	
        DataColumnSpec[] allColSpecs = new DataColumnSpec[2];
        // change types!
        allColSpecs[0] = 
                new DataColumnSpecCreator("Colum1n_name", StringCell.TYPE).createSpec();
        allColSpecs[1] = 
            new DataColumnSpecCreator("Colum2n_name", StringCell.TYPE).createSpec();
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);

        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        for(int i = 0; i < 10 /* here goes a number */; i++) {
	        RowKey key = new RowKey("Product " + i);
	        DataCell[] cells = new DataCell[2];
	        cells[0] = new StringCell("first column value");
	        cells[1] = new StringCell("second column value");
	        DataRow row = new DefaultRow(key, cells);
	        container.addRowToTable(row);
	        exec.checkCanceled();
        }

        container.close();
        BufferedDataTable out = container.getTable();
        
        return new BufferedDataTable[]{out};
    }

    private StratosphereConnection getClusterConnection() {
		return new StratosphereConnection();
	}

	/**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message

        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        // TODO save user settings to the config object.
        
        m_methods.saveSettingsTo(settings);
        m_local.saveSettingsTo(settings);
        m_jar.saveSettingsTo(settings);
        m_input.saveSettingsTo(settings);
        m_output.saveSettingsTo(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO load (valid) settings from the config object.
        // It can be safely assumed that the settings are valided by the 
        // method below.
        
    	m_methods.loadSettingsFrom(settings);
    	m_local.loadSettingsFrom(settings);
    	m_jar.loadSettingsFrom(settings);
    	m_input.loadSettingsFrom(settings);
    	m_output.loadSettingsFrom(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO check if the settings could be applied to our model
        // e.g. if the count is in a certain range (which is ensured by the
        // SettingsModel).
        // Do not actually set any values of any member variables.

    	m_methods.validateSettings(settings);
        m_local.validateSettings(settings);
        m_jar.validateSettings(settings);
        m_input.validateSettings(settings);
        m_output.validateSettings(settings);

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        
        // TODO load internal data. 
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute method, models loaded in loadModelContent,
        // and user settings set through loadSettingsFrom - is all taken care 
        // of). Load here only the other internals that need to be restored
        // (e.g. data used by the views).

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
        // TODO save internal models. 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }

}


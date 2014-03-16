package de.bund.bfr.knime.oasq;

/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
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
import org.knime.core.node.defaultnodesettings.SettingsModelString;


/**
 * This is the model implementation of OASQ.
 * Status quo implementation of LBM and SPC.
 *
 * @author Markus Freitag
 */
public class OASQNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(OASQNodeModel.class);
        
    /** the settings key which is used to retrieve and 
        store the settings (from the dialog or from a settings file)    
       (package visibility to be usable from the dialog). */
	static final String CFGKEY_COUNT = "Count";
	static final String CFGKEY_METHOD = "Method";
	static final String LIKELYHOOD_METHOD = "Likelihood Method";
	static final String SPEARMAN_METHOD = "Spearman Method";

    /** initial default count value. */
    private ScenarioSetup setup;
    private Predictor pred;
    
	private final SettingsModelString method = new SettingsModelString(
			CFGKEY_METHOD, LIKELYHOOD_METHOD);
    /**
     * Constructor for the node model.
     */
    protected OASQNodeModel() {
        // 1: sales data
    	// 2: outbreak data
        super(2, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	System.out.println("Methode: " + method.getStringValue());
        BufferedDataTable sales_table = inData[0];
        BufferedDataTable outbreaks_table = inData[1];
               
        System.out.println("sales data...");
        SalesDataset sales_dataset = new SalesDataset(sales_table);
        
        System.out.println("outbreak data...");
        OutbreakDataset outbreak_dataset = new OutbreakDataset(outbreaks_table);
        
        System.out.println("scenario setup...");
        setup = new ScenarioSetup(sales_dataset, outbreak_dataset);
        
        System.out.println("init pred...");
        pred = new Predictor(setup.getProductDistributions(), setup.getProducts(), setup.getNormProducts(),setup.getProductMinusMean(), method.getStringValue().equals(SPEARMAN_METHOD));
        
        System.out.println("pred init...");
        pred.init();
        
        System.out.println("run experiment...");
        setup.runExperiment(pred);
        
        System.out.println("output results...");
        /* 	output format
         * 
         *	product	|	[SPC|LBM] value
         * 	4		|	0.37	
         * 	11		|	0.91
         * 	13		|	0.84	
         * 	12		|	0.52
         *  ...		|	...
         */
        double[] probabilitites = pred.getProbDistributionFor(outbreak_dataset.getNumCases() - 1);
        
        DataColumnSpec[] allColSpecs = new DataColumnSpec[2];
        allColSpecs[0] = 
                new DataColumnSpecCreator("Item", IntCell.TYPE).createSpec();
        allColSpecs[1] = 
            new DataColumnSpecCreator(method.getStringValue(), DoubleCell.TYPE).createSpec();
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);

        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        for(int i = 0; i < probabilitites.length; i++) {
	        RowKey key = new RowKey("Product " + i);
	        DataCell[] cells = new DataCell[2];
	        cells[0] = new IntCell(i);
	        cells[1] = new DoubleCell(probabilitites[i]);
	        DataRow row = new DefaultRow(key, cells);
	        container.addRowToTable(row);
	        exec.checkCanceled();
        }

        container.close();
        BufferedDataTable out = container.getTable();
        
        return new BufferedDataTable[]{out};
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
    	// no input validation implemented yet
        // TODO: 
    	// * check if input is a table with only numbers
    	// * check for header
    	// * check for homogene structure
        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        method.saveSettingsTo(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        method.loadSettingsFrom(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        method.validateSettings(settings);

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

}


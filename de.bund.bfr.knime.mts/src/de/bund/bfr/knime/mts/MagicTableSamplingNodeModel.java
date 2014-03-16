package de.bund.bfr.knime.mts;

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
import java.util.Random;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
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
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;


/**
 * This is the model implementation of MagicTableSampling.
 * This node creates a sample from a given distribution. A.J. Walkers method is used.
 * 
 * @author Markus Freitag
 */
public class MagicTableSamplingNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(MagicTableSamplingNodeModel.class);
        
    /** the settings key which is used to retrieve and 
        store the settings (from the dialog or from a settings file)    
       (package visibility to be usable from the dialog). */
	static final String CFGKEY_COUNT = "Count";
	static final String CFGKEY_GUILTY = "Column";
	public final static int DEFAULT_COUNT = 20;
	public final static int DEFAULT_GU = 10;

    /** initial default count value. */
    
	private final SettingsModelInteger count = new SettingsModelInteger(
			CFGKEY_COUNT, DEFAULT_COUNT);
	private final SettingsModelInteger guilty = new SettingsModelInteger(
			CFGKEY_GUILTY, DEFAULT_GU);
	
	private MagicTable<Object> aliasContSrc;
	private ScenarioSetup setup;
    /**
     * Constructor for the node model.
     */
    protected MagicTableSamplingNodeModel() {
        // 1: sales data
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	int countValue = count.getIntValue();
    	int guiltyColumn = guilty.getIntValue();
    	
    	System.out.println("number of samples: " + countValue);
    	System.out.println("guilty column: " + guiltyColumn);
    	
        BufferedDataTable sales_table = inData[0];
               
        System.out.println("sales data...");
        SalesDataset sales_dataset = new SalesDataset(sales_table, guiltyColumn);

        System.out.println("scenario setup...");
        setup = new ScenarioSetup(sales_dataset);

        System.out.println("init MT...");
		aliasContSrc = new MagicTable<Object>(new Random(guiltyColumn),
				setup.getProductDistributions()); 
		
        System.out.println("create samples...");
		int[] samples = new int[countValue];
		for(int i = 0; i < countValue; i++) {
			int sampleIndex = (aliasContSrc.sampleIndex());
			samples[i] = sales_dataset.getPlzDict()[sampleIndex];
			System.out.println(sampleIndex + " " + sales_dataset.getPlzDict()[sampleIndex]);
		}
        
        System.out.println("output results...");
        /* 	output format
         * 
         *	id=plz	|	cases
         * 	11234	|	3	
         * 	11344	|	1	
         * 	11336	|	0	
         * 	12334	|	8
         *  ...		|	...
         */
        DataColumnSpec[] allColSpecs = new DataColumnSpec[1];
        allColSpecs[0] = 
                new DataColumnSpecCreator("Cases", IntCell.TYPE).createSpec();
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        BufferedDataContainer container = exec.createDataContainer(outputSpec);

        int [] zipCodes = sales_dataset.getPlzDict();
        for (int zip : zipCodes) {
        	int num_samples = 0;
        	for (int sample : samples) {
				if(zip == sample)
					num_samples++;
			}
	        RowKey key = new RowKey(String.valueOf(zip));
	        DataCell[] cells = new DataCell[1];
	        cells[0] = new IntCell(num_samples);
	        DataRow row = new DefaultRow(key, cells);
	        container.addRowToTable(row);
	        exec.checkCanceled();
		}

        container.close();
        BufferedDataTable out = container.getTable();
        
        System.out.println("done.");
        return new BufferedDataTable[]{out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
    	// no validation implemented yet
    	// TODO:	validate positive integer input for all ports
    	// 			if possible: guilty column index must be <= number of columns in input
        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        count.saveSettingsTo(settings);
        guilty.saveSettingsTo(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	count.loadSettingsFrom(settings);
    	guilty.loadSettingsFrom(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	count.validateSettings(settings);
    	guilty.validateSettings(settings);

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


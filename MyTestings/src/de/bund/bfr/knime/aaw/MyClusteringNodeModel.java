package de.bund.bfr.knime.aaw;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.distmatrix.type.DistanceVectorDataCell;
import org.knime.distmatrix.type.DistanceVectorDataCellFactory;

/**
 * This is the model implementation of MyClustering.
 * 
 *
 * @author aaw
 */
public class MyClusteringNodeModel extends NodeModel {
    
    /**
     * Constructor for the node model.
     */
    protected MyClusteringNodeModel() {
    
        // TODO: Specify the amount of input and output ports needed.
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

    	BufferedDataContainer output = exec.createDataContainer(getSpec());
    	LinkedList<String[]> ll = new LinkedList<String[]>();
    	
    	for (DataRow row : inData[0]) {
        	DataCell[] cells = new DataCell[5];
    	    cells[0] = row.getCell(0);
    	    cells[1] = row.getCell(1);
    	    cells[2] = row.getCell(2);
    	    cells[3] = row.getCell(3);
    	    
    	    if (!row.getCell(3).isMissing()) {
    			String c_Keywords = ((StringCell) row.getCell(3)).getStringValue();
    	        String[] sp = c_Keywords.split(";");

    	        double[] result = new double[ll.size()];
    	        int i=0;
    	        //int count = 0;
    	        for (String[] spa : ll) {
    	            double sum = 0.0;
    	            for (String spai : spa) {
    	                for (String spi : sp) {
    	                    //sum += Math.abs(spi.compareTo(spai));
    	                	int ld = Levenshtein.LD(spi, spai);
    	                	if (ld < Math.min(spi.length(), spai.length()) / 5.0) {
        	                    sum++;
        	                    break;
    	                	}
    	                    //count++;
    	                }
    	            }
    	            int max = Math.min(sp.length, spa.length);
    	            result[i] = (max - sum) / max;
    	            i++;
    	        }
    	        
    	        ll.add(sp);
        	    cells[4] = DistanceVectorDataCellFactory.createCell(result, 1);
    	    }
    	    else {
    		    cells[4] = DataType.getMissingCell();
    	    }    	    
    	    
    	    if (row.getCell(1).isMissing()) {
        	    output.addRowToTable(new DefaultRow(row.getKey(), cells));
    	    }
    	    else {
    	    	String refmanID = ((StringCell) row.getCell(1)).getStringValue();
        	    output.addRowToTable(new DefaultRow(new RowKey(refmanID), cells));    	    	
    	    }
    	}	    
	    
	    output.close();

	    return new BufferedDataTable[]{output.getTable()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    }

    private DataTableSpec getSpec() {
    	DataColumnSpec[] spec = new DataColumnSpec[5];
    	spec[0] = new DataColumnSpecCreator("ID", IntCell.TYPE).createSpec();
    	spec[1] = new DataColumnSpecCreator("RefmanID", StringCell.TYPE).createSpec();
    	spec[2] = new DataColumnSpecCreator("PaperTitle", StringCell.TYPE).createSpec();
    	spec[3] = new DataColumnSpecCreator("Keywords", StringCell.TYPE).createSpec();
    	spec[4] = new DataColumnSpecCreator("Distance", DistanceVectorDataCell.TYPE).createSpec();
    	
    	return new DataTableSpec(spec);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {


        return new DataTableSpec[]{getSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
         // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

}


package de.bund.bfr.knime.mts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.node.BufferedDataTable;

/* Modification of EmpiricalDataSet.java */

public class SalesDataset {
	
	private double[] foodDistributions;	
	private int[] plzDict;
	public SalesDataset(BufferedDataTable foodDistributionTable, int guiltyColumn) throws IOException {
		/* file data structure is as follows:
		 * 
		 * i	0			1				2				3				4
		 * 0	ZIP CODE| 	Product 0 	| 	Product 1 	| 	Product 2	|	...
		 * 1	01234	|	6.123		|	1.234		|	4.561		|	...
		 * 2	01235	|	0.312		|	3.211		|	3.112		|	...
		 * 3	01312	|	0.001		|	2.321		|	0.222		|	...
		 * 4	...		|	...			|	...			|	...			|	...
		 * 
		 */
		int num_lines = foodDistributionTable.getRowCount();
		
		foodDistributions = new double[num_lines];
		plzDict = new int[num_lines];
		
		CloseableRowIterator row_it = foodDistributionTable.iterator();
		int row_index = 0;			
		
		while(row_it.hasNext()){	// iterate over all rows/tuples/zip codes		
			DataRow currentRow = row_it.next();
			// this line does not need a comment because the method is awesomely named 
			Double[] currentRowValues = convertCellsToDoubleArray(currentRow.iterator()); 
			plzDict[row_index] = Integer.parseInt(currentRow.getKey().getString());
			foodDistributions[row_index] = currentRowValues[0];
			row_index++;
		}
	}
	
	private Double[] convertCellsToDoubleArray(Iterator<DataCell> cell_iterator) {
		ArrayList<Double> double_list = new ArrayList<Double>();
		while(cell_iterator.hasNext())
			double_list.add(Double.parseDouble(cell_iterator.next().toString()));
		Double[] double_array = new Double[double_list.size()];
		double_array = double_list.toArray(double_array);
		return double_array;
	}

	public double[] getFoodDistributions() {
		return foodDistributions;
	}

	public int[] getPlzDict() {
		return plzDict;
	}
}

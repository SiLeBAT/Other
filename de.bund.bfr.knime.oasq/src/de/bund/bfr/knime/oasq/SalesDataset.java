package de.bund.bfr.knime.oasq;

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
	
	private String[] foodDistributionNames;
	private double[][] foodDistributions;	
	private int[] plzDict;
	public SalesDataset(BufferedDataTable foodDistributionTable) throws IOException {
		foodDistributionNames = null;
		foodDistributions = null;

		int num_lines = foodDistributionTable.getRowCount();

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
		CloseableRowIterator row_it = foodDistributionTable.iterator();
		
		if (foodDistributionNames == null) {
			String[] column_names = foodDistributionTable.getDataTableSpec().getColumnNames();
			foodDistributionNames = Arrays.copyOfRange(column_names, 0, column_names.length);
			foodDistributions = new double[foodDistributionNames.length][num_lines];
			plzDict = new int[num_lines];
		}
		
		int row_index = 0;			
		while(row_it.hasNext()){	// iterate over all rows/tuples/zip codes		
			DataRow currentRow = row_it.next();
			// this line does not need a comment because the method is awesomely named 
			Double[] currentRowValues = convertCellsToDoubleArray(currentRow.iterator()); 
			plzDict[row_index] = Integer.parseInt(currentRow.getKey().getString());
//			pr(plzDict[row_index]);
			for (int product_index = 0; product_index < currentRowValues.length; product_index++) {
				foodDistributions[product_index][row_index] = currentRowValues[product_index];
//				pr(foodDistributions[product_index][row_index]);
			}
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

	public String[] getFoodDistributionNames() {
		return foodDistributionNames;
	}

	public void setFoodDistributionNames(String[] foodDistributionNames) {
		this.foodDistributionNames = foodDistributionNames;
	}

	public double[][] getFoodDistributions() {
		return foodDistributions;
	}

	public void setFoodDistributions(double[][] foodDistributions) {
		this.foodDistributions = foodDistributions;
	}

	public int[] getPlzDict() {
		return plzDict;
	}
}

package org.eclipse.stem.fbd;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class EmpiricalDataset {
	
	private static String DELIMITER = "\t";

	private String[] foodDistributionNames;
	private double[][] foodDistributions;	

	public EmpiricalDataset(File foodDistributionFile) throws IOException {
		foodDistributionNames = null;
		foodDistributions = null;

		BufferedReader br = new BufferedReader(new FileReader(
				foodDistributionFile));
		int num_lines = 0;

		/* count entries/rows in file */
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			num_lines++;
		}
		/* go back to the beginning of the file */
		br = new BufferedReader(new FileReader(foodDistributionFile));

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
		for (int line_index = 0; line_index < num_lines; line_index++) {
			String[] row = br.readLine().split(DELIMITER);

			if (foodDistributionNames == null) {
				foodDistributionNames = Arrays.copyOfRange(row, 1, row.length);
				foodDistributions = new double[foodDistributionNames.length][num_lines - 1];
				continue;
			}

			for (int product_index = 1; product_index < row.length; product_index++) {
				foodDistributions[product_index - 1][line_index - 1] = Double.parseDouble(row[product_index]);
			}
		}
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
}

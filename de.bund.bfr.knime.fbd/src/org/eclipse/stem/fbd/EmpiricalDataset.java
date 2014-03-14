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
		int lines = 0;

		for (String line = br.readLine(); line != null; line = br.readLine()) {
			lines++;
		}

		br = new BufferedReader(new FileReader(foodDistributionFile));

		for (int j = 0; j < lines; j++) {
			String[] row = br.readLine().split(DELIMITER);

			if (foodDistributionNames == null) {
				foodDistributionNames = Arrays.copyOfRange(row, 1, row.length);
				foodDistributions = new double[foodDistributionNames.length][lines - 1];
				continue;
			}

			for (int i = 1; i < row.length; i++) {
				foodDistributions[i - 1][j - 1] = Double.parseDouble(row[i]);
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

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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BatchExperimentScenario {

	// Number of case reports
	public static final int MAX_NO_OF_REPORTS = 100;

	// Used for Likelihood algorithm
	private double[][] productDistributions;

	// Used for Spearman algorithm
	private double[][] products;
	private double[] meanProducts;
	private double[] normProducts;
	private double[][] productMinusMean;

	private MagicTable<Object> aliasContSrc;

	public BatchExperimentScenario(EmpiricalDataset data) {
		List<List<Double>> prods = this.removeRegionsWithoutProducts(data
				.getFoodDistributions());
		int numProducts = prods.size();
		int numRegions = prods.get(0).size();

		productDistributions = new double[numProducts][numRegions];
		products = new double[numProducts][numRegions];
		
		/* assign data from file to respective lists */
		for (int productIndex = 0; productIndex < numProducts; productIndex++) {
			double sumOfAllProductValues = MathOps.sum(prods.get(productIndex));

			for (int regionIndex = 0; regionIndex < numRegions; regionIndex++) {
				/* productDistributions = product fraction of total amount for region */
				productDistributions[productIndex][regionIndex] = prods.get(
						productIndex).get(regionIndex)
						/ sumOfAllProductValues;
				products[productIndex][regionIndex] = prods.get(productIndex)
						.get(regionIndex);
			}
		}

		meanProducts = new double[numProducts];
		normProducts = new double[numProducts];
		productMinusMean = new double[numProducts][numRegions];

		for (int productIndex = 0; productIndex < numProducts; productIndex++) {
			meanProducts[productIndex] = MathOps.mean(products[productIndex]);
			normProducts[productIndex] = 0.0;

			for (int regionIndex = 0; regionIndex < numRegions; regionIndex++) {
				double p = products[productIndex][regionIndex] - meanProducts[productIndex];

				normProducts[productIndex] += p * p;
				productMinusMean[productIndex][regionIndex] = p;
			}

			normProducts[productIndex] = Math.sqrt(normProducts[productIndex]);
		}
	}

	public void init(int contaminatedSource, int iterNo)
			throws MagicTableException {
		aliasContSrc = new MagicTable<Object>(new Random(iterNo),
				productDistributions[contaminatedSource]);
	}

	public void runExperiment(double noise, long seed,
			MLSourcePredictor predictor) {
		Random rand = new Random(seed);

		for (int i = 0; i < MAX_NO_OF_REPORTS; i++) {
			int location = 0;

			if (noise >= rand.nextDouble()) {
				location = rand.nextInt(productDistributions[0].length);
			} else {
				location = aliasContSrc.sampleIndex();
			}

			predictor.update(location);
		}
	}

	private List<List<Double>> removeRegionsWithoutProducts(double[][] foodDist) {
		List<List<Double>> res = new ArrayList<List<Double>>();

		for (int productIndex = 0; productIndex < foodDist.length; productIndex++) {
			res.add(new ArrayList<Double>());
		}

		for (int regionIndex = 0; regionIndex < foodDist[0].length; regionIndex++) {
			boolean isZeroEverywhere = true;

			for (int productIndex = 0; productIndex < foodDist.length; productIndex++) {
				if (foodDist[productIndex][regionIndex] > 0.0) {
					isZeroEverywhere = false;
				}
			}

			if (isZeroEverywhere) {
				continue;
			}

			for (int productIndex = 0; productIndex < foodDist.length; productIndex++) {
				res.get(productIndex).add(foodDist[productIndex][regionIndex]);
			}
		}

		return res;
	}

	public double[][] getProductDistributions() {
		return this.productDistributions;
	}

	public double[][] getProducts() {
		return products;
	}

	public double[] getMeanProducts() {
		return meanProducts;
	}

	public double[] getNormProducts() {
		return normProducts;
	}

	public double[][] getProductMinusMean() {
		return productMinusMean;
	}
}

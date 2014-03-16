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
import java.util.Arrays;
import java.util.List;

public class MLSourcePredictor {

	private static final int MAX_ACCURACY = 200;

	private boolean spearman;
	private double[] cases;
	private int numCases;

	private List<double[]> probDistributionList;
	private List<Integer> mostLikelyProductList;

	// Used for Likelihood algorithm
	private double[][] productDistributions;

	// Used for Spearman algorithm
	private double[][] products;
	private double[] normProducts;
	private double[][] productsMinusMean;

	private int numProducts;
	private int numRegions;

	public MLSourcePredictor(double[][] productDistributions,
			double[][] products, double[] normProducts,
			double[][] productsMinusMean, boolean spearman) {
		this.productDistributions = productDistributions;
		this.products = products;
		this.normProducts = normProducts;
		this.productsMinusMean = productsMinusMean;
		this.spearman = spearman;

		numProducts = products.length;
		numRegions = products[0].length;
	}

	public void init() {
		probDistributionList = new ArrayList<double[]>();
		mostLikelyProductList = new ArrayList<Integer>();
		cases = new double[numRegions];
		numCases = 0;

		Arrays.fill(cases, 0.0);
	}

	public void update(int newRegionIndex) {
		int mostLikelyProduct = -1;
		double[] probDistribution = null;

		if (!spearman) {
			double mostLikelyProductValue = -MAX_ACCURACY * numRegions;

			if (probDistributionList.size() > 0) {
				probDistribution = Arrays.copyOf(probDistributionList
						.get(probDistributionList.size() - 1), numProducts);
			} else {
				probDistribution = new double[numProducts];
				Arrays.fill(probDistribution, 0.0);
			}

			for (int productIndex = 0; productIndex < numProducts; productIndex++) {
				double value = productDistributions[productIndex][newRegionIndex];

				if (!MathOps.approxEqual(value, 0)) {
					probDistribution[productIndex] += Math.log(value);
				} else {
					probDistribution[productIndex] -= MAX_ACCURACY;
				}

				if (probDistribution[productIndex] > mostLikelyProductValue) {
					mostLikelyProductValue = probDistribution[productIndex];
					mostLikelyProduct = productIndex;
				}
			}
		} else {
			double mostLikelyProductValue = 0;

			probDistribution = new double[numProducts];
			cases[newRegionIndex]++;
			numCases++;

			for (int productIndex = 0; productIndex < numProducts; productIndex++) {
				double normProduct = normProducts[productIndex];
				double meanCases = (double) numCases / (double) numRegions;
				double normCases = 0.0;
				double cor = 0.0;
				boolean isZero = false;

				for (int regionIndex = 0; regionIndex < numRegions; regionIndex++) {
					if (cases[regionIndex] != 0
							&& products[productIndex][regionIndex] == 0) {
						isZero = true;
						break;
					}

					double c = cases[regionIndex] - meanCases;

					normCases += c * c;
					cor += c * productsMinusMean[productIndex][regionIndex];
				}

				if (!isZero) {
					normCases = Math.sqrt(normCases);
					probDistribution[productIndex] = Math.max(cor / normCases
							/ normProduct, 0);

					if (probDistribution[productIndex] > mostLikelyProductValue) {
						mostLikelyProductValue = probDistribution[productIndex];
						mostLikelyProduct = productIndex;
					}
				} else {
					probDistribution[productIndex] = 0.0;
				}
			}
		}

		probDistributionList.add(probDistribution);
		mostLikelyProductList.add(mostLikelyProduct);
	}

	public double[] getProbDistributionFor(int reportNumber) {
		if (probDistributionList.size() < reportNumber) {
			return null;
		}

		// Distribution must be normed so that sum over all probabilities is 1
		double[] distribution = probDistributionList.get(reportNumber);
		double[] normedDistribution = new double[numProducts];

		// Normalization
		if (!spearman) {
			double s = MathOps.sumLogs(distribution);

			for (int productIndex = 0; productIndex < numProducts; productIndex++) {
				normedDistribution[productIndex] = Math
						.exp(distribution[productIndex] - s);
			}
		} else {
			double s = MathOps.sum(distribution);

			for (int productIndex = 0; productIndex < numProducts; productIndex++) {
				normedDistribution[productIndex] = distribution[productIndex]
						/ s;
			}
		}

		return normedDistribution;
	}

	public List<Integer> getMostLikelyProductList() {
		return mostLikelyProductList;
	}
}

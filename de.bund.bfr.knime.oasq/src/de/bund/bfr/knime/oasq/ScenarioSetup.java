package de.bund.bfr.knime.oasq;

import java.util.Arrays;


public class ScenarioSetup {

	// Used for Likelihood algorithm
	private double[][] productDistributions;

	// Used for Spearman algorithm
	private double[][] products;
	private double[] meanProducts;
	private double[] normProducts;
	private double[][] productMinusMean;
	private Integer[] outbreaks;
	private SalesDataset sales;

	public ScenarioSetup(SalesDataset sales, OutbreakDataset outbreak_data) {
		this.sales = sales;
	
		outbreaks = outbreak_data.getOutbreaks();
		products = sales.getFoodDistributions();
		
		int numProducts = products.length;
		int numRegions = products[0].length;

		productDistributions = new double[numProducts][numRegions];

		/* assign data from file to respective lists */
		for (int productIndex = 0; productIndex < numProducts; productIndex++) {
			double sumOfAllProductValues = MathOps.sum(products[productIndex]);
			/*
			 *  nothing sold
			 */
			if(sumOfAllProductValues == 0.0) {
				Arrays.fill(productDistributions[productIndex], 0.0);
				continue;
			}
			for (int regionIndex = 0; regionIndex < numRegions; regionIndex++) {
				/*
				 * productDistributions = product fraction of total amount for
				 * region
				 */
				productDistributions[productIndex][regionIndex] = products[productIndex][regionIndex]
						/ sumOfAllProductValues; // products normiert
			}

		}

		meanProducts = new double[numProducts];
		normProducts = new double[numProducts];
		productMinusMean = new double[numProducts][numRegions];

		for (int productIndex = 0; productIndex < numProducts; productIndex++) {
			meanProducts[productIndex] = MathOps.mean(products[productIndex]);
			normProducts[productIndex] = 0.0;

			for (int regionIndex = 0; regionIndex < numRegions; regionIndex++) {
				double p = products[productIndex][regionIndex]
						- meanProducts[productIndex];

				normProducts[productIndex] += p * p;
				productMinusMean[productIndex][regionIndex] = p;
			}

			normProducts[productIndex] = Math.sqrt(normProducts[productIndex]);
		}
	}

	/*
	 * public void init(int contaminatedSource, int iterNo) throws
	 * MagicTableException { aliasContSrc = new MagicTable<Object>(new
	 * Random(iterNo), productDistributions[contaminatedSource]); }
	 */

	public void runExperiment(Predictor predictor) {

		for (int i = 0; i < outbreaks.length; i++) {
			int region = dictLookup(outbreaks[i]);
			predictor.update(region);
		}
	}

	private int dictLookup(int plz) {
		int ERROR_VALUE = 100000; // plz do not exceed 99999
		int region = ERROR_VALUE; // 0 == not found == error
		int[] dict = sales.getPlzDict();
		for (int i = 0; i < dict.length; i++) {
			if (dict[i] == plz)
				region = i;
		}
		if (region == ERROR_VALUE)
			System.out.println("ERROR: plz not found in dictionary");
		return region;
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
package de.bund.bfr.knime.mts;

public class ScenarioSetup {

	private double[] productDistributions;
	private double[] products;
	private SalesDataset sales;

	public ScenarioSetup(SalesDataset sales) {
		this.sales = sales;
		products = this.sales.getFoodDistributions();
		
		int numRegions = products.length;

		productDistributions = new double[numRegions];

		/* assign data from file to respective lists */
			double sumOfAllProductValues = MathOps.sum(products);

			for (int regionIndex = 0; regionIndex < numRegions; regionIndex++) {
				/*
				 * productDistributions = product fraction of total amount for
				 * region
				 */
				productDistributions[regionIndex] = products[regionIndex]
						/ sumOfAllProductValues; // products normiert
			}
	}
		
	public double[] getProductDistributions() {
		return this.productDistributions;
	}

	public double[] getProducts() {
		return products;
	}
}
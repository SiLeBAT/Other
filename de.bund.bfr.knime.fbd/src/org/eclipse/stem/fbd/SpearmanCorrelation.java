package org.eclipse.stem.fbd;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

/**
 * A sample word count Stratosphere job.
 * <p>
 * You can run this sample from your IDE using the main() method. The
 * LocalExecutor starts a Stratosphere instance out of your IDE.
 * <p>
 * The two inner classes SplitWords and CountWords provide sample user logic to
 * count the words. Please check the respective comments for details.
 */
public class SpearmanCorrelation {

	private static Double calculate_spc(List<Integer> sales, List<Integer> cases) {
		int numRegions = sales.size(); // vectors/lists have equal length, so
										// have the arrays

		// convert lists to arrays of primitive types (performance optimization)
		int[] vector_array_sales = new int[numRegions];
		for (int i = 0; i < numRegions; i++) {
			vector_array_sales[i] = sales.get(i);
		}

		int[] vector_array_cases = new int[numRegions];
		for (int i = 0; i < numRegions; i++) {
			vector_array_cases[i] = cases.get(i);
		}

		// PRE
		// calculate mean sales
		int sum_sales = 0;
		for (int i = 0; i < numRegions; i++) {
			sum_sales += vector_array_sales[i];
		}
		double mean_sales = (double) sum_sales / (double) numRegions;

		// calculate sales norm and stuff
		double norm_sales = 0.0;
		double[] productMinusMean = new double[numRegions];

		for (int i = 0; i < numRegions; i++) {
			double currentProductSale = (double) vector_array_sales[i];

			double saleValueMinusMean = currentProductSale - mean_sales;
			norm_sales += saleValueMinusMean * saleValueMinusMean;
			productMinusMean[i] = saleValueMinusMean;
		}
		norm_sales = Math.sqrt(norm_sales);

		// calculate mean cases
		int sum_cases = 0;
		for (int i = 0; i < numRegions; i++) {
			sum_cases += vector_array_cases[i];
		}
		double mean_cases = (double) sum_cases / (double) numRegions;

		// MAIN
		// calculate spc
		double norm_cases = 0.0;
		double cor = 0.0;

		boolean isZero = false;
		for (int i = 0; i < numRegions; i++) {
			if (vector_array_cases[i] != 0 && vector_array_sales[i] == 0) {
				isZero = true;
				break;
			}
			double c = vector_array_cases[i] - mean_cases;

			norm_cases += c * c;
			cor += c * productMinusMean[i];
		}
		norm_cases = Math.sqrt(norm_cases);

		/*
		 * System.out.println(cor); System.out.println(norm_cases);
		 * System.out.println(norm_sales); System.out.println();
		 */

		if (!isZero)
			return Math.max(cor / norm_cases / norm_sales, 0.0);
		else
			return 0.0;
	}

	private static Double calculate_spc2(List<Integer> sales,
			List<Integer> cases) {
		double[] vector_array_sales = toDoubleArray(sales);
		double[] vector_array_cases = toDoubleArray(cases);
		double mean_sales = getMean(vector_array_sales);
		double mean_cases = getMean(vector_array_cases);

		// calculate sales norm and stuff
		double cor = 0.0;
		double norm_sales = 0.0;
		double norm_cases = 0.0;

		for (int i = 0; i < vector_array_cases.length; i++) {
			double s = vector_array_sales[i] - mean_sales;
			double c = vector_array_cases[i] - mean_cases;

			cor += s * c;
			norm_sales += s * s;
			norm_cases += c * c;
		}

		norm_sales = Math.sqrt(norm_sales);
		norm_cases = Math.sqrt(norm_cases);

		return cor / norm_cases / norm_sales;
	}

	private static double[] toDoubleArray(List<Integer> list) {
		double[] array = new double[list.size()];

		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}

		return array;
	}

	private static double getMean(double[] array) {
		double sum = 0.0;

		for (double d : array) {
			sum += d;
		}

		return sum / array.length;
	}

	public static void main(String[] args) throws Exception {
		List<Integer> sales = Arrays.asList(3, 2, 3, 10, 1, 34);
		List<Integer> cases = Arrays.asList(12, 2, 0, 10, 1, 34);
		SpearmansCorrelation corr = new SpearmansCorrelation();

		System.out.println(calculate_spc(sales, cases));
		System.out.println(calculate_spc2(sales, cases));
		System.out.println(corr.correlation(toDoubleArray(sales),
				toDoubleArray(cases)));
	}
}
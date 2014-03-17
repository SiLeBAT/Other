package de.bund.bfr.knime.mts;

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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Provides different mathematical methods.
 * 
 */
public final class MathOps {

	/**
	 * Machine Epsilon, calculated as described in <a
	 * href="http://en.wikipedia.org/wiki/Machine_epsilon"
	 * >http://en.wikipedia.org/wiki/Machine_epsilon</a> .
	 */
	public static final double MACHINE_EPSILON = MathOps
			.calculateMachineEpsilon();

	private MathOps() {
		// do not instantiate
	}

	/**
	 * Double values are not precise due to the way they are stored (See IEEE
	 * 754). In order to compare doubles, one has to approximate the equation
	 * using a certain threshold. This method provides double comparison with
	 * using a reasonable threshold.
	 * 
	 * @param dbl1
	 *            Double value
	 * @param dbl2
	 *            Double value
	 * @return true if values are equal up to a certain threshold
	 */
	public static boolean approxEqual(final double dbl1, final double dbl2) {
		return Math.abs(dbl1 - dbl2) <= MathOps.MACHINE_EPSILON;
	}

	/**
	 * Calculates the maximum entry of an array, where only specific indices are
	 * allowed to be treated. Returns the index of the maximum.
	 * 
	 * @param indices
	 *            allowed indices to be checked for maximum
	 * @param entries
	 *            array, where maximum is taken from
	 * @return index of the maximum of the entries array
	 */
	public static int argMaxDouble(final double[] entries,
			final Collection<Integer> indices) {
		if (entries == null || entries.length == 0 || indices == null
				|| indices.isEmpty()) {
			return -1;
		}

		final Iterator<Integer> itr = indices.iterator();
		int argmax = itr.next();

		while (itr.hasNext()) {
			final Integer element = itr.next();
			if (entries[element] > entries[argmax]) {
				argmax = element;
			}
		}
		return argmax;
	}

	/**
	 * Calculates the maximum entry of an array, where only specific indices are
	 * allowed to be treated. Returns the index of the maximum.
	 * 
	 * @param indices
	 *            allowed indices to be checked for maximum
	 * @param entries
	 *            array, where maximum is taken from
	 * @return index of the maximum of the entries array
	 */
	public static int argMaxDouble(final double[] entries, final int... indices) {
		if (entries == null || entries.length == 0 || indices == null
				|| indices.length == 0) {
			return -1;
		}
		int argmax = indices[0];

		for (int element : indices) {
			if (entries[element] > entries[argmax]) {
				argmax = element;
			}
		}
		return argmax;
	}

	/**
	 * Calculates the maximum entry of a list, where only specific indices are
	 * allowed to be treated. Returns the index of the maximum.
	 * 
	 * @param indices
	 *            allowed indices to be checked for maximum
	 * @param entries
	 *            list where maximum is taken from
	 * @return index of the maximum of the entries array
	 */
	public static int argMaxDouble(final List<Double> entries,
			final Collection<Integer> indices) {
		if (entries == null || entries.isEmpty() || indices == null
				|| indices.isEmpty()) {
			return -1;
		}

		final Iterator<Integer> itr = indices.iterator();
		int argmax = itr.next();

		while (itr.hasNext()) {
			final Integer element = itr.next();
			if (entries.get(element) > entries.get(argmax)) {
				argmax = element;
			}
		}
		return argmax;
	}

	/**
	 * Calculates the minimum entry of an array, where only specific indices are
	 * allowed to be treated. Returns the index of the minimum.
	 * 
	 * @param indices
	 *            allowed indices to be checked for minimum
	 * @param entries
	 *            array, where minimum is taken from
	 * @return index of the minimum of the entries array
	 */
	public static int argMinDouble(final double[] entries,
			final Collection<Integer> indices) {
		if (entries == null || entries.length == 0 || indices == null
				|| indices.isEmpty()) {
			return -1;
		}

		final Iterator<Integer> itr = indices.iterator();
		int argmin = itr.next();

		while (itr.hasNext()) {
			final Integer element = itr.next();
			if (entries[element] < entries[argmin]) {
				argmin = element;
			}
		}
		return argmin;
	}

	/**
	 * Calculates the minimum entry of an array, where only specific indices are
	 * allowed to be treated. Returns the index of the minimum.
	 * 
	 * @param indices
	 *            allowed indices to be checked for minimum
	 * @param entries
	 *            array, where minimum is taken from
	 * @return index of the minimum of the entries array
	 */
	public static int argMinDouble(final double[] entries, final int... indices) {
		if (entries == null || entries.length == 0 || indices == null
				|| indices.length == 0) {
			return -1;
		}
		int argmin = indices[0];

		for (int element : indices) {
			if (entries[element] < entries[argmin]) {
				argmin = element;
			}
		}
		return argmin;
	}

	/**
	 * Calculates the minimum entry of a list, where only specific indices are
	 * allowed to be treated. Returns the index of the minimum.
	 * 
	 * @param indices
	 *            allowed indices to be checked for minimum
	 * @param entries
	 *            list where minimum is taken from
	 * @return index of the minimum of the entries array
	 */
	public static int argMinDouble(final List<Double> entries,
			final Collection<Integer> indices) {
		if (entries == null || entries.isEmpty() || indices == null
				|| indices.isEmpty()) {
			return -1;
		}

		final Iterator<Integer> itr = indices.iterator();
		int argmin = itr.next();

		while (itr.hasNext()) {
			final Integer element = itr.next();
			if (entries.get(element) < entries.get(argmin)) {
				argmin = element;
			}
		}
		return argmin;
	}

	private static void binaryPush(final List<Double> sortedNumbers,
			final Double newValue) {
		// bounds
		int low = 0;
		int high = sortedNumbers.size() - 1;

		while (low <= high) {
			final int mid = (low + high) >>> 1;
			final double midVal = sortedNumbers.get(mid);
			// covering following cases:
			//
			// 1. newValue is between positions mid and mid+1
			// 2. mid+1 is equal to sortedNumbers.size(), indicating that we are
			// at the end of the array.
			//
			// In both cases, newValue will be added to position mid+1.
			if (midVal <= newValue
					&& (sortedNumbers.size() <= mid + 1 || newValue <= sortedNumbers
							.get(mid + 1))) {
				sortedNumbers.add(mid + 1, newValue);
				return;
			}
			if (midVal < newValue) {
				low = mid + 1;
			} else {
				high = mid - 1;
			}
		}
		// we are at the beginning of the array
		sortedNumbers.add(low, newValue);
	}

	/**
	 * Machine epsilon. For further information see <a
	 * href="http://en.wikipedia.org/wiki/Machine_epsilon"
	 * >http://en.wikipedia.org/wiki/Machine_epsilon</a>.
	 * 
	 * @return machine epsilon
	 */
	public static double calculateMachineEpsilon() {
		double machEps = 1.0d;
		do {
			machEps /= 2.0d;
		} while ((1.0 + (machEps / 2.0)) != 1.0);
		return machEps;
	}

	/**
	 * Checks if a given array of doubles obeys the rules of discrete
	 * probability distributions.
	 * 
	 * @param probabilities
	 *            array of probabilities
	 * @return false if it can't be a discrete probability distribution
	 */
	public static boolean checkIfDiscreteDistribution(
			final double... probabilities) {
		if (probabilities == null) {
			return false;
		}
		for (double prob : probabilities) {
			if (prob < 0D || prob > 1D) {
				return false;
			}
		}
		return MathOps.approxEqual(MathOps.sum(probabilities), 1d);
	}

	/**
	 * Greatest common divisor, coded by by Scot Drysdale, Dartmouth University.
	 * See <a href=
	 * "http://www.cs.dartmouth.edu/farid/teaching/cs15/cs5/lectures/0501/GCD.java"
	 * >http://www.cs.dartmouth.edu/farid/teaching/cs15/cs5/lectures/0501/GCD.
	 * java</a>
	 * 
	 * @param arg0
	 *            first number
	 * @param arg1
	 *            second number
	 * @return greatest common divisor of the given numbers
	 */
	public static int gcd(final int arg0, final int arg1) {
		int rest;
		int num0 = Math.abs(arg0);
		int num1 = Math.abs(arg1);

		do {
			rest = num0 % num1;
			num0 = num1;
			num1 = rest;
		} while (rest > 0);

		return num0;
	}

	/**
	 * Least common multiple calculated by reducing the problem to GCD. See <a
	 * href="http://en.wikipedia.org/wiki/Least_common_multiple">http://en.
	 * wikipedia.org/wiki/Least_common_multiple</a>
	 * 
	 * @param arg0
	 *            first number
	 * @param arg1
	 *            second number
	 * @return least common multiple of the given numbers
	 */
	public static int lcm(final int arg0, final int arg1) {
		return Math.abs(arg0 * arg1) / MathOps.gcd(arg0, arg1);
	}

	/**
	 * Trivial function, still not contained in Java API... This function is the
	 * implementation of the mathematical mean function.
	 * 
	 * @param values
	 *            array of doubles
	 * @return mean of given values
	 */
	public static double mean(final double... values) {
		if (values == null || values.length == 0) {
			throw new IllegalArgumentException(
					"Mean can't be calculated from null or empty array");
		}
		return MathOps.sum(values) / values.length;
	}

	/**
	 * Fast and accurate implementation of the mathematical sum, taking in
	 * account floating point limitations. It considers the deficits of IEEE 754
	 * and sums up first small numbers. Thereby the asymptotic runtime is no
	 * more than O(2n log(n)).
	 * 
	 * @param numbers
	 *            Sequence of double values
	 * @return sum of given numbers
	 */
	public static Double sum(final Collection<Double> numbers) {
		if (numbers == null || numbers.isEmpty()) {
			return 0D;
		}

		final LinkedList<Double> nrs = new LinkedList<Double>(numbers);

		// sort big integers first
		final Comparator<Double> cmp = new Comparator<Double>() {
			@Override
			public int compare(final Double obj1, final Double obj2) {
				return Double.compare(Math.abs(obj1), Math.abs(obj2));
			}
		};
		// sort in order to used binary search for further operations
		Collections.sort(nrs, cmp);

		// sum it up!
		while (nrs.size() > 1) {
			Double v1 = nrs.pop();
			Double v2 = nrs.pop();
			if (Double.isNaN(v1) || Double.isNaN(v2)) {
				Activator
						.getDefault()
						.getLog()
						.log(new Status(
								IStatus.ERROR,
								Activator.PLUGIN_ID,
								String.format(
										"Either v1 = %s or v2 = %s is not a number...",
										v1, v2)));
			}
			final Double s0m = v1 + v2;
			MathOps.binaryPush(nrs, s0m);
		}
		return nrs.pop();
	}

	/**
	 * Accurate implementation of the mathematical sum, taking into account
	 * floating point limitations. It considers the deficits of IEEE 754 and
	 * sums up first small numbers. Thereby the asymptotic runtime is no more
	 * than O(2n log(n)).
	 * 
	 * @param numbers
	 *            Sequence of double values
	 * @return sum of given numbers
	 */
	public static double sum(final double... numbers) {
		if (numbers == null || numbers.length == 0) {
			return 0;
		}
		return MathOps.sum(Arrays.asList(CollectionUtils.wrap(numbers)));
	}

	public static double sumLogs(double... log_values) {
		double s = log_values[0];
		int k = 0;
		while (++k < log_values.length) {
			double x = Math.max(s, log_values[k]);
			double y = Math.min(s, log_values[k]);

			double c = y - x;
			s = x + Math.log(1 + Math.exp(c));
		}
		return s;
	}

	public static double sumLogs(Collection<Double> log_values) {
		Iterator<Double> it = log_values.iterator();
		Double s = it.next();
		while (it.hasNext()) {
			Double k = it.next();
			Double x = Math.max(s, k);
			Double y = Math.min(s, k);
			double c = y - x;
			s = x + Math.log(1 + Math.exp(c));
		}
		return s;
	}

	/**
	 * See above.
	 */
	public static double sum(final Double... numbers) {
		if (numbers == null || numbers.length == 0) {
			return 0;
		}
		return MathOps.sum(Arrays.asList(numbers));
	}
}
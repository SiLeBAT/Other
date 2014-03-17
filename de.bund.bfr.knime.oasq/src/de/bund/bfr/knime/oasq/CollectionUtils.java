package de.bund.bfr.knime.oasq;

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
import java.util.Iterator;
import java.util.List;

/**
 * General utility class.
 * 
 */
public final class CollectionUtils {

	private CollectionUtils() {
		// do not instantiate
	}

	/**
	 * Make a list out of an iterator.
	 * 
	 * @param <T>
	 *            Element type
	 * @param iterator
	 *            iterator in question.
	 * @return shiny list containing all elements from the iterator
	 */
	public static <T> List<T> toList(final Iterator<T> iterator) {
		final List<T> res = new ArrayList<T>();
		if (iterator == null) {
			return res;
		}
		while (iterator.hasNext()) {
			res.add(iterator.next());
		}
		return res;
	}

	/**
	 * Returns an array filled with consecutive values from a given interval,
	 * spaced by a given step width.
	 * 
	 * @param from
	 *            start value
	 * @param upTo
	 *            end value
	 * @param step
	 *            step width between to consecutive values of the result array
	 * @return array of integers from a given range
	 */
	public static int[] range(final int from, final int upTo, final int step) {
		if (step < 1) {
			throw new IllegalArgumentException(String.format(
					"Step size must be greater than 1, but is %s", step));
		}
		int[] res = new int[(int) Math.ceil(Math.abs(upTo - from)
				/ (double) step)];
		int pos = 0;
		if (from < upTo) {
			for (int i = from; i < upTo; i += step) {
				res[pos++] = i;
			}
			return res;
		}
		for (int i = from; i > upTo; i -= step) {
			res[pos++] = i;
		}
		return res;
	}

	/**
	 * Returns an array filled with consecutive values from a given interval.
	 * 
	 * @param from
	 *            start value
	 * @param upTo
	 *            end value
	 * @return array of integers from a given range
	 */
	public static int[] range(final int from, final int upTo) {
		return CollectionUtils.range(from, upTo, 1);
	}

	/**
	 * Convert int[] to Integer[].
	 * 
	 * @param ary
	 *            int[]
	 * @return Integer[]
	 */
	public static Integer[] wrap(final int[] ary) {
		// don't throw errors
		if (ary == null) {
			return null;
		}
		Integer[] res = new Integer[ary.length];
		for (int i = 0; i < ary.length; i++) {
			res[i] = Integer.valueOf(ary[i]);
		}
		return res;
	}

	/**
	 * Convert double[] to Double[].
	 * 
	 * @param ary
	 *            double[]
	 * @return Double[]
	 */
	public static Double[] wrap(final double[] ary) {
		// don't throw errors
		if (ary == null) {
			return null;
		}
		Double[] res = new Double[ary.length];
		for (int i = 0; i < ary.length; i++) {
			res[i] = Double.valueOf(ary[i]);
		}
		return res;
	}

	/**
	 * Convert Double[] to double[].
	 * 
	 * @param ary
	 *            Double[]
	 * @return double[]
	 */
	public static double[] unwrap(final Double[] ary) {
		// don't throw errors
		if (ary == null) {
			return null;
		}
		double[] res = new double[ary.length];
		for (int i = 0; i < ary.length; i++) {
			res[i] = ary[i].doubleValue();
		}
		return res;
	}

	/**
	 * Convert Integer[] to int[].
	 * 
	 * @param ary
	 *            Integer[]
	 * @return int[]
	 */
	public static int[] unwrap(final Integer[] ary) {
		// don't throw errors
		if (ary == null) {
			return null;
		}
		int[] res = new int[ary.length];
		for (int i = 0; i < ary.length; i++) {
			res[i] = ary[i].intValue();
		}
		return res;
	}

	/**
	 * Joins two Arrays in one. The first array in the first array1.length
	 * positions and the entries of the second array directly afterwards.
	 * 
	 * @param array1
	 *            first array.
	 * @param array2
	 *            second array.
	 * @return The joined Array.
	 */
	public static String[] joinTwoArrays(final String[] array1,
			final String[] array2) {
		final String[] joinedArray = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}
}
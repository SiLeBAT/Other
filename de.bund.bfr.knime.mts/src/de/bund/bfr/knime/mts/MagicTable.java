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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Implementation of A J Walker's alias method, aka Magic Table. The algorithm
 * provides sampling of arbitrary distributions over finite space.
 * 
 * @param <T>
 *            Type of objects to be sampled.
 */
public class MagicTable<T> {

	/* input entries */
	private List<T> entries;

	private int mean;

	private int[] magicTablePos;

	private double[] magicTableProbs;

	/* input probabilities */
	private double[] probs;

	private Random random;

	/**
	 * This constructor provides basic usage of magic table, if only
	 * probabilities are given. In return, the MagicTable class can only sample
	 * the respective index. That is, by creating a magic table using this
	 * constructor, <code>sampleObject()</code> method will always return null.
	 * 
	 * @param probabilities
	 *            arbitrary discrete distribution
	 * @throws MagicTableException
	 *             throws exception when input is not appropriate
	 */
	public MagicTable(final double... probabilities) throws MagicTableException {
		this(new ArrayList<T>(), probabilities);
	}

	/**
	 * By specifying an object list with respective probabilities, the
	 * MagicTable allows to sample objects by their respective probabilities by
	 * calling <code>sampleObject()</code>.
	 * 
	 * @param entries
	 *            Alphabet etc.
	 * @param probabilities
	 *            for each Object in same order
	 * @throws MagicTableException
	 *             throws exception when input is not appropriate
	 */
	public MagicTable(final List<T> entries, final double... probabilities)
			throws MagicTableException {
		this(new Random(), entries, probabilities);
	}

	/**
	 * This constructor provides basic usage of magic table, if only
	 * probabilities are given. In return, the MagicTable class can only sample
	 * the respective index. That is, by creating a magic table using this
	 * constructor, <code>sampleObject()</code> method will always return null.
	 * <em>The provided random object will be used to sample from the table.</em>
	 * 
	 * @param probabilities
	 *            arbitrary discrete distribution
	 * @param rand
	 *            BuiltInRandom object
	 * @throws MagicTableException
	 *             throws exception when input is not appropriate
	 */
	public MagicTable(final Random rand, final double... probabilities)
			throws MagicTableException {
		this(rand, new ArrayList<T>(), probabilities);
	}

	/**
	 * By specifying an object list with respective probabilities, the
	 * MagicTable allows to sample objects by their respective probabilities by
	 * calling <code>sampleObject()</code>.
	 * <em>The provided random object will be used to sample from the table.</em>
	 * 
	 * @param entries
	 *            Alphabet etc.
	 * @param probabilities
	 *            for each Object in same order
	 * @param rand
	 *            BuiltInRandom object
	 * @throws MagicTableException
	 *             throws exception when input is not appropriate
	 */
	public MagicTable(final Random rand, final List<T> entries,
			final double... probabilities) throws MagicTableException {
		this.random = rand;
		if (entries == null || probabilities == null) {
			throw new MagicTableException(
					"No input probabilities or entries given");
		}
		this.probs = probabilities.clone();
		this.mean = probabilities.length;
		this.entries = Collections.unmodifiableList(new ArrayList<T>(entries));

		if (!this.checkConsistency()) {
			throw new MagicTableException(
					"Magic table is inconsistent. Please check logfile for more information.");
		}

		this.createTable();
	}

	/**
	 * Checks if {@link MagicTable} is valid according to the constraints given
	 * by our model. No empty input, input probabilities should add up to 1.0,
	 * for each character there should be exactly one probability.
	 * 
	 * @return if {@link MagicTable} is valid or not.
	 */
	public final boolean checkConsistency() {
		Activator a = Activator.getDefault();
		if (a != null) {
			ILog log = Activator.getDefault().getLog();
			if (this.mean == 0) {
				log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"No input probabilities given"));
				return false;
			}

			if (!this.entries.isEmpty() && this.entries.size() != this.mean) {
				log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String
						.format("Entry size (#%s)  and no. of "
								+ "probabilities (#%s) are unequal.",
								this.entries.size(), this.mean)));
				return false;
			}
			if (!MathOps.checkIfDiscreteDistribution(this.probs)) {
				log.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String
						.format("Input probabilities do not obey laws of "
								+ "discrete distribution. Prabilities: %s",
								Arrays.toString(this.probs))));
				return false;
			}
			return true;
		} else {
			if (this.mean == 0) {
				System.err.println("No input probabilities given");
				return false;
			}

			if (!this.entries.isEmpty() && this.entries.size() != this.mean) {
				System.err.println(String.format(
						"Entry size (#%s)  and no. of "
								+ "probabilities (#%s) are unequal.",
						this.entries.size(), this.mean));
				return false;
			}
			if (!MathOps.checkIfDiscreteDistribution(this.probs)) {
				System.err.println(String.format(
						"Input probabilities do not obey laws of "
								+ "discrete distribution. Prabilities: %s",
						Arrays.toString(this.probs)));
				return false;
			}
			return true;
		}
	}

	/**
	 * Creates the Magic Table from the given entries of objects and
	 * probabilities.
	 */
	private void createTable() {
		// linearized magic table
		this.magicTableProbs = new double[2 * this.mean];
		this.magicTablePos = new int[2 * this.mean];

		final double mReciprocal = 1.0 / this.mean;
		final SortedMap<Double, List<Integer>> rest = new TreeMap<Double, List<Integer>>();
		for (int i = 0; i < this.probs.length; i++) {
			if (!rest.containsKey(this.probs[i])) {
				rest.put(this.probs[i], new ArrayList<Integer>());
			}
			rest.get(this.probs[i]).add(i);
		}

		for (int t = 0; t < this.mean; t++) {
			// calculate corresponding probabilities
			final double upr = rest.firstKey();
			final int index = rest.get(upr).get(0);
			final double lwr = mReciprocal - upr;

			// set probabilities in magic table
			this.magicTableProbs[t] = upr;
			this.magicTableProbs[t + this.mean] = lwr;

			// set indices in magic table
			this.magicTablePos[t] = index;
			double mxVal = rest.lastKey();
			final int maxJ = rest.get(mxVal).remove(0);
			this.magicTablePos[t + this.mean] = maxJ;

			// update r
			double nwVal = mxVal - lwr;
			if (!rest.containsKey(nwVal)) {
				rest.put(nwVal, new ArrayList<Integer>());
			}
			rest.get(nwVal).add(maxJ);

			// remove empty bins
			if (rest.get(upr).isEmpty()) {
				rest.remove(upr);
			}
			if (rest.containsKey(mxVal) && rest.get(mxVal).isEmpty()) {
				rest.remove(mxVal);
			}
		}
	}

	/**
	 * Returns the list of initial given objects.
	 * 
	 * @return initial given objects
	 */
	public List<T> getEntries() {
		return this.entries;
	}

	/**
	 * Returns the list of initial given probabilities.
	 * 
	 * @return initial given probabilities
	 */
	public double[] getProbabilities() {
		return this.probs.clone();
	}

	/**
	 * Samples the index from the {@link MagicTable} according to the respective
	 * probability.
	 * 
	 * @return one sampled index
	 */
	public int sampleIndex() {
		final int randomIndex = this.random.nextInt(this.mean);

		// choose cell (top or bottom)
		if (this.random.nextDouble() > this.mean
				* this.magicTableProbs[randomIndex]) {
			return this.magicTablePos[randomIndex + this.mean];
		}
		return this.magicTablePos[randomIndex];
	}

	/**
	 * Samples one object from the {@link MagicTable} according to the
	 * calculated probabilities.
	 * 
	 * @return one sampled Object
	 */
	public T sampleObject() {
		if (!this.entries.isEmpty()) {
			return this.entries.get(this.sampleIndex());
		}
		return null;
	}

	public void setRandom(final Random rand) {
		this.random = rand;
	}
}

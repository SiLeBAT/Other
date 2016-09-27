package flink_test;

import static org.apache.flink.api.java.aggregation.Aggregations.SUM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.functions.FunctionAnnotation.ForwardedFields;
import org.apache.flink.api.java.operators.IterativeDataSet;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.Collector;

/**
 * A basic implementation of the Page Rank algorithm using a bulk iteration.
 * 
 * <p>
 * This implementation requires a set of pages and a set of directed links as
 * input and works as follows. <br>
 * In each iteration, the rank of every page is evenly distributed to all pages
 * it points to. Each page collects the partial ranks of all pages that point to
 * it, sums them up, and applies a dampening factor to the sum. The result is
 * the new rank of the page. A new iteration is started with the new ranks of
 * all pages. This implementation terminates after a fixed number of iterations.
 * <br>
 * This is the Wikipedia entry for the
 * <a href="http://en.wikipedia.org/wiki/Page_rank">Page Rank algorithm</a>.
 * 
 * <p>
 * Input files are plain text files and must be formatted as follows:
 * <ul>
 * <li>Pages represented as an (String) ID separated by new-line characters.<br>
 * For example <code>"1\n2\n12\n42\n63"</code> gives five pages with IDs 1, 2,
 * 12, 42, and 63.
 * <li>Links are represented as pairs of page IDs which are separated by space
 * characters. Links are separated by new-line characters.<br>
 * For example <code>"1 2\n2 12\n1 12\n42 63"</code> gives four (directed) links
 * (1)-&gt;(2), (2)-&gt;(12), (1)-&gt;(12), and (42)-&gt;(63).<br>
 * For this simple implementation it is required that each page has at least one
 * incoming and one outgoing link (a page can point to itself).
 * </ul>
 * 
 * <p>
 * Usage:
 * <code>PageRankBasic --pages &lt;path&gt; --links &lt;path&gt; --output &lt;path&gt; --numPages &lt;n&gt; --iterations &lt;n&gt;</code>
 * <br>
 * If no parameters are provided, the program is run with default data from
 * {@link org.apache.flink.examples.java.graph.util.PageRankData} and 10
 * iterations.
 * 
 * <p>
 * This example shows how to use:
 * <ul>
 * <li>Bulk Iterations
 * <li>Default Join
 * <li>Configure user-defined functions using constructor parameters.
 * </ul>
 */
@SuppressWarnings("serial")
public class FlinkTest {

	private static final double DAMPENING_FACTOR = 0.85;
	private static final double EPSILON = 0.0001;

	// *************************************************************************
	// PROGRAM
	// *************************************************************************

	public static void main(String[] args) throws Exception {

		ParameterTool params = ParameterTool.fromArgs(args);

		final int numPages = params.getInt("numPages");
		final int maxIterations = params.getInt("iterations", 10);

		// set up execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		// make the parameters available to the web ui
		env.getConfig().setGlobalJobParameters(params);

		// get input data
		DataSet<String> pagesInput = getPagesDataSet(env, params);
		DataSet<Tuple2<String, String>> linksInput = getLinksDataSet(env, params);

		// assign initial rank to pages
		DataSet<Tuple2<String, Double>> pagesWithRanks = pagesInput.map(new RankAssigner((1.0d / numPages)));

		// build adjacency list from link input
		DataSet<Tuple2<String, String[]>> adjacencyListInput = linksInput.groupBy(0)
				.reduceGroup(new BuildOutgoingEdgeList());

		// set iterative data set
		IterativeDataSet<Tuple2<String, Double>> iteration = pagesWithRanks.iterate(maxIterations);

		DataSet<Tuple2<String, Double>> newRanks = iteration
				// join pages with outgoing edges and distribute rank
				.join(adjacencyListInput).where(0).equalTo(0).flatMap(new JoinVertexWithEdgesMatch())
				// collect and sum ranks
				.groupBy(0).aggregate(SUM, 1)
				// apply dampening factor
				.map(new Dampener(DAMPENING_FACTOR, numPages));

		DataSet<Tuple2<String, Double>> finalPageRanks = iteration.closeWith(newRanks,
				newRanks.join(iteration).where(0).equalTo(0)
						// termination condition
						.filter(new EpsilonFilter()));

		List<Tuple2<String, Double>> list = finalPageRanks.collect();

		Collections.sort(list, (t1, t2) -> t1.f1.compareTo(t2.f1));

		for (Tuple2<String, Double> t : list) {
			System.out.println(t.f0 + "\t" + t.f1);
		}
	}

	// *************************************************************************
	// USER FUNCTIONS
	// *************************************************************************

	/**
	 * A map function that assigns an initial rank to all pages.
	 */
	public static final class RankAssigner implements MapFunction<String, Tuple2<String, Double>> {
		Tuple2<String, Double> outPageWithRank;

		public RankAssigner(double rank) {
			this.outPageWithRank = new Tuple2<>(null, rank);
		}

		@Override
		public Tuple2<String, Double> map(String page) {
			outPageWithRank.f0 = page;
			return outPageWithRank;
		}
	}

	/**
	 * A reduce function that takes a sequence of edges and builds the adjacency
	 * list for the vertex where the edges originate. Run as a pre-processing
	 * step.
	 */
	@ForwardedFields("0")
	public static final class BuildOutgoingEdgeList
			implements GroupReduceFunction<Tuple2<String, String>, Tuple2<String, String[]>> {

		private final ArrayList<String> neighbors = new ArrayList<>();

		@Override
		public void reduce(Iterable<Tuple2<String, String>> values, Collector<Tuple2<String, String[]>> out) {
			neighbors.clear();
			String id = null;

			for (Tuple2<String, String> n : values) {
				id = n.f0;
				neighbors.add(n.f1);
			}
			out.collect(new Tuple2<>(id, neighbors.toArray(new String[neighbors.size()])));
		}
	}

	/**
	 * Join function that distributes a fraction of a vertex's rank to all
	 * neighbors.
	 */
	public static final class JoinVertexWithEdgesMatch implements
			FlatMapFunction<Tuple2<Tuple2<String, Double>, Tuple2<String, String[]>>, Tuple2<String, Double>> {

		@Override
		public void flatMap(Tuple2<Tuple2<String, Double>, Tuple2<String, String[]>> value,
				Collector<Tuple2<String, Double>> out) {
			String[] neighbors = value.f1.f1;
			double rank = value.f0.f1;
			double rankToDistribute = rank / neighbors.length;

			for (String neighbor : neighbors) {
				out.collect(new Tuple2<>(neighbor, rankToDistribute));
			}
		}
	}

	/**
	 * The function that applies the page rank dampening formula
	 */
	@ForwardedFields("0")
	public static final class Dampener implements MapFunction<Tuple2<String, Double>, Tuple2<String, Double>> {

		private final double dampening;
		private final double randomJump;

		public Dampener(double dampening, double numVertices) {
			this.dampening = dampening;
			this.randomJump = (1 - dampening) / numVertices;
		}

		@Override
		public Tuple2<String, Double> map(Tuple2<String, Double> value) {
			value.f1 = (value.f1 * dampening) + randomJump;
			return value;
		}
	}

	/**
	 * Filter that filters vertices where the rank difference is below a
	 * threshold.
	 */
	public static final class EpsilonFilter
			implements FilterFunction<Tuple2<Tuple2<String, Double>, Tuple2<String, Double>>> {

		@Override
		public boolean filter(Tuple2<Tuple2<String, Double>, Tuple2<String, Double>> value) {
			return Math.abs(value.f0.f1 - value.f1.f1) > EPSILON;
		}
	}

	// *************************************************************************
	// UTIL METHODS
	// *************************************************************************

	private static DataSet<String> getPagesDataSet(ExecutionEnvironment env, ParameterTool params) {
		return env.readCsvFile(params.get("pages")).fieldDelimiter(",").lineDelimiter("\n").types(String.class)
				.map(new MapFunction<Tuple1<String>, String>() {
					@Override
					public String map(Tuple1<String> v) {
						return v.f0;
					}
				});
	}

	private static DataSet<Tuple2<String, String>> getLinksDataSet(ExecutionEnvironment env, ParameterTool params) {
		return env.readCsvFile(params.get("links")).fieldDelimiter(",").lineDelimiter("\n").types(String.class,
				String.class);
	}
}
/*******************************************************************************
 * Copyright (c) 2016 German Federal Institute for Risk Assessment (BfR)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Department Biological Safety - BfR
 *******************************************************************************/
package de.bund.bfr.flink;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class Closeness {

	@SuppressWarnings("serial")
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Usage: Closeness <nodes file> <edges file> <result path>");
			return;
		}

		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		final String nodesFile = args[0];
		final String edgesFile = args[1];
		final String resultPath = args[2];
		final int parallelism = env.getParallelism();

		DataSet<Tuple2<Map<String, Set<Long>>, Map<Long, Set<String>>>> graph = env.readCsvFile(edgesFile)
				.types(Long.class, Long.class).reduceGroup(
						new GroupReduceFunction<Tuple2<Long, Long>, Tuple2<Map<String, Set<Long>>, Map<Long, Set<String>>>>() {

							@Override
							public void reduce(Iterable<Tuple2<Long, Long>> lines,
									Collector<Tuple2<Map<String, Set<Long>>, Map<Long, Set<String>>>> collector)
									throws Exception {
								Map<String, Set<Long>> incidentNodes = new HashMap<>();
								Map<Long, Set<String>> outgoingEdges = new HashMap<>();

								for (Tuple2<Long, Long> line : lines) {
									if (!incidentNodes.containsKey(line.toString())) {
										incidentNodes.put(line.toString(), new HashSet<Long>());
									}

									if (!outgoingEdges.containsKey(line.f0)) {
										outgoingEdges.put(line.f0, new HashSet<String>());
									}

									if (!outgoingEdges.containsKey(line.f1)) {
										outgoingEdges.put(line.f1, new HashSet<String>());
									}

									incidentNodes.get(line.toString()).add(line.f0);
									incidentNodes.get(line.toString()).add(line.f1);
									outgoingEdges.get(line.f0).add(line.toString());
									outgoingEdges.get(line.f1).add(line.toString());
								}

								collector.collect(new Tuple2<>(incidentNodes, outgoingEdges));
							}
						});

		DataSet<List<Long>> nodeLists = env.readFileOfPrimitives(nodesFile, Long.class)
				.reduceGroup(new GroupReduceFunction<Long, List<Long>>() {

					@Override
					public void reduce(Iterable<Long> nodes, Collector<List<Long>> collector) throws Exception {
						List<List<Long>> nodeLists = new ArrayList<>();

						for (int i = 0; i < parallelism; i++) {
							nodeLists.add(new ArrayList<Long>());
						}

						int i = 0;

						for (Long node : nodes) {
							nodeLists.get(i).add(node);
							i = (i + 1) % parallelism;
						}

						for (List<Long> list : nodeLists) {
							collector.collect(list);
						}
					}
				});

		nodeLists.cross(graph).flatMap(
				new FlatMapFunction<Tuple2<List<Long>, Tuple2<Map<String, Set<Long>>, Map<Long, Set<String>>>>, Tuple2<Long, Double>>() {

					@Override
					public void flatMap(
							Tuple2<List<Long>, Tuple2<Map<String, Set<Long>>, Map<Long, Set<String>>>> idListWithGraph,
							Collector<Tuple2<Long, Double>> collector) throws Exception {
						Map<String, Set<Long>> incidentNodes = idListWithGraph.f1.f0;
						Map<Long, Set<String>> outgoingEdges = idListWithGraph.f1.f1;
						int numberOfNodes = outgoingEdges.size();
						int numberOfEdges = incidentNodes.size();

						for (long nodeId : idListWithGraph.f0) {
							Deque<Long> nodeQueue = new LinkedList<>();
							Map<Long, Integer> visitedNodes = new HashMap<>(numberOfNodes, 1.0f);
							Set<String> visitedEdges = new HashSet<>(numberOfEdges, 1.0f);
							int distanceSum = 0;

							visitedNodes.put(nodeId, 0);
							nodeQueue.addLast(nodeId);

							while (!nodeQueue.isEmpty()) {
								long currentNodeId = nodeQueue.removeFirst();
								int targetNodeDistance = visitedNodes.get(currentNodeId) + 1;

								for (String edgeId : outgoingEdges.get(currentNodeId)) {
									if (visitedEdges.add(edgeId)) {
										for (long targetNodeId : incidentNodes.get(edgeId)) {
											if (currentNodeId != targetNodeId
													&& !visitedNodes.containsKey(targetNodeId)) {
												visitedNodes.put(targetNodeId, targetNodeDistance);
												nodeQueue.addLast(targetNodeId);
												distanceSum += targetNodeDistance;
											}
										}
									}
								}
							}

							collector.collect(new Tuple2<>(nodeId,
									1.0 / (distanceSum + (numberOfNodes - visitedNodes.size()) * numberOfNodes)));
						}
					}
				}).writeAsCsv(resultPath);

		env.execute("Closeness Centrality Example");
	}
}

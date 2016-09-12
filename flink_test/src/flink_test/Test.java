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
package flink_test;

import java.util.List;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;

public class Test {

	public static void main(String[] args) throws Exception {
		ExecutionEnvironment env = ExecutionEnvironment.createLocalEnvironment();
		DataSet<String> data = env.readTextFile(Test.class.getResource("/flink_test/in.txt").toString());
		List<String> list = data.filter(new FilterFunction<String>() {
			private static final long serialVersionUID = 1L;

			public boolean filter(String value) {
				return value.startsWith("http://");
			}
		}).collect();

		for (String s : list) {
			System.out.println(s);
		}
	}
}

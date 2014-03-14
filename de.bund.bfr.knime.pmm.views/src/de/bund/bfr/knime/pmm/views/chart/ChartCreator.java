/*******************************************************************************
 * PMM-Lab � 2012, Federal Institute for Risk Assessment (BfR), Germany
 * 
 * PMM-Lab is a set of KNIME-Nodes and KNIME workflows running within the KNIME software plattform (http://www.knime.org.).
 * 
 * PMM-Lab � 2012, Federal Institute for Risk Assessment (BfR), Germany
 * Contact: armin.weiser@bfr.bund.de or matthias.filter@bfr.bund.de 
 * 
 * Developers and contributors to the PMM-Lab project are 
 * Joergen Brandt (BfR)
 * Armin A. Weiser (BfR)
 * Matthias Filter (BfR)
 * Alexander Falenski (BfR)
 * Christian Thoens (BfR)
 * Annemarie Kaesbohrer (BfR)
 * Bernd Appel (BfR)
 * 
 * PMM-Lab is a project under development. Contributions are welcome.
 * 
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
 ******************************************************************************/
package de.bund.bfr.knime.pmm.views.chart;

import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

import de.bund.bfr.knime.pmm.core.ColorAndShapeCreator;
import de.bund.bfr.knime.pmm.core.Utilities;
import de.bund.bfr.knime.pmm.core.common.Unit;
import de.bund.bfr.knime.pmm.core.math.ConvertException;
import de.bund.bfr.knime.pmm.core.ui.StandardFileFilter;

public class ChartCreator extends ChartPanel {

	private static final long serialVersionUID = 1L;

	private Map<String, Plotable> plotables;
	private Map<String, String> shortLegend;
	private Map<String, String> longLegend;
	private Map<String, Color> colors;
	private Map<String, Shape> shapes;
	private Map<String, List<Color>> colorLists;
	private Map<String, List<Shape>> shapeLists;

	private String paramX;
	private String paramY;
	private Unit unitX;
	private Unit unitY;
	private String transformX;
	private String transformY;
	private boolean useManualRange;
	private double minX;
	private double minY;
	private double maxX;
	private double maxY;
	private boolean drawLines;
	private boolean showLegend;
	private boolean addInfoInLegend;
	private boolean showConfidenceInterval;

	public ChartCreator(Plotable plotable) {
		super(new JFreeChart(new XYPlot()));
		getPopupMenu().insert(new DataAndModelChartSaveAsItem(), 4);
		plotables = new LinkedHashMap<String, Plotable>();
		shortLegend = new LinkedHashMap<String, String>();
		longLegend = new LinkedHashMap<String, String>();
		colors = new LinkedHashMap<String, Color>();
		shapes = new LinkedHashMap<String, Shape>();
		colorLists = new LinkedHashMap<String, List<Color>>();
		shapeLists = new LinkedHashMap<String, List<Shape>>();

		plotables.put("", plotable);
		shortLegend.put("", "");
		longLegend.put("", "");
	}

	public ChartCreator(Map<String, Plotable> plotables,
			Map<String, String> shortLegend, Map<String, String> longLegend) {
		super(new JFreeChart(new XYPlot()));
		getPopupMenu().insert(new DataAndModelChartSaveAsItem(), 4);
		this.plotables = plotables;
		this.shortLegend = shortLegend;
		this.longLegend = longLegend;
		colors = new LinkedHashMap<String, Color>();
		shapes = new LinkedHashMap<String, Shape>();
		colorLists = new LinkedHashMap<String, List<Color>>();
		shapeLists = new LinkedHashMap<String, List<Shape>>();
	}

	public void createChart() {
		setChart(getChart(new ArrayList<String>(plotables.keySet())));
	}

	public void createChart(String idToPaint) {
		setChart(getChart(idToPaint));
	}

	public void createChart(List<String> idsToPaint) {
		setChart(getChart(idsToPaint));
	}

	public JFreeChart getChart(String idToPaint) {
		if (idToPaint != null) {
			return getChart(Arrays.asList(idToPaint));
		} else {
			return getChart(new ArrayList<String>());
		}
	}

	public JFreeChart getChart(List<String> idsToPaint) {
		if (paramX == null || paramY == null) {
			return new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT,
					new XYPlot(), showLegend);
		}

		NumberAxis xAxis = new NumberAxis(Utilities.getNameWithUnit(paramX,
				unitX, transformX));
		NumberAxis yAxis = new NumberAxis(Utilities.getNameWithUnit(paramY,
				unitY, transformY));
		XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
		double usedMinX = Double.POSITIVE_INFINITY;
		double usedMaxX = Double.NEGATIVE_INFINITY;
		int index = 0;
		ColorAndShapeCreator colorAndShapeCreator = new ColorAndShapeCreator(
				idsToPaint.size());

		for (String id : idsToPaint) {
			Plotable plotable = plotables.get(id);

			try {
				if (plotable != null) {
					if (plotable.getType() == Plotable.BOTH
							|| plotable.getType() == Plotable.BOTH_STRICT) {
						Double minArg = Plotable.transform(plotable
								.convertToUnit(paramX, plotable
										.getMinArguments().get(paramX), unitX),
								transformX);
						Double maxArg = Plotable.transform(plotable
								.convertToUnit(paramX, plotable
										.getMaxArguments().get(paramX), unitX),
								transformX);

						if (minArg != null) {
							usedMinX = Math.min(usedMinX, minArg);
						}

						if (maxArg != null) {
							usedMaxX = Math.max(usedMaxX, maxArg);
						}

						for (Map<String, Integer> choice : plotable
								.getAllChoices()) {
							double[][] points = plotable.getPoints(paramX,
									paramY, unitX, unitY, transformX,
									transformY, choice);

							if (points != null) {
								for (int i = 0; i < points[0].length; i++) {
									usedMinX = Math.min(usedMinX, points[0][i]);
									usedMaxX = Math.max(usedMaxX, points[0][i]);
								}
							}
						}
					} else if (plotable.getType() == Plotable.DATASET
							|| plotable.getType() == Plotable.DATASET_STRICT) {
						double[][] points = plotable.getPoints(paramX, paramY,
								unitX, unitY, transformX, transformY);

						if (points != null) {
							for (int i = 0; i < points[0].length; i++) {
								usedMinX = Math.min(usedMinX, points[0][i]);
								usedMaxX = Math.max(usedMaxX, points[0][i]);
							}
						}
					} else if (plotable.getType() == Plotable.FUNCTION) {
						Double minArg = Plotable.transform(plotable
								.convertToUnit(paramX, plotable
										.getMinArguments().get(paramX), unitX),
								transformX);
						Double maxArg = Plotable.transform(plotable
								.convertToUnit(paramX, plotable
										.getMaxArguments().get(paramX), unitX),
								transformX);

						if (minArg != null) {
							usedMinX = Math.min(usedMinX, minArg);
						}

						if (maxArg != null) {
							usedMaxX = Math.max(usedMaxX, maxArg);
						}
					} else if (plotable.getType() == Plotable.FUNCTION_SAMPLE) {
						Double minArg = Plotable.transform(plotable
								.convertToUnit(paramX, plotable
										.getMinArguments().get(paramX), unitX),
								transformX);
						Double maxArg = Plotable.transform(plotable
								.convertToUnit(paramX, plotable
										.getMaxArguments().get(paramX), unitX),
								transformX);

						if (minArg != null) {
							usedMinX = Math.min(usedMinX, minArg);
						}

						if (maxArg != null) {
							usedMaxX = Math.max(usedMaxX, maxArg);
						}

						for (Double x : plotable.getSamples()) {
							if (isValid(x)) {
								usedMinX = Math.min(usedMinX, x);
								usedMaxX = Math.max(usedMaxX, x);
							}
						}
					}
				}
			} catch (ConvertException e) {
			}
		}

		if (Double.isInfinite(usedMinX)) {
			usedMinX = 0.0;
		}

		if (Double.isInfinite(usedMaxX)) {
			usedMaxX = 100.0;
		}

		if (paramX.equals(Utilities.TIME)
				|| paramX.equals(Utilities.CONCENTRATION)) {
			usedMinX = Math.min(usedMinX, 0.0);
			xAxis.setAutoRangeIncludesZero(true);
		} else {
			xAxis.setAutoRangeIncludesZero(false);
		}

		if (paramY.equals(Utilities.TIME)
				|| paramY.equals(Utilities.CONCENTRATION)) {
			yAxis.setAutoRangeIncludesZero(true);
		} else {
			yAxis.setAutoRangeIncludesZero(false);
		}

		if (usedMinX == usedMaxX) {
			usedMinX -= 1.0;
			usedMaxX += 1.0;
		}

		if (useManualRange && minX < maxX && minY < maxY) {
			usedMinX = minX;
			usedMaxX = maxX;
			xAxis.setRange(new Range(minX, maxX));
			yAxis.setRange(new Range(minY, maxY));
		}

		boolean conversionFailed = false;

		for (String id : idsToPaint) {
			Plotable plotable = plotables.get(id);

			if (plotable != null && plotable.getType() == Plotable.DATASET) {
				try {
					plotDataSet(plot, plotable, id, colorAndShapeCreator
							.getColorList().get(index), colorAndShapeCreator
							.getShapeList().get(index));
					index++;
				} catch (ConvertException e) {
					conversionFailed = true;
				}
			}
		}

		for (String id : idsToPaint) {
			Plotable plotable = plotables.get(id);

			if (plotable != null
					&& plotable.getType() == Plotable.DATASET_STRICT) {
				try {
					plotDataSetStrict(plot, plotable, id);
					index++;
				} catch (ConvertException e) {
					conversionFailed = true;
				}
			}
		}

		for (String id : idsToPaint) {
			Plotable plotable = plotables.get(id);

			if (plotable != null && plotable.getType() == Plotable.FUNCTION) {
				try {
					plotFunction(plot, plotable, id, colorAndShapeCreator
							.getColorList().get(index), colorAndShapeCreator
							.getShapeList().get(index), usedMinX, usedMaxX);
					index++;
				} catch (ConvertException e) {
					conversionFailed = true;
				}
			}
		}

		for (String id : idsToPaint) {
			Plotable plotable = plotables.get(id);

			if (plotable != null
					&& plotable.getType() == Plotable.FUNCTION_SAMPLE) {
				try {
					plotFunctionSample(plot, plotable, id, colorAndShapeCreator
							.getColorList().get(index), colorAndShapeCreator
							.getShapeList().get(index), usedMinX, usedMaxX);
					index++;
				} catch (ConvertException e) {
					conversionFailed = true;
				}
			}
		}

		for (String id : idsToPaint) {
			Plotable plotable = plotables.get(id);

			if (plotable != null && plotable.getType() == Plotable.BOTH) {
				try {
					plotBoth(plot, plotable, id, colorAndShapeCreator
							.getColorList().get(index), colorAndShapeCreator
							.getShapeList().get(index), usedMinX, usedMaxX);
					index++;
				} catch (ConvertException e) {
					conversionFailed = true;
				}
			}
		}

		for (String id : idsToPaint) {
			Plotable plotable = plotables.get(id);

			if (plotable != null && plotable.getType() == Plotable.BOTH_STRICT) {
				try {
					plotBothStrict(plot, plotable, id, usedMinX, usedMaxX);
					index++;
				} catch (ConvertException e) {
					conversionFailed = true;
				}
			}
		}

		if (conversionFailed) {
			JOptionPane
					.showMessageDialog(
							this,
							"Some datasets/functions cannot be converted to the desired unit",
							"Warning", JOptionPane.WARNING_MESSAGE);
		}

		return new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot,
				showLegend);
	}

	public void setParamX(String paramX) {
		this.paramX = paramX;
	}

	public void setParamY(String paramY) {
		this.paramY = paramY;
	}

	public void setUnitX(Unit unitX) {
		this.unitX = unitX;
	}

	public void setUnitY(Unit unitY) {
		this.unitY = unitY;
	}

	public void setTransformX(String transformX) {
		this.transformX = transformX;
	}

	public void setTransformY(String transformY) {
		this.transformY = transformY;
	}

	public void setManualRange(boolean useManualRange) {
		this.useManualRange = useManualRange;
	}

	public void setMinX(double minX) {
		this.minX = minX;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}

	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public void setDrawLines(boolean drawLines) {
		this.drawLines = drawLines;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	public void setAddLegendInfo(boolean addInfoInLegend) {
		this.addInfoInLegend = addInfoInLegend;
	}

	public void setShowConfidence(boolean showConfidenceInterval) {
		this.showConfidenceInterval = showConfidenceInterval;
	}

	public void setColors(Map<String, Color> colors) {
		this.colors = colors;
	}

	public void setShapes(Map<String, Shape> shapes) {
		this.shapes = shapes;
	}

	public void setColorLists(Map<String, List<Color>> colorLists) {
		this.colorLists = colorLists;
	}

	public void setShapeLists(Map<String, List<Shape>> shapeLists) {
		this.shapeLists = shapeLists;
	}

	private void plotDataSet(XYPlot plot, Plotable plotable, String id,
			Color defaultColor, Shape defaultShape) throws ConvertException {
		double[][] points = plotable.getPoints(paramX, paramY, unitX, unitY,
				transformX, transformY);
		String legend = shortLegend.get(id);
		Color color = colors.get(id);
		Shape shape = shapes.get(id);

		if (addInfoInLegend) {
			legend = longLegend.get(id);
		}

		if (color == null) {
			color = defaultColor;
		}

		if (shape == null) {
			shape = defaultShape;
		}

		if (points != null) {
			DefaultXYDataset dataset = new DefaultXYDataset();
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(
					drawLines, true);

			dataset.addSeries(legend, points);
			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			renderer.setSeriesPaint(0, color);
			renderer.setSeriesShape(0, shape);

			int i;

			if (plot.getDataset(0) == null) {
				i = 0;
			} else {
				i = plot.getDatasetCount();
			}

			plot.setDataset(i, dataset);
			plot.setRenderer(i, renderer);
		}
	}

	private void plotDataSetStrict(XYPlot plot, Plotable plotable, String id)
			throws ConvertException {
		String legend = shortLegend.get(id);
		List<Color> colorList = colorLists.get(id);
		List<Shape> shapeList = shapeLists.get(id);
		ColorAndShapeCreator creator = new ColorAndShapeCreator(
				plotable.getNumberOfCombinations());
		int index = 0;

		if (addInfoInLegend) {
			legend = longLegend.get(id);
		}

		if (colorList == null || colorList.isEmpty()) {
			colorList = creator.getColorList();
		}

		if (shapeList == null || shapeList.isEmpty()) {
			shapeList = creator.getShapeList();
		}

		for (Map<String, Integer> choiceMap : plotable.getAllChoices()) {
			double[][] dataPoints = plotable.getPoints(paramX, paramY, unitX,
					unitY, transformX, transformY, choiceMap);

			if (dataPoints != null) {
				DefaultXYDataset dataSet = new DefaultXYDataset();
				XYLineAndShapeRenderer dataRenderer = new XYLineAndShapeRenderer(
						drawLines, true);
				String addLegend = "";

				for (String arg : choiceMap.keySet()) {
					if (!arg.equals(paramX)) {
						addLegend += " ("
								+ arg
								+ "="
								+ plotable.getFunctionArguments().get(arg)
										.get(choiceMap.get(arg)) + ")";
					}
				}

				dataSet.addSeries(legend + addLegend, dataPoints);
				dataRenderer
						.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
				dataRenderer.setSeriesPaint(0, colorList.get(index));
				dataRenderer.setSeriesShape(0, shapeList.get(index));

				int i;

				if (plot.getDataset(0) == null) {
					i = 0;
				} else {
					i = plot.getDatasetCount();
				}

				plot.setDataset(i, dataSet);
				plot.setRenderer(i, dataRenderer);
			}

			index++;
		}
	}

	private void plotFunction(XYPlot plot, Plotable plotable, String id,
			Color defaultColor, Shape defaultShape, double minX, double maxX)
			throws ConvertException {
		double[][] points = plotable.getFunctionPoints(paramX, paramY, unitX,
				unitY, transformX, transformY, minX, maxX,
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		double[][] functionErrors = null;
		String legend = shortLegend.get(id);
		Color color = colors.get(id);
		Shape shape = shapes.get(id);

		if (showConfidenceInterval) {
			functionErrors = plotable.getFunctionErrors(paramX, paramY, unitX,
					unitY, transformX, transformY, minX, maxX,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		}

		if (addInfoInLegend) {
			legend = longLegend.get(id);
		}

		if (color == null) {
			color = defaultColor;
		}

		if (shape == null) {
			shape = defaultShape;
		}

		if (points != null) {
			int i;

			if (plot.getDataset(0) == null) {
				i = 0;
			} else {
				i = plot.getDatasetCount();
			}

			if (functionErrors != null) {
				YIntervalSeriesCollection functionDataset = new YIntervalSeriesCollection();
				DeviationRenderer functionRenderer = new DeviationRenderer(
						true, false);
				YIntervalSeries series = new YIntervalSeries(legend);

				for (int j = 0; j < points[0].length; j++) {
					double error = Double.isNaN(functionErrors[1][j]) ? 0.0
							: functionErrors[1][j];

					series.add(points[0][j], points[1][j],
							points[1][j] - error, points[1][j] + error);
				}

				functionDataset.addSeries(series);
				functionRenderer
						.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
				functionRenderer.setSeriesPaint(0, color);
				functionRenderer.setSeriesFillPaint(0, color);
				functionRenderer.setSeriesShape(0, shape);

				plot.setDataset(i, functionDataset);
				plot.setRenderer(i, functionRenderer);
			} else {
				DefaultXYDataset functionDataset = new DefaultXYDataset();
				XYLineAndShapeRenderer functionRenderer = new XYLineAndShapeRenderer(
						true, false);

				functionDataset.addSeries(legend, points);
				functionRenderer
						.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
				functionRenderer.setSeriesPaint(0, color);
				functionRenderer.setSeriesShape(0, shape);

				plot.setDataset(i, functionDataset);
				plot.setRenderer(i, functionRenderer);
			}
		}
	}

	private void plotFunctionSample(XYPlot plot, Plotable plotable, String id,
			Color defaultColor, Shape defaultShape, double minX, double maxX)
			throws ConvertException {
		double[][] functionPoints = plotable.getFunctionPoints(paramX, paramY,
				unitX, unitY, transformX, transformY, minX, maxX,
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		double[][] samplePoints = plotable.getFunctionSamplePoints(paramX,
				paramY, unitX, unitY, transformX, transformY, minX, maxX,
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		double[][] functionErrors = null;
		String legend = shortLegend.get(id);
		Color color = colors.get(id);
		Shape shape = shapes.get(id);

		if (showConfidenceInterval) {
			functionErrors = plotable.getFunctionErrors(paramX, paramY, unitX,
					unitY, transformX, transformY, minX, maxX,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		}

		if (addInfoInLegend) {
			legend = longLegend.get(id);
		}

		if (color == null) {
			color = defaultColor;
		}

		if (shape == null) {
			shape = defaultShape;
		}

		if (functionPoints != null) {
			int i;

			if (plot.getDataset(0) == null) {
				i = 0;
			} else {
				i = plot.getDatasetCount();
			}

			if (functionErrors != null) {
				YIntervalSeriesCollection functionDataset = new YIntervalSeriesCollection();
				DeviationRenderer functionRenderer = new DeviationRenderer(
						true, false);
				YIntervalSeries series = new YIntervalSeries(legend);

				for (int j = 0; j < functionPoints[0].length; j++) {
					double error = Double.isNaN(functionErrors[1][j]) ? 0.0
							: functionErrors[1][j];

					series.add(functionPoints[0][j], functionPoints[1][j],
							functionPoints[1][j] - error, functionPoints[1][j]
									+ error);
				}

				functionDataset.addSeries(series);
				functionRenderer
						.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
				functionRenderer.setSeriesPaint(0, color);
				functionRenderer.setSeriesFillPaint(0, color);
				functionRenderer.setSeriesShape(0, shape);

				if (samplePoints != null) {
					functionRenderer.setBaseSeriesVisibleInLegend(false);
				}

				plot.setDataset(i, functionDataset);
				plot.setRenderer(i, functionRenderer);
			} else {
				DefaultXYDataset functionDataset = new DefaultXYDataset();
				XYLineAndShapeRenderer functionRenderer = new XYLineAndShapeRenderer(
						true, false);

				functionDataset.addSeries(legend, functionPoints);
				functionRenderer
						.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
				functionRenderer.setSeriesPaint(0, color);
				functionRenderer.setSeriesShape(0, shape);

				if (samplePoints != null) {
					functionRenderer.setBaseSeriesVisibleInLegend(false);
				}

				plot.setDataset(i, functionDataset);
				plot.setRenderer(i, functionRenderer);
			}

			if (samplePoints != null) {
				DefaultXYDataset sampleDataset = new DefaultXYDataset();
				XYLineAndShapeRenderer sampleRenderer = new XYLineAndShapeRenderer(
						false, true);

				sampleDataset.addSeries(legend, samplePoints);
				sampleRenderer
						.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
				sampleRenderer.setSeriesPaint(0, color);
				sampleRenderer.setSeriesShape(0, shape);

				plot.setDataset(i + 1, sampleDataset);
				plot.setRenderer(i + 1, sampleRenderer);
			}
		}
	}

	private void plotBoth(XYPlot plot, Plotable plotable, String id,
			Color defaultColor, Shape defaultShape, double minX, double maxX)
			throws ConvertException {
		double[][] modelPoints = plotable.getFunctionPoints(paramX, paramY,
				unitX, unitY, transformX, transformY, minX, maxX,
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		double[][] dataPoints = plotable.getPoints(paramX, paramY, unitX,
				unitY, transformX, transformY);
		double[][] functionErrors = null;
		String legend = shortLegend.get(id);
		Color color = colors.get(id);
		Shape shape = shapes.get(id);

		if (showConfidenceInterval) {
			functionErrors = plotable.getFunctionErrors(paramX, paramY, unitX,
					unitY, transformX, transformY, minX, maxX,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		}

		if (addInfoInLegend) {
			legend = longLegend.get(id);
		}

		if (color == null) {
			color = defaultColor;
		}

		if (shape == null) {
			shape = defaultShape;
		}

		if (modelPoints != null) {
			int i;

			if (plot.getDataset(0) == null) {
				i = 0;
			} else {
				i = plot.getDatasetCount();
			}

			if (functionErrors != null) {
				YIntervalSeriesCollection functionDataset = new YIntervalSeriesCollection();
				DeviationRenderer functionRenderer = new DeviationRenderer(
						true, false);
				YIntervalSeries series = new YIntervalSeries(legend);

				for (int j = 0; j < modelPoints[0].length; j++) {
					double error = Double.isNaN(functionErrors[1][j]) ? 0.0
							: functionErrors[1][j];

					series.add(modelPoints[0][j], modelPoints[1][j],
							modelPoints[1][j] - error, modelPoints[1][j]
									+ error);
				}

				functionDataset.addSeries(series);
				functionRenderer
						.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
				functionRenderer.setSeriesPaint(0, color);
				functionRenderer.setSeriesFillPaint(0, color);
				functionRenderer.setSeriesShape(0, shape);

				if (dataPoints != null) {
					functionRenderer.setBaseSeriesVisibleInLegend(false);
				}

				plot.setDataset(i, functionDataset);
				plot.setRenderer(i, functionRenderer);
			} else {
				DefaultXYDataset functionDataset = new DefaultXYDataset();
				XYLineAndShapeRenderer functionRenderer = new XYLineAndShapeRenderer(
						true, false);

				functionDataset.addSeries(legend, modelPoints);
				functionRenderer
						.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
				functionRenderer.setSeriesPaint(0, color);
				functionRenderer.setSeriesShape(0, shape);

				if (dataPoints != null) {
					functionRenderer.setBaseSeriesVisibleInLegend(false);
				}

				plot.setDataset(i, functionDataset);
				plot.setRenderer(i, functionRenderer);
			}
		}

		if (dataPoints != null) {
			DefaultXYDataset dataSet = new DefaultXYDataset();
			XYLineAndShapeRenderer dataRenderer = new XYLineAndShapeRenderer(
					drawLines, true);

			dataSet.addSeries(legend, dataPoints);
			dataRenderer
					.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			dataRenderer.setSeriesPaint(0, color);
			dataRenderer.setSeriesShape(0, shape);

			int i;

			if (plot.getDataset(0) == null) {
				i = 0;
			} else {
				i = plot.getDatasetCount();
			}

			plot.setDataset(i, dataSet);
			plot.setRenderer(i, dataRenderer);
		}
	}

	private void plotBothStrict(XYPlot plot, Plotable plotable, String id,
			double minX, double maxX) throws ConvertException {
		String legend = shortLegend.get(id);
		List<Color> colorList = colorLists.get(id);
		List<Shape> shapeList = shapeLists.get(id);
		ColorAndShapeCreator creator = new ColorAndShapeCreator(
				plotable.getNumberOfCombinations());
		int index = 0;

		if (addInfoInLegend) {
			legend = longLegend.get(id);
		}

		if (colorList == null || colorList.isEmpty()) {
			colorList = creator.getColorList();
		}

		if (shapeList == null || shapeList.isEmpty()) {
			shapeList = creator.getShapeList();
		}

		for (Map<String, Integer> choiceMap : plotable.getAllChoices()) {
			double[][] dataPoints = plotable.getPoints(paramX, paramY, unitX,
					unitY, transformX, transformY, choiceMap);

			if (dataPoints == null) {
				continue;
			}

			double[][] modelPoints = plotable.getFunctionPoints(paramX, paramY,
					unitX, unitY, transformX, transformY, minX, maxX,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
					choiceMap);

			if (modelPoints == null) {
				continue;
			}

			double[][] modelErrors = null;

			if (showConfidenceInterval) {
				modelErrors = plotable.getFunctionErrors(paramX, paramY, unitX,
						unitY, transformX, transformY, minX, maxX,
						Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			}

			int i;

			if (plot.getDataset(0) == null) {
				i = 0;
			} else {
				i = plot.getDatasetCount();
			}

			String addLegend = "";

			for (String arg : choiceMap.keySet()) {
				if (!arg.equals(paramX)) {
					addLegend += " ("
							+ arg
							+ "="
							+ plotable.getFunctionArguments().get(arg)
									.get(choiceMap.get(arg)) + ")";
				}
			}

			if (modelErrors != null) {
				YIntervalSeriesCollection modelSet = new YIntervalSeriesCollection();
				DeviationRenderer modelRenderer = new DeviationRenderer(true,
						false);
				YIntervalSeries series = new YIntervalSeries(legend);

				for (int j = 0; j < modelPoints[0].length; j++) {
					double error = Double.isNaN(modelErrors[1][j]) ? 0.0
							: modelErrors[1][j];

					series.add(modelPoints[0][j], modelPoints[1][j],
							modelPoints[1][j] - error, modelPoints[1][j]
									+ error);
				}

				modelSet.addSeries(series);
				modelRenderer
						.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
				modelRenderer.setSeriesPaint(0, colorList.get(index));
				modelRenderer.setSeriesFillPaint(0, colorList.get(index));
				modelRenderer.setSeriesShape(0, shapeList.get(index));

				if (dataPoints != null) {
					modelRenderer.setBaseSeriesVisibleInLegend(false);
				}

				plot.setDataset(i, modelSet);
				plot.setRenderer(i, modelRenderer);
			} else {
				DefaultXYDataset modelSet = new DefaultXYDataset();
				XYLineAndShapeRenderer modelRenderer = new XYLineAndShapeRenderer(
						true, false);

				modelSet.addSeries(legend + addLegend, modelPoints);
				modelRenderer
						.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
				modelRenderer.setBaseSeriesVisibleInLegend(false);
				modelRenderer.setSeriesPaint(0, colorList.get(index));
				modelRenderer.setSeriesShape(0, shapeList.get(index));

				plot.setDataset(i, modelSet);
				plot.setRenderer(i, modelRenderer);
			}

			DefaultXYDataset dataSet = new DefaultXYDataset();
			XYLineAndShapeRenderer dataRenderer = new XYLineAndShapeRenderer(
					drawLines, true);

			dataSet.addSeries(legend + addLegend, dataPoints);
			dataRenderer
					.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			dataRenderer.setSeriesPaint(0, colorList.get(index));
			dataRenderer.setSeriesShape(0, shapeList.get(index));
			plot.setDataset(i + 1, dataSet);
			plot.setRenderer(i + 1, dataRenderer);

			index++;
		}
	}

	private boolean isValid(Double value) {
		return value != null && !value.isInfinite() && !value.isNaN();
	}

	private class DataAndModelChartSaveAsItem extends JMenuItem implements
			ActionListener {

		private static final long serialVersionUID = 1L;

		public DataAndModelChartSaveAsItem() {
			super("Save as... (SVG)");

			addActionListener(this);
		}

		private void fireSaveAsButtonClicked(String fileName) {
			ChartCreator chartPanel = ChartCreator.this;

			ChartUtilities.saveChartAs(chartPanel.getChart(), fileName,
					chartPanel.getWidth(), chartPanel.getHeight());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();

			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.addChoosableFileFilter(new StandardFileFilter(".svg",
					"SVG Vector Graphic (*.svg)"));

			if (fileChooser.showSaveDialog(ChartCreator.this) == JFileChooser.APPROVE_OPTION) {
				String fileName = fileChooser.getSelectedFile().getName();
				String path = fileChooser.getSelectedFile().getAbsolutePath();

				if (fileName.toLowerCase().endsWith(".svg")) {
					fireSaveAsButtonClicked(path);
				} else {
					fireSaveAsButtonClicked(path + ".svg");
				}
			}
		}

	}

}

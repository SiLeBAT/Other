package de.bund.bfr.flink.outbreakanalysis

import scala.annotation.meta.field
import scala.collection.JavaConverters._
import scala.io.Source

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.functions.RichGroupReduceFunction
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.core.fs.FileSystem
import org.apache.flink.core.fs.Path
import org.apache.flink.util.Collector
import org.apache.flink.api.common.operators.Order
import org.apache.flink.core.fs.FileSystem.WriteMode
import org.apache.flink.api.java.aggregation.Aggregations

/**
 */
object OutbreakAnalysis {
  private var numberOfScenarios: Integer = 50
  private var salesPath: String = "C:\\Users\\heisea\\Data\\expData\\refac\\PL2\\201043\\expData_refac_201043PL2_GS_100_0.tsv"
  private var outbreakPath: String = "C:\\Users\\heisea\\Data\\outbreak_data\\exp_c10_PL2.csv"
  private var delimiter: String = "\t"
  private var minimalMCL: Double = 0.95
  private var mclCDFOutputPath: Option[String] = Option("C:\\Users\\heisea\\Downloads\\mclCDFs.csv")
  private var mclSetSizeOutputPath: Option[String] = Option("C:\\Users\\heisea\\Downloads\\mclSetSize.csv")
  private var productScoreOutputPath: Option[String] = Option("C:\\Users\\heisea\\Downloads\\productScore.csv")
  private var productRankOutputPath: Option[String] = Option("C:\\Users\\heisea\\Downloads\\productRanks.csv")
  private var productSetOutputPath: Option[String] = Option("C:\\Users\\heisea\\Downloads\\productSet.csv")
  private val env = ExecutionEnvironment.createLocalEnvironment(2)

  def main(args: Array[String]) {

    // outbreak line consists of area code, count 
    val outbreaks: DataSet[(String, Int)] = env.readCsvFile(outbreakPath)

    // sales line consists of ean, area code, count 
    val productSales: DataSet[(String, String, Double)] = readSales()

    // build sparse sales vector for product
    // the distribution over the sale areas is normalized to one
    val products: DataSet[Product] = productSales.groupBy(0).reduceGroup { in =>
      val list = in.toList
      val totalSales: Double = list.map { _._3 }.sum
      val distribution = list.map { t => t._2 -> (t._3 / totalSales) }.toMap
      Product(list.head._1, distribution)
    }

    // now generate N scenarios for each product (N=numberOfScenarios, user-specified, usually 50)
    // use the MagicTable to draw M cases (M=numberOfOutbreaks)
    var parameters = new Configuration()
    parameters.setInteger("numberOfScenarios", numberOfScenarios)
    val numberOfOutbreaks = count(outbreaks)
    val scenarios: DataSet[Scenario] = products.flatMap(new SampleScenarios).
      withBroadcastSet(numberOfOutbreaks, "numberOfOutbreaks").
      withParameters(parameters)

    // calculate LBA-score for each product in each scenario
    val productScenarioLBAs: DataSet[Score] = scenarios.cross(products).map(new LBAScore)

    // find the index for each product within scenarios generated from it
    val ranks: DataSet[Rank] = productScenarioLBAs.groupBy("scenario.*").sortGroup("score", Order.DESCENDING).
      reduceGroup { sortedRanks =>
        val matchingScoreWithIndex = sortedRanks.zipWithIndex.find { scoreWithIndex =>
          scoreWithIndex._1.scenario.causingProduct == scoreWithIndex._1.productName
        }.get
        Rank(matchingScoreWithIndex._1.productName, matchingScoreWithIndex._2)
      }

    // calculate the MCL probabilties per rank
    val numberOfProducts = count(products)
    val mclProbabilities: DataSet[RankProbability] = ranks.groupBy("rank").reduceGroup(new TotalRankDistribution).
      withBroadcastSet(numberOfProducts, "numberOfProducts").
      withParameters(parameters)

    // sum the probabilities up to a cumulative distribution function
    val mclCDF: DataSet[List[Double]] = mclProbabilities.map { x => (x, 0) }.groupBy(1).sortGroup(0, Order.ASCENDING).
      reduceGroup { rankProbabilities =>
        val probByRanks = rankProbabilities.toList.map { _._1 }.sortBy { _.rank }
        var sum = 0d
        probByRanks.map { rd => sum += rd.probability; sum }
      }

    // now find the first rank with a cumulative distribution over the user-given threshold
    val mclSetSize = mclCDF.map { _.indexWhere { _ >= minimalMCL } + 1 }

    // create a pseudo-scenario (for code reusage) of the actual outbreak
    val actualScenario = outbreaks.map { x => (x, 0) }.groupBy(1).reduceGroup { group =>
      Scenario(ScenarioId("actual", 0), group.map { _._1 }.toMap.mapValues { _.toDouble })
    }

    // score all products in respect to the actual outbreak
    val productLBAs: DataSet[Score] = actualScenario.cross(products).map(new LBAScore)

    // rank the products
    val productRanks: DataSet[Rank] = productLBAs.groupBy("scenario.*").sortGroup("score", Order.DESCENDING).
      reduceGroup { (scores, collector) =>
        scores.zipWithIndex.foreach { scoreWithIndex =>
          collector.collect(Rank(scoreWithIndex._1.productName, scoreWithIndex._2 + 1)) 
        }
      }

    // find the MCL set of the first N products
    val productSet = productRanks.map { x => (x, 0) }.groupBy(1).sortGroup(0, Order.ASCENDING).
      reduceGroup(new FindProductSet).
      withBroadcastSet(mclSetSize, "mclSetSize")
       

    if (mclCDFOutputPath.isDefined)
      mclCDF.flatMap { x => x }.writeAsText(mclCDFOutputPath.get, WriteMode.OVERWRITE).
        setParallelism(1)

    if (mclSetSizeOutputPath.isDefined)
      mclSetSize.writeAsText(mclSetSizeOutputPath.get, WriteMode.OVERWRITE).setParallelism(1)
      
    if (productScoreOutputPath.isDefined)
      productLBAs.map { s => (s.productName, s.score) }.
        writeAsCsv(productScoreOutputPath.get, "\n", delimiter, WriteMode.OVERWRITE).setParallelism(1)

    if (productRankOutputPath.isDefined)
      productRanks.map { _.productName }.
        writeAsText(productRankOutputPath.get, WriteMode.OVERWRITE).setParallelism(1)

    if (productSetOutputPath.isDefined) {
      productSet.flatMap { x => x }.
        writeAsText(productSetOutputPath.get, WriteMode.OVERWRITE).setParallelism(1)
    }

    env.execute("Foodborne disease simulation")
  }
  
  def readSales() : DataSet[(String, String, Double)] = {
    // sale matrix; rows = area codes; columns = product names
    // WORKAROUND: Flink currently does not support variable columns in csv.
    // Manually read header from data source and configure input dataSet accordingly.
    val input = new Path(salesPath)
    val source = Source.fromInputStream(input.getFileSystem.open(input))
    val header = source.getLines().next().split(delimiter).map { _.replaceAll("^\"(.*)\"$", "$1") }
    val saleMatrix: DataSet[String] = env.readTextFile(salesPath)
    
    // sales line consists of ean, area code, count 
    saleMatrix.flatMap { (row, collector) =>
      val cells = row.split(delimiter)
      val area = cells.head.replaceAll("^\"(.*)\"$", "$1")
      if (area != "row ID")
        cells.tail.map { _.toDouble }.zipWithIndex.filter { _._1 > 0 }.foreach { countWithIndex =>
          collector.collect(header(countWithIndex._2 + 1), area, countWithIndex._1.toDouble)
        }
    }
  }
  
  def count(dataSet : DataSet[_]) : DataSet[Int] = {
    // WORKAROUND: getting count is currently clumsy in Flink; should be replaced with more compact code
    dataSet.map { o => (1, 1) }.aggregate(Aggregations.SUM, 0).map { _._1 }
  }
}

// data holders, distribution represent the sparse sale or outbreak distributions
case class Product(name: String, distribution: Map[String, Double])
case class ScenarioId(causingProduct: String, number: Int)
case class Scenario(id: ScenarioId, distribution: Map[String, Double])
case class Score(productName: String, scenario: ScenarioId, score: Double)
case class Rank(productName: String, rank: Int)
case class RankProbability(rank: Int, probability: Double)

/**
 * Draws numberOfScenarios times distributions with numberOfOutbreaks cases. 
 */
final class SampleScenarios extends RichFlatMapFunction[Product, Scenario] {
  var numberOfOutbreaks: Int = 0
  var numberOfScenarios: Int = 0

  override def open(config: Configuration): Unit = {
    numberOfOutbreaks = getRuntimeContext().getBroadcastVariable[Int]("numberOfOutbreaks").get(0)
    numberOfScenarios = config.getInteger("numberOfScenarios", 1)
  }

  def flatMap(product: Product, out: Collector[Scenario]) = {
    val saleArray = product.distribution.toArray
    def mt = new MagicTable(saleArray.map { _._2.toDouble })

    for (i <- 1 to numberOfScenarios) {
      val relCase = 1d / numberOfOutbreaks
      val areas = (1 to numberOfOutbreaks).map { i => saleArray(mt.sampleIndex())._1 }
      out.collect(Scenario(ScenarioId(product.name, i),
        areas.groupBy { x => x }.mapValues { group => group.size * relCase }))
    }
  }
}

/**
 * Calculates the log-likehood of the scenario and product
 */
final class LBAScore extends RichMapFunction[(Scenario, Product), Score] {
  def map(scenarioAndProduct: (Scenario, Product)): Score = {
    val scenario = scenarioAndProduct._1
    val product = scenarioAndProduct._2
    val salesInRegion = scenario.distribution.map { regionalOutbreaks =>
      product.distribution.getOrElse(regionalOutbreaks._1, 0d)
    }.toList
    // if there is any region with an outbreak where the product has not been sold -> prob = 0
    if (salesInRegion.contains(0))
      return Score(product.name, scenario.id, Double.NegativeInfinity)
    val logLH = scenario.distribution.map { _._2 }.view.zipWithIndex.map { amountWithIndex =>
      amountWithIndex._1 * Math.log(salesInRegion(amountWithIndex._2))
    }.sum
    return Score(product.name, scenario.id, logLH)
  }
}

/**
 * Calculates the relative number of rank occurrences.
 */
final class TotalRankDistribution extends RichGroupReduceFunction[Rank, RankProbability] {
  var numberOfProducts: Int = 0
  var numberOfScenarios: Int = 0

  override def open(config: Configuration): Unit = {
    numberOfProducts = getRuntimeContext().getBroadcastVariable[Int]("numberOfProducts").get(0)
    numberOfScenarios = config.getInteger("numberOfScenarios", 1)
  }

  def reduce(values: java.lang.Iterable[Rank], out: Collector[RankProbability]) = {
    val ranks = values.asScala.iterator.buffered
    out.collect(RankProbability(ranks.head.rank + 1, ranks.size.toDouble / numberOfProducts / numberOfScenarios))
  }
}

/**
 * Extracts the MCL item set from the produts.
 */
final class FindProductSet extends RichGroupReduceFunction[(Rank, Int), List[String]] {
  var mclSetSize: Int = 0

  override def open(config: Configuration): Unit = {
    mclSetSize = getRuntimeContext().getBroadcastVariable[Int]("mclSetSize").get(0)
  }

  def reduce(ranks: java.lang.Iterable[(Rank, Int)], out: Collector[List[String]]) = { 
      val productByRanks = ranks.iterator.asScala.toList.map { _._1 }.sortBy { _.rank }
      out.collect(productByRanks.slice(0, mclSetSize).map { _.productName })
  }
}


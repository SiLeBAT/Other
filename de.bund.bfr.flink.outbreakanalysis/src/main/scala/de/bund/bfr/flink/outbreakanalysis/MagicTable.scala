package de.bund.bfr.flink.outbreakanalysis

import java.util.Random
import java.util.TreeMap
import scala.collection.mutable.ListBuffer

class MagicTable(probabilities: Array[Double]) {

  def mean = probabilities.length

  def probs = probabilities

  def random = new Random()

  private var magicTablePos: Array[Int] = _

  private var magicTableProbs: Array[Double] = _

  if (probabilities == null) throw new IllegalArgumentException("No input probabilities given")

  this.createTable()

  private def createTable() {
    // linearized magic table
    this.magicTableProbs = Array.ofDim[Double](2 * this.mean)
    this.magicTablePos = Array.ofDim[Int](2 * this.mean)
    
    // group probability indices
    var rest = new TreeMap[Double, ListBuffer[Int]]()
    for (index <- 0 until this.mean) {
      val prob = this.probs(index)
      var buffer = rest.get(prob)
      if (buffer == null) {
        buffer = new ListBuffer[Int]()
        rest.put(prob, buffer)
      }
      buffer += index
    }
      
    val mReciprocal = 1.0 / this.mean
    for (t <- 0 until this.mean) {
      // calculate corresponding probabilities
      val upper = rest.firstKey
      val index = rest.firstEntry().getValue.head
      val lower = mReciprocal - upper
      
      // set probabilities in magic table
      this.magicTableProbs(t) = upper
      this.magicTableProbs(t + this.mean) = lower
      
      // set indices in magic table
      this.magicTablePos(t) = index
      val maxVal = rest.lastKey
      val maxIndex = rest.lastEntry().getValue.remove(0)
      this.magicTablePos(t + this.mean) = maxIndex
      
      // update rest
      val newVal = maxVal - lower
      var valueList = rest.get(newVal)
      if (valueList == null) {     
        valueList = new ListBuffer[Int]()
        rest.put(newVal, valueList)
      }      
      valueList += maxIndex
      
      // clean up empty buckets
      if (rest.get(upper).isEmpty) 
        rest.remove(upper)
        
      if (rest.get(maxVal).isEmpty) 
        rest.remove(maxVal)
    }
  }

  def sampleIndex(): Int = {
    var randomIndex = this.random.nextInt(this.mean)
    
    // choose cell (top or bottom)
    if (this.random.nextDouble() > this.mean * this.magicTableProbs(randomIndex)) 
      randomIndex += this.mean
      
    this.magicTablePos(randomIndex)
  }
}

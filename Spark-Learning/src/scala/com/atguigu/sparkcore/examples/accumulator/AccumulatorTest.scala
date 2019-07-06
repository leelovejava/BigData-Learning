package com.atguigu.sparkcore.examples.accumulator

import org.apache.spark.util.LongAccumulator
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 累加器
  */
object AccumulatorTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("accumulator")
    val sc = new SparkContext(conf)
    val accumulator: LongAccumulator = sc.longAccumulator("My Accumulator")
    val lines = sc.textFile("./data/words")
    val trans = lines.map(line=>{
      accumulator.add(1)
      line
    })
    trans.collect()

    println(s"accumulator value is ${accumulator.value}")



  }
}

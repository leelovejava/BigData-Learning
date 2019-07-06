package com.atguigu.sparkcore.examples.accumulator

import org.apache.spark.util.{DoubleAccumulator, LongAccumulator}
import org.apache.spark.{SparkConf, SparkContext}

object Spark_Accumulator {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("test")
    val sc = new SparkContext(conf)
//    val acc: DoubleAccumulator = sc.doubleAccumulator("xx")
    val acc: LongAccumulator = sc.longAccumulator("xx")

    val lines = sc.textFile("./data/words",2)
//    var i = 0
    val result = lines.map(line=>{
      acc.add(1)
//      i += 1
//      println(s"Executor  i = "+i)
      line
    })
    result.count()
    println(s"Driver  acc = "+acc.value)

  }
}

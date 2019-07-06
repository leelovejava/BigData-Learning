package com.atguigu.sparkcore.examples.broadcast

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.{SparkConf, SparkContext}

object Spark_broadcast {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("broadCast")

    val sc = new SparkContext(conf)
    val blackList = List[String]("hello spark3", "hello spark4")

    val bc: Broadcast[List[String]] = sc.broadcast(blackList)
    val lines = sc.textFile("./data/words")


    lines.filter(line => {
      blackList.contains(line)
      //       val value: List[String] = bc.value
      //      value.contains(line)
    }).foreach(println)
  }
}

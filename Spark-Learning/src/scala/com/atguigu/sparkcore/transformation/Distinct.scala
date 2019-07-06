package com.atguigu.sparkcore.transformation

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 去重
  * distinct(map+reduceByKey+map)
  */
object SparkTransformation_union {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("test")
    val sc = new SparkContext(conf)
    val rdd1 = sc.parallelize(List[String]("a", "b", "c", "d", "a", "a", "c"))
    rdd1.distinct().foreach(println)
  }
}

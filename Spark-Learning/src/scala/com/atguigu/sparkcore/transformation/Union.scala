package com.atguigu.sparkcore.transformation

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 合并两个数据集。两个数据集的类型要一致。
  * 返回新的RDD的分区数是合并RDD分区数的总和。
  */
object Union {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("test")
    val sc = new SparkContext(conf)
    val rdd1 = sc.parallelize(List[String]("a", "b", "c"), 2)
    val rdd2 = sc.parallelize(List[String]("d", "e", "f"), 3)
    println(s"rdd1 partitions length = ${rdd1.getNumPartitions}")
    println(s"rdd2 partitions length = ${rdd2.getNumPartitions}")

    val unit: RDD[String] = rdd1.union(rdd2)
    println(s"unit partitions length = ${unit.getNumPartitions}")
    unit.foreach(println)
  }
}

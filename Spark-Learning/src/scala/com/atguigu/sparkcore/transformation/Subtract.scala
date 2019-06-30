package com.atguigu.sparkcore.transformation

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * subtract
  * 取两个数据集的差集，结果RDD的分区数与subtract前面的RDD的分区数一致。
  */
object Subtract {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("test")
    val sc = new SparkContext(conf)
    val rdd1 = sc.parallelize(List[String]("a", "b", "c"))
    val rdd2 = sc.parallelize(List[String]("a", "b", "f"))
    val unit: RDD[String] = rdd1.subtract(rdd2)
    unit.foreach(println)
  }
}

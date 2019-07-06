package com.atguigu.sparkcore.transformation

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * intersection
  * 取两个数据集的交集，返回新的RDD与父RDD分区多的一致
  */
object Intersection {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("test")
    val sc = new SparkContext(conf)
    val rdd1 = sc.parallelize(List[String]("a", "b", "c"))
    val rdd2 = sc.parallelize(List[String]("d", "e", "f"))
    val unit: RDD[String] = rdd1.intersection(rdd2)
    unit.foreach(println)
  }
}

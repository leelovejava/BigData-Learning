package com.atguigu.sparkcore.examples.sort

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

case class SecondSortKey(val first: Int, val second: Int) extends Ordered[SecondSortKey] {
  def compare(that: SecondSortKey): Int = {
    if (this.first - that.first == 0)
      this.second - that.second
    else
      this.first - that.first
  }
}

/**
  * 二次排序问题
  * Spark中大于两列的排序都是二次排序
  * 采用封装对象的思想,对数据进行排序
  */
object SecondSort {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setAppName("secondarySort")
    conf.setMaster("local")
    val sc = new SparkContext(conf)
    val lines = sc.textFile("./data/secondSort.txt")
    val transRDD: RDD[(SecondSortKey, String)] =
      lines.map(s => {
        (SecondSortKey(s.split(" ")(0).toInt, s.split(" ")(1).toInt), s)
      })
    transRDD.sortByKey(false).map(_._2).foreach(println)

  }
}

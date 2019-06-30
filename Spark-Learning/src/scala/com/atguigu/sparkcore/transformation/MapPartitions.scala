package com.atguigu.sparkcore.transformation

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
  * mapPartitions
  * 与map类似，遍历的单位是每个partition上的数据
  */
object MapPartitions {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("test")
    val sc = new SparkContext(conf)

    val unit: RDD[String] = sc.parallelize(Array[String]("a", "b", "c", "d", "e", "f"), 3)


    val unit1: RDD[String] = unit.mapPartitions(iter => {
      val list = new ListBuffer[String]()
      println("建立数据库连接。。。")
      while (iter.hasNext) {
        val next = iter.next()
        list.append(next)
        println("插入数据库连接。。。" + next)
      }
      println("关闭数据库连接。。。")

      list.iterator
    })
    unit1.count()
  }
}

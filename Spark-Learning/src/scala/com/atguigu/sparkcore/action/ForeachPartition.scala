package com.atguigu.sparkcore.action

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * foreachPartition
  * 遍历的数据是每个partition的数据
  */
object SparkAction_foreachPartition {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("test")
    val sc = new SparkContext(conf)

    val unit: RDD[String] = sc.parallelize(Array[String]("a", "b", "c", "d", "e", "f"), 3)


    unit.foreachPartition(iter => {
      println("建立数据库连接。。。")
      while (iter.hasNext) {
        val next = iter.next()
        println("插入数据库连接。。。" + next)
      }
      println("关闭数据库连接。。。")

    })
  }
}

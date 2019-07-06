package com.atguigu.sparkcore.transformation

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * cogroup
  * 当调用类型（K,V）和（K，W）的数据上时，返回一个数据集（K，（Iterable<V>,Iterable<W>）），子RDD的分区与父RDD多的一致
  */
object Cogroup {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("test")
    val sc = new SparkContext(conf)
    val nameRDD: RDD[(String, Int)] = sc.parallelize(Array[(String, Int)](
      ("zhangsan", 18), ("lisi", 19), ("wangwu", 20), ("maliu", 21), ("zhangsan", 180), ("maliu", 210)
    ))
    val scoreRDD: RDD[(String, Int)] = sc.parallelize(Array[(String, Int)](
      ("zhangsan", 100), ("lisi", 200), ("wangwu", 300), ("tianqi", 400),
      ("zhangsan", 1000), ("wangwu", 3000)
    ))


    val unit: RDD[(String, (Iterable[Int], Iterable[Int]))] = nameRDD.cogroup(scoreRDD)
    unit.foreach(println)
  }
}

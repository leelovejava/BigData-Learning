package com.atguigu.sparkcore.transformation

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Join {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("test")
    val sc = new SparkContext(conf)

    val nameRDD: RDD[(String, Int)] = sc.parallelize(Array[(String, Int)](("zhangsan", 18), ("lisi", 19), ("wangwu", 20), ("maliu", 21)), 3)
    val scoreRDD: RDD[(String, Int)] = sc.parallelize(Array[(String, Int)](("zhangsan", 100), ("lisi", 200), ("wangwu", 300), ("tianqi", 400)), 4)

    println(s"nameRDD partitions length = ${nameRDD.getNumPartitions}")
    println(s"scoreRDD partitions length = ${scoreRDD.getNumPartitions}")


    val unit: RDD[(String, (Int, Int))] = nameRDD.join(scoreRDD)

    println(s"unit partitions length = ${unit.getNumPartitions}")
    unit.foreach(println)


    //    unit.foreach(println)

    //    val unit: RDD[(String, (Option[Int], Option[Int]))] = nameRDD.fullOuterJoin(scoreRDD)
    //    unit.foreach(println)

    //    val unit: RDD[(String, (Option[Int], Int))] = nameRDD.rightOuterJoin(scoreRDD)
    //    unit.foreach(println)

    //    val unit: RDD[(String, (Int, Option[Int]))] = nameRDD.leftOuterJoin(scoreRDD)

  }

}

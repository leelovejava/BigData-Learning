package com.atguigu.sparkcore.wc

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object SparkScalaWordCount {
  def main(args: Array[String]): Unit = {
    val filePath = "D:/workspace2/BigData-Learning/Spark-Learning/src/resources/data/words"
    sortWordCount(filePath)

  }

  def tupleWordCount(filePath: String): Unit = {


    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("wordcount")
    val sc = new SparkContext(conf)

    sc.textFile(filePath).flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).foreach(println)

    val lines: RDD[String] = sc.textFile(filePath)
    val words: RDD[String] = lines.flatMap(line => {
      line.split(" ")
    })
    // tuple计数
    // (hello,1)
    val pairWords: RDD[(String, Int)] = words.map(word => {
      new Tuple2(word, 1)
    })
    //reduceByKey 1.先对相同的key进行分组 2.然后对同一个组内的value做操作
    val result: RDD[(String, Int)] = pairWords.reduceByKey((v1: Int, v2: Int) => {
      v1 + v2
    })
    result.foreach(tp => {
      println(tp)
    })
  }

  /**
    * 简化版workCount
    *
    * @param filePath
    */
  def sortWordCount(filePath: String): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("wordcount")
    val sc = new SparkContext(conf)

    val lines = sc.textFile(filePath)

    lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).
      sortBy(tp => {
        tp._2
      }, false).
      foreach(println)

    // sortByKey
    // tuple swap 互换位置
    lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).
      map(tp => {
        tp.swap
      }).sortByKey(false).map(_.swap).foreach(println)
    // val lines: RDD[String] 中: RDD[String]可以省略,自动类型推断

    // tuple可new可不new,不new可省略, map(word => (word, 1))
    // word只使用一次,可以用_代替 map((_,1))

    // _+_ 等于 (v1: Int, v2: Int) => { v1 + v2 } 只使用一次,用_代替
    // 如果一个参数在匿名的方法体中只使用了一次,可以用_代替


    // sortByKey 按照key的asci排序 ascending=true 升序
    // sortByKey(false)
    // 实现思路: 让key和value互换位置 (w,c)->(c,w).排序完，再互换位置 (w,c)

    // result.sortBy(tp=>{tp._2},false).foreach(tp=>{println(tp)})
    // sortBy(tp => {tp._2},false)

  }
}

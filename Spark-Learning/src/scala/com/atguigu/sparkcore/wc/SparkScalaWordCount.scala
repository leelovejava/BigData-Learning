package com.mujie.youxiaosparkscala

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object SparkScalaWordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("wordcount")
    val sc = new SparkContext(conf)
    val lines: RDD[String] = sc.textFile("./data/words")
    val words: RDD[String] = lines.flatMap(line=>{line.split(" ")})
    val pairWords: RDD[(String, Int)] = words.map(word=>{new Tuple2(word,1)})
    //reduceByKey 1.先对相同的key进行分组 2.然后对同一个组内的value做操作
    val result: RDD[(String, Int)] = pairWords.reduceByKey((v1:Int, v2:Int)=>{v1+v2})

    val unit: RDD[(Int, String)] = result.map(tp=>{tp.swap})
    val unit1: RDD[(Int, String)] = unit.sortByKey(false)
    unit1.map(_.swap).foreach(println)



//    result.sortBy(tp=>{tp._2},false).foreach(tp=>{println(tp)})



//    val conf = new SparkConf()
//    conf.setMaster("local")
//    conf.setAppName("wordcount")
//    val sc = new SparkContext(conf)
//    sc.textFile("./data/words").flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).foreach(println)


//    val conf = new SparkConf()
//    conf.setMaster("local")
//    conf.setAppName("wordcount")
//    val sc = new SparkContext(conf)
//    val lines: RDD[String] = sc.textFile("./data/words")
//    val words: RDD[String] = lines.flatMap(line=>{line.split(" ")})
//    val pairWords: RDD[(String, Int)] = words.map(word=>{new Tuple2(word,1)})
//    //reduceByKey 1.先对相同的key进行分组 2.然后对同一个组内的value做操作
//    val result: RDD[(String, Int)] = pairWords.reduceByKey((v1:Int, v2:Int)=>{v1+v2})
//    result.foreach(tp=>{println(tp)})
  }
}

package com.atguigu.sparkcore.examples

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 统计wordcount
  */
object WordCount {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
    conf.setMaster("local").setAppName("wordcount")
    val sc = new SparkContext(conf)
    val lines = sc.textFile("./data/words",2)

    val rdd1 = sc.parallelize(Array[String]("a","b","c","d","e"),2);
    println(s"lines rdd partition = ${lines.getNumPartitions}")
    println(s"rdd1 rdd partition = ${rdd1.getNumPartitions}")

    val lines2: RDD[String] = lines.map(one=>{one+"#"})

    val rdd2 = rdd1.filter(one=>{
      true
    })

    val result = lines2.union(rdd2)
    val unit: RDD[String] = result.coalesce(1)
    println(s"unit rdd partition = ${unit.getNumPartitions}")

    unit.count()

    while(true){

    }



//    val conf = new SparkConf()
//    conf.setMaster("local").setAppName("wordcount")
//    val sc = new SparkContext(conf)
//    val lines = sc.textFile("./data/words")
//    val words = lines.flatMap(line=>{
//      line.split(" ")
//    })
//    val pairWords = words.map(word=>{
//      new Tuple2(word,1)
//    })
//    val reduceResult = pairWords.reduceByKey((v1,v2)=>{v1+v2})
//    val result = reduceResult.sortBy(tp=>{tp._2},false)
//    result.foreach(println)
//    sc.stop()
  }
}

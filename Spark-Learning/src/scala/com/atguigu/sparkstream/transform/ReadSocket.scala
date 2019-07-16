package com.atguigu.sparkstream.transform

import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Durations, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object ReadSocket {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local[2]")
    conf.setAppName("test")
//    val sc = new SparkContext(conf)
//    sc.setLogLevel("Error")
    val ssc: StreamingContext = new StreamingContext(conf,Durations.seconds(5))
    val context: SparkContext = ssc.sparkContext
    context.setLogLevel("Error")

    val lines: ReceiverInputDStream[String] = ssc.socketTextStream("mynode5",9999)
    val words: DStream[String] = lines.flatMap(line => {
      line.split(" ")
    })
    val pairWords: DStream[(String, Int)] = words.map(word=>{new Tuple2(word,1)})
    val result : DStream[(String, Int)]  = pairWords.reduceByKey((v1:Int,v2:Int)=>{v1+v2})

    /**
      * foreachRDD
      *   1.forachRDD可以获取DStream中的RDD,对RDD使用RDD的transformation类算子执行，一定要对获取的RDD使用action算子执行。
      *   2.foreachRDD算子内，获取的RDD的算子外的代码是在Driver端执行，可以利用这个特点做到动态的改变广播变量。
      */
    result.foreachRDD(rdd=>{
      rdd.foreach(println)
//      println("**********************")
//      val unit: RDD[(String, Int)] = rdd.filter(one => {
//        !"zhangsan".equals(one._1)
//      })
//      unit.count()
    })
//    result.print()

    ssc.start()
    ssc.awaitTermination()
//    ssc.stop()

  }
}

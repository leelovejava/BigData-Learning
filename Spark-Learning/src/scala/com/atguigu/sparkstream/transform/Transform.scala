package com.atguigu.sparkstream.transform

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Durations, StreamingContext}

/**
  * @date 2019-07-16
  */
object Transform {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local[2]")
    conf.setAppName("test")
    val ssc = new StreamingContext(conf,Durations.seconds(5))
    ssc.sparkContext.setLogLevel("Error")

    val lines = ssc.socketTextStream("mynode5",9999)

    /**
      * transform
      *   1.transform 可以获取DStream中的RDD对RDD进行使用RDD算子操作。不必须对RDD使用Action算子操作
      *   2.transform算子内，获取的RDD的算子外的代码是在Driver端执行的，利用这个特点可以做到动态的改变广播变量。
      */
    val unit: DStream[(String, Int)] = lines.transform(rdd => {
      println("========================")
      val transfer: RDD[String] = rdd.filter(line => {
        !line.contains("zhangsan")
      })
      val value: RDD[(String, Int)] = transfer.map(one=>{(one,1)})
      value
    })
    unit.print()

    ssc.start()
    ssc.awaitTermination()
  }
}

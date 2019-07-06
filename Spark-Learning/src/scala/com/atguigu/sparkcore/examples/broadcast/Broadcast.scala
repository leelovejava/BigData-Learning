package com.atguigu.sparkcore.examples.broadcast

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.{SparkConf, SparkContext}

/** *
  * 广播变量 broadcast
  * 注意事项
  * 能不能将一个RDD使用广播变量广播出去？
  * 不能，因为RDD是不存储数据的。可以将RDD的结果广播出去。
  * 广播变量只能在Driver端定义，不能在Executor端定义。
  * 在Driver端可以修改广播变量的值，在Executor端无法修改广播变量的值
  *
  *
  * 和SparkConf在一起的，是在Driver端执行
  * 和算子相关的代码是在Executor端执行
  */
object Broadcast {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("broadCast")

    val sc = new SparkContext(conf)
    val blackList = List[String]("hello spark3", "hello spark4")

    val bc: Broadcast[List[String]] = sc.broadcast(blackList)
    val lines = sc.textFile("./data/words")

    // 过滤,判断是否包含line
    lines.filter(line => {
      // 不使用广播变量
      //blackList.contains(line)

      // 使用广播变量
      val value: List[String] = bc.value
      value.contains(line)
    }).foreach(println)
  }
}

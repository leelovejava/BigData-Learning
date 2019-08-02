package com.dsj.spark.structedStreaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger

object kafka_StructuredStreaming_mysql {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("applogs")
      .master("local[2]")
      .getOrCreate()

    import spark.implicits._
    //读取数据
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "cdh01:9092,cdh02:9092,cdh03:9092")
      .option("subscribe", "applogs")
      .load()
    val ds = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]

    val filter = ds.map(x => x._2).map(_.split(",")).filter(_.length == 4)

    // 统计app所有页面浏览量
    val appCounts = filter.map(x => x(2)).groupBy("value").count().toDF("appname", "count")

    // 数据输出
    val writer = new JDBCSink(Constants.url, Constants.userName, Constants.passWord)
    val query = appCounts.writeStream
      .foreach(writer)
      .outputMode("update")
      .trigger(Trigger.ProcessingTime("2 seconds"))
      .start()
    // 统计app uv
    //val userCounts = filter.map(x =>x(0)).groupBy("value").count().toDF("logtime","count")
    val userCounts = filter.map(x => x(0)).distinct().toDF("uid")
    val writer2 = new JDBCSink2(Constants.url, Constants.userName, Constants.passWord)
    val query2 = userCounts.writeStream
      .foreach(writer2)
      .outputMode("update")
      .trigger(Trigger.ProcessingTime("2 seconds"))
      .start()
    query.awaitTermination()
    query2.awaitTermination()
  }
}
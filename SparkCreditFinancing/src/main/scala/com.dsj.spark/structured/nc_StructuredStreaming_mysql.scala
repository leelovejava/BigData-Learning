package com.dsj.spark.structedStreaming

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger

object nc_StructuredStreaming_mysql {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("applogs")
      .master("local[2]")
      .getOrCreate()

    import spark.implicits._
    // 读取数据
    val lines = spark.readStream
      .format("socket")
      .option("host", "cdh01")
      .option("port", 9999)
      .load()

    val filter = lines.as[String].map(_.split(",")).filter(_.length == 4)

    // 统计app所有页面浏览量
    val appCounts = filter.map(x => x(2)).groupBy("value").count().toDF("appname", "count")

    // 数据输出
    val writer = new JDBCSink(Constants.url, Constants.userName, Constants.passWord)
    val query = appCounts.writeStream
      .foreach(writer)
      .outputMode("update")
      .trigger(Trigger.ProcessingTime("2 seconds"))
      .start()

    query.awaitTermination()
  }
}
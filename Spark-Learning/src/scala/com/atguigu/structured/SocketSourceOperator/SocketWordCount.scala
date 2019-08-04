package com.atguigu.structured.SocketSourceOperator

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.OutputMode

object SocketWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName(this.getClass.getName).setMaster("yarn-client")
      .set("yarn.resourcemanager.hostname", "mt-mdh.local")
      .set("spark.executor.instances", "2")
      .set("spark.default.parallelism", "4")
      .set("spark.sql.shuffle.partitions", "4")
      .setJars(List("/opt/sparkjar/bigdata.jar"
        , "/opt/jars/spark-streaming-kafka-0-10_2.11-2.3.1.jar"
        , "/opt/jars/kafka-clients-0.10.2.2.jar"
        , "/opt/jars/kafka_2.11-0.10.2.2.jar"))
    val spark = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()
    val lines = spark.readStream.
      format("socket")
      .option("host", "localhost")
      .option("port", 9998)
      .load()

    import spark.implicits._
    val words = lines.as[String].flatMap(_.split(" "))

    // 内部加了一个timestamp字段
    val wordCounts = words.groupBy("value").count()

    val query = wordCounts
      .writeStream
      .outputMode(OutputMode.Update())
      .format("console")
      .start()

    query.awaitTermination()
  }

}

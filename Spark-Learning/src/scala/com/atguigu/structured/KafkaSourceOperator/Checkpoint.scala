package com.atguigu.structured.KafkaSourceOperator

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession


object Checkpoint {
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

    import spark.implicits._

    // Create DataSet representing the stream of input lines from kafka
    val lines = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "mt-mdh.local:9093")
      .option("subscribe", "Stream_Static_Join")
      .load()
      .selectExpr("CAST(value AS STRING)")
      .as[String]

    // Generate running word count
    val wordCounts = lines
      .flatMap(_.split(" "))
      .groupBy("value")
      .count()

    // Start running the query that prints the running counts to the console
    val query = wordCounts
      .writeStream
      .outputMode("complete")
      .option("checkpointLocation", "/checkpoint/structured/")
      .format("console")
      .start()

    query.awaitTermination()
  }

}

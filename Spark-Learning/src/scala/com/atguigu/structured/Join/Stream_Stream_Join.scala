package com.atguigu.structured.Join

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.get_json_object
import org.apache.spark.sql.streaming.{OutputMode, Trigger}

/**
  * Spark 2.3.0支持流 dataset之间的join
  */
object Stream_Stream_Join {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[*]")
      .set("yarn.resourcemanager.hostname", "mt-mdh.local")
      .set("spark.executor.instances", "2")
      .set("spark.default.parallelism", "4")
      .set("spark.sql.shuffle.partitions", "4")
      .setJars(List("/opt/sparkjar/bigdata.jar"
        , "/opt/jars/spark-streaming-kafka-0-10_2.11-2.3.1.jar"
        , "/opt/jars/kafka-clients-0.10.2.2.jar"
        , "/opt/jars/kafka_2.11-0.10.2.2.jar"
        , "/opt/jars/spark-sql-kafka-0-10_2.11-2.0.2.jar"))

    val spark = SparkSession
      .builder
      .appName("StructuredKafkaWordCount")
      .config(sparkConf)
      .getOrCreate()
    import spark.implicits._

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "mt-mdh.local:9093")
      .option("subscribe", "jsontest")
      .load()
    val words = df.selectExpr("CAST(value AS STRING)")

    val fruit = words.select(
      get_json_object($"value", "$.time").alias("timestamp").cast("long")
      , get_json_object($"value", "$.fruit").alias("fruit"))

    val fruitCast = fruit
      .select(fruit("timestamp")
        .cast("timestamp"), fruit("fruit"))
      .withWatermark("timestamp", "10 minutes")


    fruitCast.join(fruitCast, "fruit")
      .writeStream
      .outputMode(OutputMode.Append())
      .format("console")
      .trigger(Trigger.ProcessingTime(5000))
      .option("truncate", "false")
      .start()
      .awaitTermination()
  }
}

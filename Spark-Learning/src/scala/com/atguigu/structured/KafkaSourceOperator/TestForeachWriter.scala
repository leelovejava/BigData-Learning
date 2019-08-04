package com.atguigu.structured.KafkaSourceOperator

import org.apache.spark.SparkConf
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.{ForeachWriter, SparkSession}


class TestForeachWriter extends ForeachWriter[String] {

  override def open(partitionId: Long, version: Long): Boolean = {
    println("partitionId ==========> " + partitionId)
    println("version ==============> " + version)
    true
  }

  override def process(value: String): Unit = {
    println(value)
  }

  override def close(errorOrNull: Throwable): Unit = {

  }
}

object TestForeachSink {
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

    val input = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "mt-mdh.local:9093")
      .option("subscribe", "StructuredSource1")
      .load()

    import spark.implicits._
    input.groupBy().count().map(_.toString)
      .writeStream
      .outputMode(OutputMode.Update())
      .foreach(new TestForeachWriter())
      .start()
      .awaitTermination()
  }
}
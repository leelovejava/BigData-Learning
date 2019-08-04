package com.atguigu.structured.Join

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.streaming.OutputMode

/**
  * 从spark 2.0的时候，Structured Streaming 已经开始支持流和静态 dataframe/dataset之间的inner join和一些类型的外部join
  */
object Stream_Static_Join {
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
      //      .enableHiveSupport()
      .getOrCreate()

    val details = spark.createDataFrame(Seq(
      ("000000", "male", 21),
      ("000001", "female", 21),
      ("000002", "male", 23),
      ("000003", "female", 34),
      ("000004", "male", 25),
      ("000005", "female", 27),
      ("000006", "male", 27),
      ("000007", "female", 30)
    )).toDF("gid", "sex", "age")

    details.show(1)

    import spark.implicits
    // Create DataSet representing the stream of input lines from kafka
    val kafka = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "mt-mdh.local:9093")
      .option("subscribe", "Stream_Static_Join")
      .load()
      .selectExpr("CAST(value AS STRING)")
    //      .as[String]

    kafka
      .join(details, col("value")
        .===(col("gid")), "left_outer")
      .writeStream
      .outputMode(OutputMode.Append())
      .format("console")
      .start()
      .awaitTermination()

    //    spark.stop()

  }
}

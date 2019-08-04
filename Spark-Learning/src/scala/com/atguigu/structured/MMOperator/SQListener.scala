package com.atguigu.structured.MMOperator

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{OutputMode, StreamingQueryListener}
import org.apache.spark.streaming.scheduler.StreamingListener

object SQListener {
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
    spark.streams.addListener(new StreamingQueryListener {
      override def onQueryStarted(event: StreamingQueryListener.QueryStartedEvent): Unit = {
        println("Query started ! ")
      }

      override def onQueryProgress(event: StreamingQueryListener.QueryProgressEvent): Unit = {
        println("event.progress.batchId ===========> " + event.progress.batchId)
        println("event.progress.durationMs ===========> " + event.progress.durationMs)
        println("event.progress.eventTime ===========> " + event.progress.eventTime)
        println("event.progress.id ===========> " + event.progress.id)
        println("event.progress.name ===========> " + event.progress.name)
        println("event.progress.sink.json ===========> " + event.progress.sink.json)
        println("event.progress.sources.length ===========> " + event.progress.sources.length)
        println("event.progress.sources(0).description ===========> " + event.progress.sources(0).description)
        println("event.progress.sources(0).inputRowsPerSecond ===========> " + event.progress.sources(0).inputRowsPerSecond)
        println("event.progress.sources(0).numInputRows ===========> " + event.progress.sources(0).numInputRows)
        println("event.progress.sources(0).startOffset ===========> " + event.progress.sources(0).startOffset)
        println("event.progress.sources(0).processedRowsPerSecond ===========> " + event.progress.sources(0).processedRowsPerSecond)
        println("event.progress.sources(0).endOffset ===========> " + event.progress.sources(0).endOffset)

        println("event.progress.processedRowsPerSecond ===========> " + event.progress.processedRowsPerSecond)
        println("event.progress.timestamp ===========> " + event.progress.timestamp)
        println("event.progress.stateOperators.size ===========> " + event.progress.stateOperators.size)
        println("event.progress.inputRowsPerSecond ===========> " + event.progress.inputRowsPerSecond)

      }

      override def onQueryTerminated(event: StreamingQueryListener.QueryTerminatedEvent): Unit = {
        println("Query stopped ! ")
      }

    })
    // Create DataSet representing the stream of input lines from kafka
    val lines = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "mt-mdh.local:9093")
      .option("subscribe", "split_test")
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
      .outputMode(OutputMode.Update())
      .format("console")
      .start()
    println(query.id)
    println(spark.streams.get(query.id).id)
    query.awaitTermination()
  }

}

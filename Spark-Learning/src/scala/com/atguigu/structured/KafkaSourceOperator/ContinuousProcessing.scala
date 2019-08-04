/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// scalastyle:off println
package com.atguigu.structured.KafkaSourceOperator

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger


object ContinuousProcessing {
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

    spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "mt-mdh.local:9093")
      .option("subscribe", "StructuredSource")
      .load()
      .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "mt-mdh.local:9093")
      .option("topic", "StructuredSink")
      .option("checkpointLocation", "/sql/checkpoint")
      .trigger(Trigger.Continuous("1 second")) // only change in query
      .start()
      .awaitTermination()
  }

}

// scalastyle:on println

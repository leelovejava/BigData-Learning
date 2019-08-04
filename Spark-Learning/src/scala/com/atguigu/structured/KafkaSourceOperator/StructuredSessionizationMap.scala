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

package com.atguigu.structured.KafkaSourceOperator

import java.sql.Timestamp

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.get_json_object
import org.apache.spark.sql.streaming._

object StructuredSessionizationMap {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName(this.getClass.getName).setMaster("local[*]")
      .set("yarn.resourcemanager.hostname", "mt-mdh.local")
      .set("spark.executor.instances","2")
      .set("spark.default.parallelism","4")
      .set("spark.sql.shuffle.partitions","4")
      .setJars(List("/opt/sparkjar/bigdata.jar"
        ,"/opt/jars/spark-streaming-kafka-0-10_2.11-2.3.1.jar"
        ,"/opt/jars/kafka-clients-0.10.2.2.jar"
        ,"/opt/jars/kafka_2.11-0.10.2.2.jar"
        ,"/opt/jars/spark-sql-kafka-0-10_2.11-2.0.2.jar"))

    val spark = SparkSession
      .builder
      .appName("StructuredKafkaWordCount")
      .config(sparkConf)
      .getOrCreate()
    import spark.implicits._

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers","mt-mdh.local:9093")
      .option("subscribe", "jsontest")
      .load()
    val words = df.selectExpr("CAST(value AS STRING)")

    val fruit = words.select(
      get_json_object($"value", "$.time").alias("timestamp").cast("long")
      , get_json_object($"value", "$.fruit").alias("fruit"))

    val events = fruit
//      .select(fruit("timestamp")
//        .cast("timestamp"), fruit("fruit"))
      .as[(Long,String )]
      .map { case (timestamp, fruitCol) =>
      Event(sessionId = fruitCol, timestamp)
    }

    // Sessionize the events. Track number of events, start and end timestamps of session, and
    // and report session updates.
    val sessionUpdates = events
      .groupByKey(event => event.sessionId)
      .mapGroupsWithState[SessionInfo, SessionUpdate](GroupStateTimeout.ProcessingTimeTimeout) {

        case (sessionId: String, events: Iterator[Event], state: GroupState[SessionInfo]) =>

          // If timed out, then remove session and send final update
          if (state.hasTimedOut) {
            val finalUpdate =
              SessionUpdate(sessionId, state.get.durationMs, state.get.numEvents, expired = true)
            state.remove()
            finalUpdate
          } else {
            // Update start and end timestamps in session
            val timestamps = events.
              map(_.timestamp)
              .toSeq
            val updatedSession = if (state.exists) {
              val oldSession = state.get
              SessionInfo(
                oldSession.numEvents + timestamps.size,
                oldSession.startTimestampMs,
                math.max(oldSession.endTimestampMs, timestamps.max))
            } else {
              SessionInfo(timestamps.size, timestamps.min, timestamps.max)
            }
            state.update(updatedSession)

            // Set timeout such that the session will be expired if no data received for 10 seconds
            state.setTimeoutDuration("10 seconds")
            SessionUpdate(sessionId, state.get.durationMs, state.get.numEvents, expired = false)
          }
      }

    // Start running the query that prints the session updates to the console
    val query = sessionUpdates
      .writeStream
      .outputMode(OutputMode.Update())
      .format("console")
      .start()
    query.awaitTermination()
  }
}
///** User-defined data type representing the input events */
//case class Event(sessionId: String, timestamp: Long)
//
///**
// * User-defined data type for storing a session information as state in mapGroupsWithState.
// *
// * @param numEvents        total number of events received in the session
// * @param startTimestampMs timestamp of first event received in the session when it started
// * @param endTimestampMs   timestamp of last event received in the session before it expired
// */
//case class SessionInfo(
//    numEvents: Int,
//    startTimestampMs: Long,
//    endTimestampMs: Long) {
//
//  /** Duration of the session, between the first and last events */
//  def durationMs: Long = endTimestampMs - startTimestampMs
//}

/**
 * User-defined data type representing the update information returned by mapGroupsWithState.
 *
 * @param id          Id of the session
 * @param durationMs  Duration the session was active, that is, from first event to its expiry
 * @param numEvents   Number of events received by the session while it was active
 * @param expired     Is the session active or expired
 */
//case class SessionUpdate(
//    id: String,
//    durationMs: Long,
//    numEvents: Int,
//    expired: Boolean)

// scalastyle:on println


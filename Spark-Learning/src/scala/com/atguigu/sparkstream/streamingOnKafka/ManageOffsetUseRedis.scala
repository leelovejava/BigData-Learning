package com.atguigu.sparkstream.streamingOnKafka

import java.util

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, HasOffsetRanges, KafkaUtils, LocationStrategies, OffsetRange}
import org.apache.spark.streaming.{Durations, StreamingContext}
import org.apache.spark.{SparkConf, TaskContext}

import scala.collection.mutable

/**
  * 利用redis 来维护消费者偏移量
  */
object ManageOffsetUseRedis {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("manageOffsetUseRedis")
    // 设置每个分区每秒读取多少条数据,默认全部 配置在kafka中每次拉取的数据量,这里配置的10并不是每次在kafka拉取10条数据，而是: 10 * 分区数量 * 采样时间
    conf.set("spark.streaming.kafka.maxRatePerPartition", "10")
    // 优雅的停止
    conf.set("spark.streaming.kafka.stopGracefullyOnShutdown", "true")

    // Durations.seconds(5) 采样时间,每隔五秒采集一次数据
    val ssc = new StreamingContext(conf, Durations.seconds(5))
    // 设置日志级别
    ssc.sparkContext.setLogLevel("Error")

    val topic = "m1"
    /**
      * 从Redis 中获取消费者offsetKafkaUtils
      */
    val dbIndex = 3 // redis中的库
    val currentTopicOffset: mutable.Map[String, String] = getOffSetFromRedis(dbIndex, topic)
    // 初始读取到的topic offset:
    currentTopicOffset.foreach(tp => {
      println(s" 初始读取到的offset: $tp")
    })

    // 转换成需要的类型
    val fromOffsets: Predef.Map[TopicPartition, Long] = currentTopicOffset.map { resultSet =>
      new TopicPartition(topic, resultSet._1.toInt) -> resultSet._2.toLong
    }.toMap

    /**
      * kafka参数列表
      */
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "node01:9092,node02:9092,node03:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "MyGroupId02",
      "auto.offset.reset" -> "latest", // 从头开始消费
      "enable.auto.commit" -> (false: java.lang.Boolean) // 是否自动递交偏移量, 默认是true
    )

    /**
      * 将获取到的消费者offset 传递给SparkStreaming
      */
    // 连接到kafka数据源
    val stream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      // 消费策略
      PreferConsistent,
      ConsumerStrategies.Assign[String, String](fromOffsets.keys.toList, kafkaParams, fromOffsets)
    )
    // 避免重复消费
    /*val stream = if(fromOffsets.size==0){
      KafkaUtils.createDirectStream(
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](Array(topic), kafkaParams)
      )
    }else{
      KafkaUtils.createDirectStream(
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Assign[String,String](fromOffsets.keys,kafkaParams,fromOffsets)
      )
    }*/

    /**
      * 用来更新偏移量，OffsetRange中可以获取分区及偏移量
      */
    stream.foreachRDD { (rdd: RDD[ConsumerRecord[String, String]]) =>

      println("**** 业务处理完成  ****")
      //  获取分区信息的集合
      val offsetRanges: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

      // 遍历kafka的分区
      rdd.foreachPartition { iter =>
        val o: OffsetRange = offsetRanges(TaskContext.get.partitionId)
        // fromOffset:开始读的偏移量 untilOffset:读到的偏移量
        println(s"topic:${o.topic}  partition:${o.partition}  fromOffset:${o.fromOffset}  untilOffset: ${o.untilOffset}")
      }

      // 将当前批次最后的所有分区offsets 保存到 Redis中
      saveOffsetToRedis(dbIndex, offsetRanges)

    }

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()


  }

  /**
    * 将消费者offset 保存到 Redis中
    *
    */
  def saveOffsetToRedis(db: Int, offsetRanges: Array[OffsetRange]) = {
    val jedis = RedisClient.pool.getResource
    jedis.select(db)
    offsetRanges.foreach(one => {
      // topic 分区 offset
      jedis.hset(one.topic, one.partition.toString, one.untilOffset.toString)
    })
    println("保存成功")
    if (jedis != null) {
      jedis.close()
    }
  }


  /**
    * 从Redis中获取保存的消费者offset
    *
    * @param db
    * @param topic kafka主题
    * @return
    */
  def getOffSetFromRedis(db: Int, topic: String) = {
    // 连接redis
    val jedis = RedisClient.pool.getResource
    // 设置数据库,有12个 0
    jedis.select(db)
    // 返回哈希表key中所有key和值
    val result: util.Map[String, String] = jedis.hgetAll(topic)

    if (jedis != null) {
      // 释放资源
      /// RedisClient.pool.returnResource(jedis)
      jedis.close()
    }
    if (result.size() == 0) {
      // 分区 读取到的offset
      result.put("0", "0")
      result.put("1", "0")
      result.put("2", "0")
    }
    // 可以把Java中的Map转换为Scala中的Map
    import scala.collection.JavaConversions.mapAsScalaMap
    val offsetMap: scala.collection.mutable.Map[String, String] = result
    offsetMap
  }
}

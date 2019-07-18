package com.atguigu.sparkstream.streamingOnKafka

import java.util

import org.apache.kafka.common.TopicPartition
import redis.clients.jedis.{Jedis, JedisPool}

/**
  * 判断是否存储过偏移量
  */
object JedisOffset {

  def apply(groupId: String) = {
    var fromDBOffset = Map[TopicPartition, Long]()
    val jedis: Jedis = RedisClient.pool.getResource
    val topicPartitionOffset: util.Map[String, String] = jedis.hgetAll(groupId)
    import scala.collection.JavaConversions._
    val list: List[(String, String)] = topicPartitionOffset.toList
    for (topicPL <- list) {
      val split: Array[String] = topicPL._1.split("[-]")
      fromDBOffset += (new TopicPartition(split(0), split(1).toInt) -> topicPL._2.toLong)
    }
    fromDBOffset
  }
}
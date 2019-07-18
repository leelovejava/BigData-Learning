package com.atguigu.sparkstream.streamingOnKafka

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object RedisClient {
  val redisHost = "node04"
  val redisPort = 6379
  val redisTimeout = 30000
  /**
    * JedisPool是一个连接池，既可以保证线程安全，又可以保证了较高的效率。
    */
  lazy val pool = new JedisPool(new GenericObjectPoolConfig(), redisHost, redisPort, redisTimeout)

  /**
    * Jedis连接池
    *
    * @return
  def getConnection(): Jedis = {
    //new 一个JedisPoolConfig，用来设定参数
    val conf = new JedisPoolConfig()
    val pool = new JedisPool(conf, "node04", 6379)
    //最大连接数
    conf.setMaxTotal(20)
    //最大空闲数
    conf.setMaxIdle(20)

    val jedis = pool.getResource()
    // 密码
    //jedis.auth("test123")
    jedis
  } */
}

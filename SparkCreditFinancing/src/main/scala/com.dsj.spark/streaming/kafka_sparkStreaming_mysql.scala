package com.dsj.spark.streaming

import java.sql.{Connection, DriverManager, Statement}

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * flume+kafka+Spark Streaming+mysql集成开发
  */
object kafka_sparkStreaming_mysql {

  /**
    * app页面浏览量数据插入mysql
    */
  def myFun(records: Iterator[(String, Int)]): Unit = {
    var conn: Connection = null
    var statement: Statement = null

    try {
      val url = Constants.url
      val userName: String = Constants.userName
      val passWord: String = Constants.passWord

      //conn长连接
      conn = DriverManager.getConnection(url, userName, passWord)

      records.foreach(t => {

        val appname = t._1
        val count = t._2
        print(appname + "@" + count + "***********************************")

        val sql = "select 1 from appCounts " + " where appname = '" + appname + "'"

        val updateSql = "update appCounts set count = count+" + count + " where appname ='" + appname + "'"

        val insertSql = "insert into appCounts(appname,count) values('" + appname + "'," + count + ")"
        //实例化statement对象
        statement = conn.createStatement()

        //执行查询
        var resultSet = statement.executeQuery(sql)

        if (resultSet.next()) {
          print("*****************更新******************")
          statement.executeUpdate(updateSql)
        } else {
          print("*****************插入******************")
          statement.execute(insertSql)
        }

      })
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      if (statement != null) {
        statement.close()
      }

      if (conn != null) {
        conn.close()
      }

    }

  }

  /**
    * 用户uid数据插入mysql数据
    */
  def myFun2(records: Iterator[(String, Int)]): Unit = {
    var conn: Connection = null
    var statement: Statement = null

    try {
      val url = Constants.url
      val userName: String = Constants.userName
      val passWord: String = Constants.passWord

      //conn
      conn = DriverManager.getConnection(url, userName, passWord)
      records.foreach(t => {
        val uid = t._1

        val sql = "select 1 from userCounts " + " where uid = '" + uid + "'"

        val insertSql = "insert into userCounts(uid) values('" + uid + "')"
        //实例化statement对象
        statement = conn.createStatement()

        //执行查询
        var resultSet = statement.executeQuery(sql)

        if (resultSet.next()) {
          print("*****************已经存在******************")
        } else {
          print("*****************插入******************")
          statement.execute(insertSql)
        }

      })
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      if (statement != null) {
        statement.close()
      }

      if (conn != null) {
        conn.close()
      }

    }

  }

  def main(args: Array[String]): Unit = {
    // Create the context with a 1 second batch size
    val sparkConf = new SparkConf().setAppName("applogs").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(1))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "cdh01:9092,cdh02:9092,cdh03:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "applogs",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )

    val topics = Array("applogs")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val lines = stream.map(record => record.value)

    // 无效数据过滤
    val filter = lines.map(_.split(",")).filter(_.length == 4)


    // 统计app所有页面浏览量
    val appCounts = filter.map(x => (x(2), 1)).reduceByKey(_ + _)
    appCounts.foreachRDD(rdd => {
      print("--------------------------------------------")
      //分区并行执行
      rdd.foreachPartition(myFun)
    })
    appCounts.print()

    //统计app uv
    val userCounts = filter.map(x => (x(0), 1))
    userCounts.print()

    userCounts.foreachRDD(rdd => {
      rdd.distinct()
      rdd.foreachPartition(myFun2)
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
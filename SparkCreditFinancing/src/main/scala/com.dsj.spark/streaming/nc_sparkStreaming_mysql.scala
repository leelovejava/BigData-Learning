package com.dsj.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import java.sql.Connection
import java.sql.Statement
import java.sql.DriverManager

/**
  * nc+spark streaming+mysql集成开发
  */
object nc_sparkStreaming_mysql {

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
    if (args.length < 2) {
      System.err.println("Usage: applogs <hostname> <port>")
      System.exit(1)
    }

    // Create the context with a 1 second batch size
    val sparkConf = new SparkConf().setAppName("applogs").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(1))

    // Create a socket stream on target ip:port and count the
    val lines = ssc.socketTextStream(args(0), args(1).toInt, StorageLevel.MEMORY_AND_DISK_SER)

    //无效数据过滤
    val filter = lines.map(_.split(",")).filter(_.length == 4)

    //统计app所有页面浏览量
    val appCounts = filter.map(x => (x(2), 1)).reduceByKey(_ + _)
    appCounts.foreachRDD(rdd => {
      print("--------------------------------------------")
      //分区并行执行
      rdd.foreachPartition(myFun)
    })
    appCounts.print()

    // 统计app uv
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
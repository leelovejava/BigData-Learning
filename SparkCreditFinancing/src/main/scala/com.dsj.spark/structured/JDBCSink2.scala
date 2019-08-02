package com.dsj.spark.structedStreaming

import java.sql._
import org.apache.spark.sql.ForeachWriter
import org.apache.spark.sql.Row

class JDBCSink2(url: String, username: String, password: String) extends ForeachWriter[Row] {

  var statement: Statement = _
  var resultSet: ResultSet = _
  var connection: Connection = _

  override def open(partitionId: Long, version: Long): Boolean = {
    // open connection
    Class.forName(Constants.driver)
    //connection = DriverManager.getConnection(url, username, password)
    connection = new MySqlPool(url, username, password).getJdbcConn()
    statement = connection.createStatement()
    return true
  }

  override def process(record: Row) = {
    // write string to connection
    val uid = record.getAs[String]("uid")
    try {
      val sql = "select 1 from userCounts " + " where uid = '" + uid + "'"

      val insertSql = "insert into userCounts(uid) values('" + uid + "')"

      //执行查询
      var resultSet = statement.executeQuery(sql)

      if (resultSet.next()) {
        print("*****************已经存在******************")
      } else {
        print("*****************插入******************")
        statement.execute(insertSql)
      }
    } catch {
      case ex: SQLException => {
        println("SQLException")
      }

      case ex: Exception => {

        println("Exception")
      }

      case ex: RuntimeException => {
        println("RuntimeException")
      }

      case ex: Throwable => {
        println("Throwable")
      }

    }
  }

  override def close(errorOrNull: Throwable): Unit = {
    // close the connection
    if (statement != null) {
      statement.close()
    }

    if (connection != null) {
      connection.close()

    }
  }

}
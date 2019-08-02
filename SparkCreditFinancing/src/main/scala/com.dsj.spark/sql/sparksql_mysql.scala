package com.dsj.spark.sql
import org.apache.spark.sql._
import java.sql.Connection
import java.sql.Statement
import java.sql.DriverManager
/**
 * spark sql与mysql 集成大数据项目离线分析
 */
object sparksql_mysql {
  case class applogs(uid:String,browsetime:String,browsedata:String,behaviorno:String)
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
                        .builder()
                        .appName("applogs")
                        .master("local[2]")
                        .getOrCreate()
  
  // For implicit conversions like converting RDDs to DataFrames
  import spark.implicits._
  //读取元数据
  val fileRDD = spark.sparkContext
                     .textFile("D:\\data\\browse_history.txt")
         
  //rdd 转DataSet
  val ds = fileRDD.map(line =>line.split(",")).map(t =>applogs(t(0),t(1),t(2),t(3))).toDS()
  ds.createTempView("applogs")
  
  //统计每个app页面浏览量
  val appCounts = spark.sql("select browsedata as appname ,count(browsedata) as count from applogs group by browsedata")
  appCounts.show()
  appCounts.rdd.foreachPartition(myFun)
  
  //统计app访问 uv
  val userCounts = spark.sql("select uid  from applogs group by uid") 
  userCounts.show()
  userCounts.rdd.foreachPartition(myFun2)
}
  
  /**
   * app页面浏览量数据插入mysql
   */
  def myFun(records:Iterator[Row]): Unit = {
    var conn:Connection = null
    var statement:Statement = null
    
    try{
      val url = Constants.url
      val userName:String = Constants.userName
      val passWord:String = Constants.passWord
      
      //conn长连接
      conn = DriverManager.getConnection(url, userName, passWord)
      
      records.foreach(t => {
        
        val appname = t.getAs[String]("appname")
        val count = t.getAs[Long]("count").asInstanceOf[Int]
        print(appname+"@"+count+"***********************************")
        
       val sql = "select 1 from appCounts "+" where appname = '"+appname+"'"
        
        val updateSql = "update appCounts set count = count+"+count+" where appname ='"+appname+"'"
        
        val insertSql = "insert into appCounts(appname,count) values('"+appname+"',"+count+")"
        //实例化statement对象
        statement = conn.createStatement()
        
        //执行查询
        var resultSet = statement.executeQuery(sql)
        
        if(resultSet.next()){
          print("*****************更新******************")
          statement.executeUpdate(updateSql)
        }else{
           print("*****************插入******************")
          statement.execute(insertSql)
        }
        
      })
    }catch{    
      case e:Exception => e.printStackTrace()
    }finally{
      if(statement !=null){
        statement.close()
      }
      
      if(conn !=null){
        conn.close()
      }
      
    }
    
  }
  
  /**
   * 用户uid数据插入mysql数据
   */
  def myFun2(records:Iterator[Row]): Unit = {
    var conn:Connection = null
    var statement:Statement = null
    
    try{
      val url = Constants.url
      val userName:String = Constants.userName
      val passWord:String = Constants.passWord
      
      //conn
      conn = DriverManager.getConnection(url, userName, passWord)
      
      records.foreach(t => {
        
        val uid = t.getAs[String]("uid")
        
        val sql = "select 1 from userCounts "+" where uid = '"+uid+"'"    
        
        val insertSql = "insert into userCounts(uid) values('"+uid+"')"
        //实例化statement对象
        statement = conn.createStatement()
        
        //执行查询
        var resultSet = statement.executeQuery(sql)
        
        if(resultSet.next()){
          print("*****************已经存在******************")
        }else{
           print("*****************插入******************")
          statement.execute(insertSql)
        }
        
      })
    }catch{    
      case e:Exception => e.printStackTrace()
    }finally{
      if(statement !=null){
        statement.close()
      }
      
      if(conn !=null){
        conn.close()
      }
      
    }
    
  }
}
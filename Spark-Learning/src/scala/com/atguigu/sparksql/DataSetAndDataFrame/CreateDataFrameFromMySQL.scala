package com.atguigu.sparksql.DataSetAndDataFrame

import java.util.Properties

import org.apache.spark.sql.{DataFrame, DataFrameReader, SaveMode, SparkSession}

/**
  * 将MySQL中的表加载成DataFrame
  */
object CreateDataFrameFromMySQL {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("createdataframefrommysql")
      .config("spark.sql.shuffle.partitions",1)
      .getOrCreate()
//    spark.sparkContext.setLogLevel("Error")
    /**
      * 读取mysql表第一种方式
      */
    val properties = new Properties()
    properties.setProperty("user", "root")
    properties.setProperty("password", "123456")
    val person: DataFrame = spark.read.jdbc("jdbc:mysql://192.168.179.14:3306/spark","person",properties)
    person.show()
//    spark.read.jdbc("jdbc:mysql://192.168.179.14:3306/spark",
//      "(select " +
//                   "person.id,person.name,person.age,score.score " +
//            "from person ,score " +
//        "where  person.id = score.id) T",
//      properties).show()

    /**
      * 读取mysql表第二种方式
      */
    val map = Map[String,String](
      "url"->"jdbc:mysql://192.168.179.14:3306/spark",
      "driver"->"com.mysql.jdbc.Driver",
      "user"->"root",
      "password"->"123456",
      "dbtable"->"score"//表名
    )
    val score: DataFrame = spark.read.format("jdbc").options(map).load()
    score.show()

    /**
      * 读取mysql数据第三种方式
      */
    val reader: DataFrameReader = spark.read.format("jdbc")
      .option("url", "jdbc:mysql://192.168.179.14:3306/spark")
      .option("driver", "com.mysql.jdbc.Driver")
      .option("user", "root")
      .option("password", "123456")
      .option("dbtable", "score")
    val score2: DataFrame = reader.load()
//    score2.show()

    //将以上两张表注册临时表，关联查询
    person.createOrReplaceTempView("person")
    score2.createOrReplaceTempView("score")

    val result = spark.sql("select person.id,person.name,person.age,score.score from person ,score where  person.id = score.id")
    result.show()
    //将结果保存在Mysql表中,String 格式的数据在MySQL中默认保存成text格式，如果不想使用这个格式 ，可以自己建表创建各个列的格式再保存。
    result.write.mode(SaveMode.Append).jdbc("jdbc:mysql://192.168.179.14:3306/spark", "result", properties)



  }
}

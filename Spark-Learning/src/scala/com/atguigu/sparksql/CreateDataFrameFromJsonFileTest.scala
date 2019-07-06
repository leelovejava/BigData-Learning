package com.atguigu.sparksql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object CreateDataFrameFromJsonFileTest {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("test").master("local").getOrCreate()
    val df: DataFrame = spark.read.json("./data/json")
    df.show(100)
//    df.select($"name").show()
//    df.select("name","age").filter($"name".equalTo("zhangsan")).show()
//    df.select(df.col("name"),df.col("age")).filter("name like 'zhangsan%'").show()
//    df.filter("name like 'lisi'").show()
//    df.sort(df.col("age").desc).show()

    df.createTempView("t")
    spark.sql("select *  from t where t.age = 20").show()
    val rdd: RDD[Row] = df.rdd
    rdd.foreach(one=>{

//      val str1: Long = one.getLong(0)
      val str1 = one.getAs[Long]("age")
      val str2 = one.getString(1)
      println(s"str1 = $str1,str2=$str2")
    })

//    val spark = SparkSession.builder().appName("test").master("local").getOrCreate()
//    val df :Dataset[Row] = spark.read.json("./data/json")
//    val df = frame
//    val rdd: RDD[Row] = df.rdd
//    rdd.map(line=>{
////      val  list1 = List[String](line.getAs("name"))
////      val  list2 = List[Long](line.getAs("age"))
//      df.show()
//
//
//      val name = line.getAs("name").toString
//      var age = ""
//      if(line.getAs("age")!=null){
//        age = line.getAs("age").toString
//      }
//      println("name = "+name+",age = "+age)
//      line
//    }).collect()
////    df.show()
  }
}

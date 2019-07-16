package com.atguigu.sparksql.UDF_UDAFF

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * UDF 用户自定义函数 一对一
  */
object UDF {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("UDF").getOrCreate()
    val nameList: List[String] = List[String]("zhangsan", "lisi", "wangwu", "zhaoliu", "tianqi")
    import spark.implicits._
    val nameDF: DataFrame = nameList.toDF("name")
    nameDF.createOrReplaceTempView("students")
    nameDF.show()

    // 自定义udf(一对一),统计字段值的长度
    spark.udf.register("STRLEN", (name: String) => {
      name.length
    })
    spark.sql("select name ,STRLEN(name) as length from students order by length desc").show(100)

    //    spark.udf.register("STRLEN",(name:String,i:Int)=>{
    //      name.length+i
    //    })
    //    spark.sql("select name ,STRLEN(name,10) as length from students order by length desc").show(100)
  }
}

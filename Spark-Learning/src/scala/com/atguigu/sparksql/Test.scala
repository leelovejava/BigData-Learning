package com.atguigu.sparksql

import org.apache.spark.sql._

object Test {
  def main(args: Array[String]): Unit = {
    val session = SparkSession.builder().appName("test").master("local").getOrCreate()
    val frame: DataFrame = session.read.json("./data/json")
    frame.show(100)

//    frame.select("age","name").show(100)

    import session.implicits._
    //select * from table order by name asc
//    frame.sort($"age".asc,$"name".desc).show()

    val dataset: RelationalGroupedDataset = frame.groupBy("age")
    dataset.count().show()


    //select * from table where age > 20
//    frame.filter("age >19").show()
    import session.implicits._
//    frame.filter($"age" >19).show()
//    val unit: Dataset[Row] = frame.filter(row => {
//      row(0) != null && row(0).toString.toInt > 18
//
//    })
//    unit.show()


  }
}

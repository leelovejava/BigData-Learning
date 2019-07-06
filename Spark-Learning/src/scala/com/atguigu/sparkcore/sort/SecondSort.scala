package com.atguigu.sparkcore.sort

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 二次排序
  * @param first
  * @param second
  */
case class MySecondSort(first:Int,second:Int) extends Ordered[MySecondSort]{
  override def compare(that: MySecondSort): Int = {
    if(this.first==that.first){
      this.second-that.second
    }else{
      this.first -that.first
    }
  }
}

object Spark_SecondSort {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("secondSortTest")
    val sc = new SparkContext(conf)

    val rdd1 = sc.textFile("./data/secondSort.txt")

    val rdd2: RDD[(MySecondSort, String)] = rdd1.map(line => {
      (MySecondSort(line.split(" ")(0).toInt, line.split(" ")(1).toInt), line)
    })
    rdd2.sortByKey(false).foreach(tp=>{println(tp._2)})
  }
}

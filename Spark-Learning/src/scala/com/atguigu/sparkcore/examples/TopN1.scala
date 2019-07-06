package com.atguigu.sparkcore.examples

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
  * 分组取topN问题
  * 按照原生的集合排序
  */
object TopN1 {
  /**
    * (k,v)->(k,[v,v,v,v...])->(k,[v,v,v])
    *
    * v很多,搞成一个list,list占用的是Executor的内存,有OOM问题
    *
    * 解决OOM
    * (k,v) groupByKey
    * [k,[v,v,v...]
    * v迭代器(iterator) 遍历迭代器，定义一个定长数组(长度为3:求top3)
    * [null,null,null] 数组中的值为null
    * 如果100来比较,比较值为null,数组中第一个元素[0]赋值为10
    * [100,80,70]
    * 120,先和0号位比,120排第一,100及其元素往后移 思路:80覆盖70,70放弃,100覆盖80
    * [120,100,80]
    * 110
    * @param args
    */
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("topN")
    val sc = new SparkContext(conf)
    val infos = sc.textFile("./data/scores.txt")
    val pairInfo = infos.map(one => {
      (one.split("\t")(0), one.split("\t")(1).toInt)
    })

    /**
      * 原生排序
      */
    val groupInfo = pairInfo.groupByKey()
    val className_List: RDD[(String, List[Int])] = groupInfo.map(tp => {
      val className = tp._1
      val scoreIterable = tp._2
      val sortedList: List[Int] = scoreIterable.toList.sortWith((x1, x2) => {
        x1 > x2
      })
      (className, sortedList)
    })

    val result: Array[(String, List[Int])] = className_List.collect()
    result.foreach(one => {
      val className = one._1
      val top2Info = new ListBuffer[Int]()
      if (one._2.size > 2) {
        for (i <- 0 to 1) {
          top2Info.+=(one._2(i))
        }
        println(s"className = $className,top2=${top2Info}")
      } else {
        println(s"className = $className,top2=${one._2}")
      }

    })

  }
}

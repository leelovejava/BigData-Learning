package com.atguigu.sparkcore.examples.pvAnduv

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

/**
  * 统计网站pv和uv
  * PV是网站分析的一个术语，用以衡量网站用户访问的网页的数量。对于广告主，PV值可预期它可以带来多少广告收入。一般来说，PV与来访者的数量成正比，但是PV并不直接决定页面的真实来访者数量，如同一个来访者通过不断的刷新页面，也可以制造出非常高的PV。
  * 1、什么是PV值
  * PV（page view）即页面浏览量或点击量，是衡量一个网站或网页用户访问量。具体的说，PV值就是所有访问者在24小时（0点到24点）内看了某个网站多少个页面或某个网页多少次。PV是指页面刷新的次数，每一次页面刷新，就算做一次PV流量。
  * 度量方法就是从浏览器发出一个对网络服务器的请求（Request），网络服务器接到这个请求后，会将该请求对应的一个网页（Page）发送给浏览器，从而产生了一个PV。那么在这里只要是这个请求发送给了浏览器，无论这个页面是否完全打开（下载完成），那么都是应当计为1个PV。
  *
  * 2、什么是UV值
  * UV（unique visitor）即独立访客数，指访问某个站点或点击某个网页的不同IP地址的人数。在同一天内，UV只记录第一次进入网站的具有独立IP的访问者，在同一天内再次访问该网站则不计数。UV提供了一定时间内不同观众数量的统计指标，而没有反应出网站的全面活动。
  */
object PvAndUv {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("pvuv")
    val sc = new SparkContext(conf)
    val lines = sc.textFile("./data/pvuvdata")
    //pv
    lines.map(one => {
      (one.split("\t")(5), 1)
    }).reduceByKey((v1, v2) => {
      v1 + v2
    }).sortBy(tp => {
      tp._2
    }, false).foreach(println)
    //uv
    val distinctTrans = lines.map(one => {
      s"${one.split("\t")(0)}_${one.split("\t")(5)}"
    }).distinct()
    distinctTrans.map(one => {
      (one.split("_")(1), 1)
    }).reduceByKey(_ + _).sortBy(_._2).foreach(println)
    //计算每个网址 最活跃的网址及对应的人数
    val groupTrans = lines.map(one => {
      (one.split("\t")(5), one.split("\t")(1))
    }).groupByKey()
    groupTrans.map(tp => {
      val site = tp._1
      val localIterator = tp._2.iterator
      val localMap = mutable.Map[String, Int]()
      while (localIterator.hasNext) {
        val currentLocal = localIterator.next()
        if (localMap.contains(currentLocal)) {
          val count = localMap.get(currentLocal).get + 1
          localMap.put(currentLocal, count)
        } else {
          localMap.put(currentLocal, 1)
        }
      }

      //对Map 排序
      val newList: List[(String, Int)] = localMap.toList.sortBy(tp => {
        -tp._2
      })
      (site, newList.toBuffer)

    }).foreach(println)
  }
}

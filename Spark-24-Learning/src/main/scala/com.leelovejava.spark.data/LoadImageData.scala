package org.apache.spark

import org.apache.spark.sql.SparkSession

/**
  * Spark2.4支持图片格式数据源
  * 数据下载地址
  * http://download.tensorflow.org/example_images/flower_photos.tgz
  */
object LoadImageData {
  val spark = SparkSession
    .builder()
    .appName("Spark reads pics")
    .master("local[2]")
    .getOrCreate()

  val df = spark.read.format("image").load("/opt/pic")

  df.printSchema()
  df.select(("label"),
    ("image.origin"),
    ("image.height"),
    ("image.width"),
    ("image.nChannels"),
    ("image.mode")).show(1, false)

  spark.stop()
}

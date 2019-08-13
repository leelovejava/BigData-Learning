package com.atguigu.sparkstream.output

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.{Durations, StreamingContext}

/**
  * SparkStreaming使用FileDStream监控目录
  *
  * @author tianhao
  */
object SparkStreamingMonitorDirectoryWithFileDStream {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("test")

    /**
      * Spark中使用Kryo序列化
      *
      * Kryo序列化器介绍：
      * Spark支持使用Kryo序列化机制。Kryo序列化机制，比默认的Java序列化机制，速度要快，序列化后的数据要更小，大概是Java序列化机制的1/10。
      * 所以Kryo序列化优化以后，可以让网络传输的数据变少；在集群中耗费的内存资源大大减少。
      * 对于这三种出现序列化的地方，我们都可以通过使用Kryo序列化类库，来优化序列化和反序列化的性能。
      * Spark默认使用的是Java的序列化机制，也就是ObjectOutputStream/ObjectInputStream API来进行序列化和反序列化。
      * 但是Spark同时支持使用Kryo序列化库，Kryo序列化类库的性能比Java序列化类库的性能要高很多。
      * 官方介绍，Kryo序列化机制比Java序列化机制，性能高10倍左右。
      * Spark之所以默认没有使用Kryo作为序列化类库，是因为Kryo要求最好要注册所有需要进行序列化的自定义类型，因此对于开发者来说，这种方式比较麻烦
      */
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val ssc = new StreamingContext(conf, Durations.seconds(5))
    ssc.sparkContext.setLogLevel("ERROR")

    val value: InputDStream[(LongWritable, Text)] =
      ssc.fileStream[LongWritable, Text, TextInputFormat]("hdfs://node1:8020/spark/data/", (path: Path) => {
        path.getName.endsWith("_COPYING_")
      }, true)
    value.map(one => {
      one._2
    }).foreachRDD(rdd => {
      println("++++++++++++++++++")
      rdd.foreach(println)
    })

    ssc.start()
    ssc.awaitTermination()

  }
}

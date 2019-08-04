//package bigdata.spark.StructuredStreaming
//
//import java.sql.Timestamp
//
//import org.apache.spark.SparkConf
//import org.apache.spark.sql.SparkSession
//import org.apache.spark.sql.streaming.Trigger
//import org.apache.spark.sql.functions.window
//
//object SocketEventTimeAndWaterMark {
//
//  def main(args: Array[String]): Unit = {
//    val sparkConf = new SparkConf().setAppName(this.getClass.getName).setMaster("yarn-client")
//      .set("yarn.resourcemanager.hostname", "mt-mdh.local")
//      .set("spark.executor.instances","2")
//      .set("spark.default.parallelism","4")
//      .set("spark.sql.shuffle.partitions","4")
//      .setJars(List("/opt/sparkjar/bigdata.jar"
//        ,"/opt/jars/spark-streaming-kafka-0-10_2.11-2.3.1.jar"
//        ,"/opt/jars/kafka-clients-0.10.2.2.jar"
//        ,"/opt/jars/kafka_2.11-0.10.2.2.jar"))
//    val spark = SparkSession
//      .builder()
//      .config(sparkConf)
//      .getOrCreate()
//    val lines = spark.readStream
//      .format("socket")
//      .option("host", "127.0.0.1")
//      .option("port", 9999)
//      .option("includeTimestamp", true)
//      .load()
//    import spark.implicits._
//
//      val words = lines.as[(String, Timestamp)]
//      .flatMap(line =>line._1.split(" ")
//        .map(word => (word, line._2)))
//      .toDF("word", "timestamp")
//
//       val windowedCounts = words
//      .withWatermark("timestamp", "30 seconds")
//      .groupBy(window($"timestamp", "30 seconds", "15 seconds"), $"word")
//      .count()
//
//       val query = windowedCounts
//      .writeStream
//      .outputMode("Append")
//      .format("console")
//      .trigger(Trigger.ProcessingTime(5000))
//      .option("truncate", "false")
//      .start()
//
//       query.awaitTermination()
//
//  }
//
//}

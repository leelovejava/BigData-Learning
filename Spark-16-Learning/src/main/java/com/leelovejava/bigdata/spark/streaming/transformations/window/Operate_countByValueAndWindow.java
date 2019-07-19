package com.leelovejava.bigdata.spark.streaming.transformations.window;


import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

/**
 * countByValueAndWindow(windowLength,slideInterval, [numTasks]):
 * （K，V）格式的Dstream使用时，每个Key的Value值根据Key在滑动窗口出现的次数，返回（K，Long）格式的Dstream。
 *
 * @author root
 *
 */
public class Operate_countByValueAndWindow {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("countByValueAndWindow");
		JavaStreamingContext jsc = new JavaStreamingContext(conf,Durations.seconds(5));
		jsc.checkpoint("checkpoint");
		JavaDStream<String> textFileStream = jsc.textFileStream("data");
		/**
		 * 首先将textFileStream转换为tuple格式统计word字数
		 */
		JavaPairDStream<String, Integer> mapToPair = textFileStream.flatMap(new FlatMapFunction<String, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Iterable<String> call(String t) throws Exception {
				return Arrays.asList(t.split(" "));
			}
		}).mapToPair(new PairFunction<String, String, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Tuple2<String, Integer> call(String t) throws Exception {
				return new Tuple2<String, Integer>(t.trim(), 1);
			}
		});
		
		JavaPairDStream<Tuple2<String, Integer>, Long> countByValueAndWindow = mapToPair.countByValueAndWindow(Durations.seconds(15), Durations.seconds(5));
		countByValueAndWindow.print();
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}

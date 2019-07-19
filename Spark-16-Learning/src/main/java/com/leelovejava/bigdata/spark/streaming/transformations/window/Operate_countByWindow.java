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
 * countByWindow:返回stream流中元素的滑动窗口数。
 * 窗口长度（windowLength）：窗口的持续时间 
 * 滑动间隔（slideInterval）：执行窗口操作的间隔
 *
 * @author root
 *
 */
public class Operate_countByWindow {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("Operate_countByWindow");
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
		
		JavaDStream<Long> countByWindow = mapToPair.countByWindow(Durations.seconds(15), Durations.seconds(5));
		JavaDStream<Long> count = countByWindow.count();
		count.print();
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}

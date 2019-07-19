package com.leelovejava.bigdata.spark.streaming.transformations.window;


import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

/**
 * reduceByKeyAndWindow(func, windowLength, slideInterval, [numTasks]):
 * 在（K,V）格式的Dstream上使用时，每个K对应的V通过传入的func函数进行聚合操作，返回一个（K,V）格式的新Dstream
 * 
 * @author root
 *
 */
public class Operate_reduceByKeyAndWindow {
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
		
		JavaPairDStream<String, Integer> reduceByKeyAndWindow = 
				mapToPair.reduceByKeyAndWindow(new Function2<Integer,Integer,Integer>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Integer call(Integer v1, Integer v2) throws Exception {
				return v1+v2;
			}
			
		}, Durations.seconds(21), Durations.seconds(10));
		
		
		reduceByKeyAndWindow.print();
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}

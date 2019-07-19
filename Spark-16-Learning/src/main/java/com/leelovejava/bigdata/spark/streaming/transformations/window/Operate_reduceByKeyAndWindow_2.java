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
 * reduceByKeyAndWindow(func, invFunc, windowLength, slideInterval, [numTasks]):
 * 
 * 窗口长度（windowLength）：窗口的持续时间
 * 滑动间隔（slideInterval）：执行窗口操作的间隔
 * 
 * 这是比上一个reduceByKeyAndWindow()更有效的版本，
 * 根据上一个窗口的reduce value来增量地计算每个窗口的当前的reduce value值，
 * 这是通过处理进入滑动窗口的新数据，以及“可逆的处理”离开窗口的旧数据来完成的。
 * 一个例子是当窗口滑动时，“添加”和“减少”key的数量。
 * 然而，它仅适用于“可逆的reduce 函数”，即具有相应“可逆的reduce”功能的reduce函数（作为参数invFunc）。
 * 像在reduceByKeyAndWindow中，reduce task的数量可以通过可选参数进行配置。
 * 请注意，使用此操作必须启用 checkpointing 。
 * 
 * 以上的意思就是 传一个参数的reduceByKeyAndWindow每次计算包含多个批次，每次都会从新计算。造成效率比较低，因为存在重复计算数据的情况
 * 传二个参数的reduceByKeyAndWindow 是基于上次计算过的结果，计算每次key的结果，可以画图示意。
 * @author root
 *
 */
public class Operate_reduceByKeyAndWindow_2 {
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
		
		JavaPairDStream<String, Integer> reduceByKeyAndWindow = mapToPair.reduceByKeyAndWindow(new Function2<Integer, Integer, Integer>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			/**
			 * 这里的v1是指上一个所有的状态的key的value值（如果有出去的某一批次值，v1就是下面第二个函数返回的值），v2为本次的读取进来的值
			 */
			public Integer call(Integer v1, Integer v2) throws Exception {
				System.out.println("***********v1*************"+v1);
				System.out.println("***********v2*************"+v2);
				return v1+v2;
			}
		}, new Function2<Integer,Integer,Integer>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			/**
			 * 这里的这个第二个参数的Function2是在windowLength时间后才开始执行，v1是上面一个函数刚刚加上最近读取过来的key的value值的最新值,
			 * v2是窗口滑动后，滑动间隔中出去的那一批值
			 * 返回的值又是上面函数的v1 的输入值
			 */
			public Integer call(Integer v1, Integer v2) throws Exception {
				
				System.out.println("^^^^^^^^^^^v1^^^^^^^^^^^^^"+v1);
				System.out.println("^^^^^^^^^^^v2^^^^^^^^^^^^^"+v2);
				
//				return v1-v2-1;//每次输出结果递减1 
				return v1-v2;
			}
			
		}, Durations.seconds(20), Durations.seconds(10));
		reduceByKeyAndWindow.print();
		
		jsc.start();
		jsc.awaitTermination();
		jsc.close();
	}
}

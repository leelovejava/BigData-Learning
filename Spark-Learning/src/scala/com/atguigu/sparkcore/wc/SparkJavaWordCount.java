package com.mujie.youxiaospark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;

public class SparkJavaWordCount {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("wordcount");
        JavaSparkContext sc  = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("./data/words");
        JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());


        JavaPairRDD<String, Integer> pairWords = words.mapToPair( word -> new Tuple2<>(word, 1));

        JavaPairRDD<String, Integer> result = pairWords.reduceByKey((v1, v2) -> v1 + v2);

        JavaPairRDD<Integer, String> transRDD = result.mapToPair(tuple2 -> tuple2.swap());
        JavaPairRDD<Integer, String> sortRDD = transRDD.sortByKey(false);
        JavaPairRDD<String, Integer> end = sortRDD.mapToPair( tp -> tp.swap());

        end.foreach(tuple2 -> System.out.println(tuple2));


    }
}

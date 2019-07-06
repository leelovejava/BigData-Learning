package com.atguigu.spark.core.examples;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;

import java.util.ArrayList;
import java.util.List;

/**
 * Spark广播
 */
public class Spark_broadcast {
    public static void main(String[] args) {
        SparkConf conf  = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("broadcast");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("./data/words");

        List<String> list = new ArrayList<String>();
        list.add("hello spark3");
        list.add("hello spark4");

        Broadcast<List<String>> bc = sc.broadcast(list);

        lines.filter(new Function<String, Boolean>() {
            @Override
            public Boolean call(String v1) throws Exception {
                return bc.value().contains(v1);
            }
        }).foreach(new VoidFunction<String>() {
            @Override
            public void call(String s) throws Exception {
                System.out.println(s);
            }
        });


    }
}

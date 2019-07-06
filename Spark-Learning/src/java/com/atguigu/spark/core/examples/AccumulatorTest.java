package com.atguigu.spark.core.examples;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.util.LongAccumulator;

/**
 * 累加器
 */
public class AccumulatorTest {
    public static void main(String[] args) {
        SparkSession session = SparkSession.builder().appName("accumulator").master("local").getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(session.sparkContext());
        JavaRDD<String> lines = sc.textFile("./data/words");

        LongAccumulator accumulator = sc.sc().longAccumulator();
        JavaRDD<String> map = lines.map(new Function<String, String>() {
            @Override
            public String call(String s) throws Exception {
                accumulator.add(1);
                return s;
            }
        });
        map.collect();
        System.out.println("accumulator value = " + accumulator.value());


    }
}

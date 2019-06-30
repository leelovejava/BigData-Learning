package com.atguigu.spark.core.transformatioins;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import java.util.Arrays;

public class RightOuterJoin {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("test");

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaPairRDD<String, Integer> nameRDD = sc.parallelizePairs(Arrays.asList(
                new Tuple2<String, Integer>("zhangsan", 18),
                new Tuple2<String, Integer>("lisi", 19),
                new Tuple2<String, Integer>("wangwu", 20),
                new Tuple2<String, Integer>("maliu", 21)
        ));
        JavaPairRDD<String, Integer> scoreRDD = sc.parallelizePairs(Arrays.asList(
                new Tuple2<String, Integer>("zhangsan", 100),
                new Tuple2<String, Integer>("lisi", 200),
                new Tuple2<String, Integer>("wangwu", 300),
                new Tuple2<String, Integer>("tianqi", 400)
        ));
        JavaPairRDD<String, Tuple2<Optional<Integer>,Integer>> rightOuterJoin = nameRDD.rightOuterJoin(scoreRDD);
        rightOuterJoin.foreach(new VoidFunction<Tuple2<String, Tuple2<Optional<Integer>, Integer>>>() {
            @Override
            public void call(Tuple2<String, Tuple2<Optional<Integer>, Integer>> stringTuple2Tuple2) throws Exception {
                System.out.println(stringTuple2Tuple2);
            }
        });
    }
}

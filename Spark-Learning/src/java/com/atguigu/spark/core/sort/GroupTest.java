package com.atguigu.spark.core.sort;

import org.apache.commons.collections.IteratorUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 分组取topN和topN
 * @author tianhao
 */
public class GroupTest {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("test");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> rdd1 = sc.textFile("./data/scores.txt");
        JavaPairRDD<String, Integer> rdd2 = rdd1.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String line) throws Exception {
                return new Tuple2<>(line.split("\t")[0], Integer.valueOf(line.split("\t")[1]));
            }
        });
        JavaPairRDD<String, Iterable<Integer>> rdd3 = rdd2.groupByKey();
        rdd3.foreach(new VoidFunction<Tuple2<String, Iterable<Integer>>>() {
            @Override
            public void call(Tuple2<String, Iterable<Integer>> tp) throws Exception {
                String cls = tp._1;
                Iterator<Integer> iter = tp._2.iterator();
                List<Integer> list = IteratorUtils.toList(iter);
                Collections.sort(list, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2 - o1;
                    }
                });

                if (list.size() > 3) {
                    for (int i = 0; i < 3; i++) {

                        System.out.println("class = " + cls + ",value = " + list.get(i));
                    }
                } else {
                    for (Integer score : list) {

                        System.out.println("class = " + cls + ",value = " + score);
                    }
                }

            }
        });

    }
}

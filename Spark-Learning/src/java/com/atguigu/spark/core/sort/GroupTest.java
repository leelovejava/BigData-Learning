package com.atguigu.spark.core.sort;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import java.util.Iterator;

/**
 * 分组求topN
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

                Integer[] top3Score = new Integer[3];

                while (iter.hasNext()) {
                    Integer currentScore = iter.next();
                    for (int i = 0; i < top3Score.length; i++) {
                        // 如果为null,直接赋值
                        if (top3Score[i] == null) {
                            top3Score[i] = currentScore;
                            break;
                        } else if (currentScore > top3Score[i]) {
                            // 大于 移动后面的元素
                            for (int j = 2; j > i; j--) {
                                top3Score[j] = top3Score[j - 1];
                            }
                            //赋值
                            top3Score[i] = currentScore;
                            break;
                        }
                    }
                }

                for (Integer cscore : top3Score) {

                    System.out.println("class = " + cls + ",score = " + cscore);
                }


            }
        });

    }
}

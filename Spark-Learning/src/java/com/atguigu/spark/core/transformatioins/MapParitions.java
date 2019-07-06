package com.atguigu.spark.core.transformatioins;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MapParitions {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("test");

        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> rdd = sc.parallelize(Arrays.asList("a", "b", "c", "d", "e", "f", "g"), 3);

        JavaRDD<String> transRDD = rdd.mapPartitions(new FlatMapFunction<Iterator<String>, String>() {
            @Override
            public Iterator<String> call(Iterator<String> iterator) throws Exception {
                List<String> list = new ArrayList<>();
                System.out.println("建立数据库连接。。。");
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    list.add(next);
                    System.out.println("插入数据库。。。" + next);
                }

                System.out.println("关闭数据库连接。。。");
                return list.iterator();
            }
        });
        transRDD.count();

//        JavaRDD<String> map = rdd.map(new Function<String, String>() {
//            @Override
//            public String call(String v1) throws Exception {
//                System.out.println("建立数据库连接。。。");
//                System.out.println("插入数据库。。。" + v1);
//                System.out.println("关闭数据库连接。。。");
//                return v1 + "~";
//            }
//        });
//        map.count();

    }
}

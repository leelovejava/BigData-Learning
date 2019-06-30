package com.atguigu.spark.core.action;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;

import java.util.Arrays;
import java.util.Iterator;

public class ForeachPartition {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("test");

        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> rdd = sc.parallelize(Arrays.asList("a", "b", "c", "d", "e", "f", "g"), 3);

        // 遍历分区,不返回rdd
        // mapPartitions 返回rdd
        rdd.foreachPartition(new VoidFunction<Iterator<String>>() {
            @Override
            public void call(Iterator<String> iter) throws Exception {
                System.out.println("建立数据库连接。。。");
                while (iter.hasNext()) {
                    String next = iter.next();
                    System.out.println("插入数据库。。。" + next);
                }
                System.out.println("关闭数据库连接。。。");

            }
        });

//        rdd.foreach(new VoidFunction<String>() {
//            @Override
//            public void call(String s) throws Exception {
//                System.out.println("建立数据库连接。。。");
//                System.out.println("插入数据库。。。"+s);
//                System.out.println("关闭数据库连接。。。");
//            }
//        });

    }
}

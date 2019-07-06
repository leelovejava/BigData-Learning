package com.atguigu.spark.core.examples;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.SparkSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 广播器
 */
public class BroadCastTest {
    public static void main(String[] args) {
        SparkSession session = SparkSession.builder().master("local").appName("broadcast").getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(session.sparkContext());
        List<String> nameList = new ArrayList<String>();
        nameList.add("zhangsan");
        nameList.add("lisi");

        Broadcast<List<String>> blackList = sc.broadcast(nameList);

        JavaRDD<String> nameRDD = sc.parallelize(Arrays.asList("zhangsan", "lisi", "wangwu", "zhaoliu", "tianqi"));
        JavaRDD<String> filter = nameRDD.filter(new Function<String, Boolean>() {
            @Override
            public Boolean call(String s) throws Exception {
                List<String> value = blackList.value();
                return value.contains(s);
            }
        });
        filter.foreach(new VoidFunction<String>() {
            @Override
            public void call(String s) throws Exception {
                System.out.println(s);
            }
        });


    }
}

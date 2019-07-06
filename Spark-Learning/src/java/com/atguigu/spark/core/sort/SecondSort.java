package com.atguigu.spark.core.sort;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import java.io.Serializable;

/***
 * 二次排序
 */
class MySort implements Comparable<MySort>,Serializable{

    private Integer first;
    private Integer second;

    public MySort(Integer f ,Integer s){
        this.first = f;
        this.second = s;

    }

    @Override
    public int compareTo(MySort that) {
        if(this.first == that.first){
            return this.second - that.second;
        }else {
            return this.first - that.first;
        }
    }
}

public class SecondSort {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("SparkSecondSort");
        JavaSparkContext sc = new JavaSparkContext(conf);


        JavaRDD<String> rdd1 = sc.textFile("./data/secondSort.txt");
        JavaPairRDD<MySort, String> rdd2 = rdd1.mapToPair(new PairFunction<String, MySort, String>() {
            @Override
            public Tuple2<MySort, String> call(String s) throws Exception {
                Integer first = Integer.valueOf(s.split(" ")[0]);
                Integer second = Integer.valueOf(s.split(" ")[1]);
                return new Tuple2<>(new MySort(first, second), s);
            }
        });
        rdd2.sortByKey(false).foreach(new VoidFunction<Tuple2<MySort, String>>() {
            @Override
            public void call(Tuple2<MySort, String> mySortStringTuple2) throws Exception {
                System.out.println(mySortStringTuple2._2);
            }
        });
    }
}

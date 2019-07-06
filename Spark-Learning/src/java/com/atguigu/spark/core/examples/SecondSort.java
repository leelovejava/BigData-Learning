package com.atguigu.spark.core.examples;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import java.io.Serializable;

class MySort implements Comparable<MySort> ,Serializable {
    Integer first ;
    Integer second ;

    public MySort(Integer first,Integer second){
        this.first  = first;
        this.second = second;
    }

    @Override
    public int compareTo(MySort mysort) {
        if(this.first == mysort.first){
            return -(this.second - mysort.second);
        }else{
            return -(this.first - mysort.first);
        }
    }
}

public class SecondSort {
    public static void main(String[] args) {
        SparkConf conf =new SparkConf();
        conf.setMaster("local");
        conf.setAppName("topn");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("./data/secondSort.txt");
        JavaPairRDD<MySort, String> mapToPair = lines.mapToPair(new PairFunction<String, MySort, String>() {
            @Override
            public Tuple2<MySort, String> call(String line) throws Exception {
                MySort mysort = new MySort(Integer.valueOf(line.split(" ")[0]), Integer.valueOf(line.split(" ")[1]));
                return new Tuple2<MySort, String>(mysort, line);
            }
        });

        mapToPair.sortByKey().foreach(new VoidFunction<Tuple2<MySort, String>>() {
            @Override
            public void call(Tuple2<MySort, String> tp) throws Exception {
                System.out.println(tp._2);
            }
        });
    }
}

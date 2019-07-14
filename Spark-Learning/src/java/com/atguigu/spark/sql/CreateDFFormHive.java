package com.atguigu.spark.sql;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SaveMode;

/**
 * 读取Hive中的数据,要使用HiveContext
 * HiveContext.sql(sql); 可以操作hive表,还可以操作虚拟的表
 *
 * @version spark 1.6
 */
public class CreateDFFormHive {
    /*public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setAppName("hive");
        // 在Spark1.6中没有SparkSession概念(2.x)
        JavaSparkContext sc = new JavaSparkContext(conf);
        // HiveContext是SQLContext的子类
        HiveContext hiveContext = new HiveContext(sc);
        hiveContext.sql("USE spark");
        // 在hive中创建student_info类
        hiveContext.sql("CREATE TABLE IF NOT EXISTS student_infos (name STRING,age INT) row format delimited filed");
        hiveContext.sql("load data local inpath 'root/test/student_infos' into table student_infos");

        *//**
         * 查询表生成DataFrame
         *//*
        DataFrame goodStudentDF = hiveContext.sql("SELECT si.name,so.age,ss.score from student_infos si JOIN student_scores ss on si.name=ss.name where ss.score>=80");
        goodStudentDF.registerTempTable("goodstudent");

        DataFrame result = hiveContext.sql("select * from goodstudent");
        result.show();

        *//**
         * 将结果保存到hive表 good_student_infos
         *//*
        goodStudentDF.write().mode(SaveMode.Overwrite).saveAsTable("good_student_infos");
    }*/

}

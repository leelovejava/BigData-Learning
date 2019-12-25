package com.leelovejava.hive;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * hive自定义函数
 * 实现数据脱敏
 * UDF函数可以直接应用于select语句，对查询结构做格式化处理后，再输出内容
 * a）自定义UDF需要继承org.apache.hadoop.hive.ql.UDF。
 * b）需要实现evaluate函数，evaluate函数支持重载。
 * a）把程序打包放到目标机器上去；
 * b）进入hive客户端，添加jar包：hive>add jar /run/jar/udf_test.jar;
 *
 * c）创建临时函数：hive>CREATE TEMPORARY FUNCTION add_example AS 'hive.udf.Add';
 * d）查询HQL语句：
 *     SELECT add_example(8, 9) FROM scores;
 *     SELECT add_example(scores.math, scores.art) FROM scores;
 *     SELECT add_example(6, 7, 8, 6.8) FROM scores;
 * e）销毁临时函数：hive> DROP TEMPORARY FUNCTION add_example;
 * @author leelovejava
 */
public class TuoMin extends UDF {

    public Text evaluate(final Text s) {
        if (s == null) {
            return null;
        }
        return new Text(s.toString().substring(0, 3) + "***");
    }

}

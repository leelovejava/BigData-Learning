# spark sql

## SparkSQL 1.6 &SparkSQl 2.3区别
读取数据
    
    SparkSQL 1.6中读取Hive中数据使用HiveContext
    
    SparkSQL 2.3使用SparkSession.enableHiveSupport
    
注册临时表
    1.6 sqlContext.registerTempTable(xxx)
    2.3 SparkSession.createOrReplaceTempView

HiveContext是SQLContext的子类

## UDF和UDAF函数

1.用户自定义函数。
可以自定义类实现UDFX接口

一对一

2.UDAF:用户自定义聚合函数
count、ave、max

无需UDTF(hive),原因Spark算子可以搞定,例如flatMap
一对多
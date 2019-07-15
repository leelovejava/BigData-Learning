## SparkSQL & Shark
    
   1.SparkSQL完全脱离了Hive的限制
   
   2.SparkSQL 底层解析优化器和执行引擎都是Spark
   
   3.SparkSQL 完全兼容Hive和Shark语法
   
   4.SparkSQL可以对RDD使用SQL查询，还可以将结果作为RDD返回使用

## Spark on Hive  -- SparkSQL
   
   Spark：解析，优化，执行引擎
   
   Hive : Hive 只是存储数据的地方


## Hive on Spark -- Shark
    Spark : 执行引擎
    Hive:解析优化，存储

## 加载DataFrame的方式
    1.读取JSon格式的文件加载DataFrame
        spark.read.json("./data/json")
        spark.read.format("json").load("./data/json")
        注意点： 加载Json格式的文件 生成DataFrame 列的顺序会按照Ascii码排序
    2.读取Json格式的Dataset
        val list :List[JsonString] = list
        val sparksession.implicits._
        val dataset:Dataset[String] = list.toDs()
        val df = sparkSession.read.json(dataset)
    3.读取RDD加载DataFrame
        通过反射的方式
            val rdd1:RDD[String] = sparksession.sparkContext.read(...)
            implicit sparksession.implicits._
            val rdd2:RDD[Person] = rdd1.map...
            val df = rdd2.toDF()
            注意： 需要借助外部对象，自动将对象中的属性当做列
        通过动态创建Schema方式
            val rdd1:RDD[String] = sparksession.sparkContext.read(...)
            val rdd2:RDD[Row] = rdd1.map....
            val structType = StructType(List[StructFiel](StructField(...)))
            val df = sparksession.createDataFrame(rdd2,structType)
            注意： StructType中的构建列的顺序必须要和构建Row中数据顺序保持一致
    4.读取parquet格式的数据加载DataFrame
        spark.read.parquet("./data/parquet")
        spark.read.format("parquet").load("./data/parquet")
    5.读取Mysql中的数据加载DataFrame
        第一种方式
            val props = new Properties().setProperty("user",xxx).setProperty("password",xx)
            val df  = spark.read.jdbc("jdbc:mysql://xxxx/spark","xxx",props)
        第二种方式
            val map = Map[String,String](url,driver,user,password,dbtable)
        第三种和第二种类似，只不过将options 分别设置处理
    6.读取Hive中的数据加载DataFrame
        首先配置Spark on Hive
        必须创建SparkSession指定开启Hive支持  enableHiveSupport()


## 保存DataFrame 
    保存成Parquet格式数据
        df1.write.mode(SaveMode.Append).format("parquet").save("./data/parquet")
    保存到MySQL表中
        result.write.mode(SaveMode.Append).jdbc("jdbc:mysql://192.168.179.14:3306/spark", "result", properties)
    保存到Hive表中
        df.write.mode(SaveMode.Overwrite).saveAsTable("good_student_infos")


## Spark on Hive配置
    1.在Spark的客户端spark/conf/hive-site.xml中配置
        <configuration>
        <property>
        <name>hive.metastore.uris</name>
        <value>thrift://mynode1:9083</value>
        </property>
        </configuration>
    2.启动HDFS集群，启动Hive服务端 ，启动Hive metastore服务
    3.启动SparkStandalone SparkShell 测试


## SparkSQL 1.6 & SparkSQL 2.3 区别
    1.SparkSQL 1.6中 读取Hive中数据使用HiveContext   ,   SparkSQL 2.3 使用SparkSession.enableHiveSupport() 读取
    2.SparkSQL1.6 注册临时表 sqlContxt.registerTempTable(xxx)   , SparkSQL 2.3 SparkSession.createOrReplaceTempView
    3. SparkSQL1.6 中HiveContext是SQLContext的子类



SparkSQL UDF
    User Defined Function 用户自定义函数


SparkSQL UDAF

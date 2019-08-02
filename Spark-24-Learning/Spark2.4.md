# Spark2.4新特性

## 

* 新的调度模型（Barrier Scheduling），使用户能够将分布式深度学习训练恰当地嵌入到 Spark 的 stage 中，以简化分布式训练工作流程。
* 添加了35个高阶函数，用于在 Spark SQL 中操作数组/map。
* 新增一个新的基于 Databricks 的 spark-avro 模块的原生 AVRO 数据源。
* PySpark 还为教学和可调试性的所有操作引入了热切的评估模式（eager evaluation mode）。
* Spark on K8S 支持 PySpark 和 R ，支持客户端模式（client-mode）。
* Structured Streaming 的各种增强功能。 例如，连续处理（continuous processing）中的有状态操作符。
* 内置数据源的各种性能改进。 例如，Parquet 嵌套模式修剪（schema pruning）。
* 支持 Scala 2.12

## Spark2.4支持图片格式数据源

Spark2.4开始支持内置的图片数据源读取器，可以直接读取图片的数据。



```
val df = spark.read.format("image").load("/opt/pic/")
```

目录可以还可以是/path/to/dir/**和分区目录。

**Image Schema**

读取的数据会生成一个DF，该DF就一列列名字叫做 image。但是其实他是个嵌套数据结构，具体结构如下：



```
root
|-- image: struct (nullable = true)
|    |-- origin: string (nullable = true)
|    |-- height: integer (nullable = true)
|    |-- width: integer (nullable = true)
|    |-- nChannels: integer (nullable = true)
|    |-- mode: integer (nullable = true)
|    |-- data: binary (nullable = true)
```

**orgin:**代表图片的路径。

height：图片高度

width：图片宽度

nChannels：颜色通道的数量。对于灰度图像，典型值为1，对于彩色图像（例如，RGB），典型值为3，对于具有alpha通道的彩色图像，典型值为4。

**mode：**整数标志，提供有关如何解释数据字段的信息。它指定数据存储的数据类型和通道顺序。希望（但不强制）字段的值映射到下面显示的OpenCV类型之一。OpenCV类型定义为1,2,3或4个通道，并为像素值定义了几种数据类型。通道顺序指定颜色的存储顺序。例如，如果有一个包含红色，蓝色和绿色组件的典型三通道图像，则有六种可能的排序。大多数库使用RGB或BGR。希望三（4）个通道OpenCV类型为BGR（A）顺序。

OpenCV中的类型到数字的映射（数据类型x通道数）

![img](https://mmbiz.qpic.cn/mmbiz_png/b96CibCt70iabianIHic1hBAAp84hpMCdz5V9r0OoulRAaldsc1dfbAibxicTjU31tQURgLY9WpqT10Z0jKcwAtpwYnA/640?tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

**data：**以二进制格式存储的图像数据。图像数据表示为具有尺寸形状（高度，宽度，n通道）和由schema字段指定的类型t的数组值的三维阵列。该数组以row-major顺序存储。

**通道顺序(channel order)**

通道顺序指定存储颜色的顺序。例如，如果您有一个包含红色，蓝色和绿色组件的典型三通道图像，则有六种可能的排序。大多数库使用RGB或BGR。预计三（4）个通道OpenCV类型为BGR（A）顺序

**案例**

对于有监督学习，可以用label作为分区列，目前label仅仅支持数字类型。

![img](https://mmbiz.qpic.cn/mmbiz_png/b96CibCt70iabianIHic1hBAAp84hpMCdz5VpIeXBlZWIZspJCpibGKphEvsWGYh49ELNjIVujFDibDP5SmiboSF7gKvQ/640?tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

```scala
val spark = SparkSession
  .builder()
  .appName("Spark reads pics")
  .master("local[2]")
  .getOrCreate()

val df = spark.read.format("image").load("/opt/pic")

df.printSchema()
df.select(("label"),
  ("image.origin"),
  ("image.height"),
  ("image.width"),
  ("image.nChannels"),
  ("image.mode")).show(1,false)

spark.stop()
```

![img](https://mmbiz.qpic.cn/mmbiz_png/b96CibCt70iabianIHic1hBAAp84hpMCdz5VoX8oyHTFmGr0TQiccltiaaYOR7JHhIJl7eHUWAKtqxAr22YpyMqCUo3g/640?tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

关于图片处理，目前spark支持的算法并不是很多，希望后续版本继续完善吧。



数据下载地址

http://download.tensorflow.org/example_images/flower_photos.tgz
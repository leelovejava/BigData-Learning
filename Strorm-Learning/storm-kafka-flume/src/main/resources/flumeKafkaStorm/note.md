# flume+kafka+storm

## 1、flume

### ①安装
1 ftp 上传linux： apache-flume-1.6.0-bin.tar.gz

2 解压到 /opt/sxt目录下

3 进入conf/ 目录，直接把flume-env.sh.template 文件改为flume-env.sh

4 修改flume-env.sh 文件中的export_java ,变为 export JAVA_HOME=/usr/java/jdk1.7.0_67


5 配置flume环境变量在profile中

### ②配置

conf/fk.conf

```
a1.sources = r1
a1.sinks = k1
a1.channels = c1

# Describe/configure the source
a1.sources.r1.type = avro
a1.sources.r1.bind = node06
a1.sources.r1.port = 41414

# Describe the sink
a1.sinks.k1.type = org.apache.flume.sink.kafka.KafkaSink
a1.sinks.k1.topic = testflume
a1.sinks.k1.brokerList = node06:9092,node07:9092,node08:9092
a1.sinks.k1.requiredAcks = 1
a1.sinks.k1.batchSize = 20
a1.sinks.k1.channel = c1

# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000000
a1.channels.c1.transactionCapacity = 10000

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
```

### ③启动
> bin/flume-ng agent -n a1 -c conf -f conf/fk.conf -Dflume.root.logger=DEBUG,console

## 2、kafka


创建生产者：
bin/kafka-console-producer.sh --broker-list node06:9092,node07:9092,node08:9092 --topic test

创建消费者：
bin/kafka-console-consumer.sh --zookeeper node06:2181,node07:2181,node08:2181 --from-beginning --topic test

启动
bin/kafka-server-start.sh config/server.properties

## 架构

[基于Flume的美团日志收集系统(一)架构和设计](https://www.cnblogs.com/yepei/p/4764026.html)

[基于Flume的美团日志收集系统(二)改进和优化](https://www.cnblogs.com/yepei/p/4764017.html)
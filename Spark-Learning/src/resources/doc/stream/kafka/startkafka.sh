nohup bin/kafka-server-start.sh   config/server.properties > kafka.log 2>&1 &

# 1. 启动zookeeper和kafka
# bin/kafka-server-start.sh -daemon ./config/server.properties

#  2、创建两个topic，一个为source，一个为target
# bin/kafka-topics.sh --create --zookeeper node01:2181,node02:2181,node03:2181,node04:2181 --replication-factor 2 --partitions 2 --topic source
# bin/kafka-topics.sh --create --zookeeper node01:2181,node02:2181,node03:2181,node04:2181 --replication-factor 2 --partitions 2 --topic targe

#  3、启动kafka console producer 写入source topic
#bin/kafka-console-producer.sh --broker-list node01:9092,node02:9092, node03:9092,node04:9092 --topic source

#  4、启动kafka console consumer 监听target topic
# bin/kafka-console-producer.sh --broker-list node01:9092,node02:9092, node03:9092,node04:9092 --topic source

#  5、启动kafkaStreaming程序：
# ./hadoop/spark-2.1.1-bin-hadoop2.7/bin/spark-submit --class com.atguigu.streaming.KafkaStreaming ./kafkastreaming-jar-with-dependencies.jar
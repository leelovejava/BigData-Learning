# kafka+sparkStreaming

## 组合
Spark 1.6 + kafka 0.8.2
Spark 2.3 + kafka 0.11



## 概念
    
    kafka 是分布式消息系统，默认消息是存储磁盘，默认保存7天。
    
    producer 
        消息生产者，两种机制，1.轮询，2.key的hash 。如果key 是null ,就是轮询，如果key 非null，按照key 的hash
    
    broker
        组成kafka集群的节点，broker之间没有主从关系，依赖zookeeper协调。
        
        broker负责消息的读写和存储。
        
        每个broker可以管理多个partition.一个partition只能由一个broker管理
    
    topic 
        一类消息/消息队列。
        每个topic是由多个partition 组成，为了提高并行度。由几个组成？可以创建指定。
    
    partition
        组成topic的单元，直接接触磁盘，消息是append到每个partition上的
        每个partition内部消息是强有序的。FIFO.
        每个partition有副本，几个副本？创建topic时，可以指定
    
    consumer
        每个consumer都有自己的消费者组
        每个消费者组在消费同一个topic时，这个topic中数据只能被消费一次
        不同的消费者组消费同一个topic互不影响
        kafka 0.8 之前 consumer 自己在zookeeper中维护消费者offset
        kafka 0.8 之后，consumer的offset是通过kafka 集群来维护的
    
    zookeeper
        负责协调broker
        存储原数据，broker，topic,partition..
        kafka 0.8之前还可以存储消费者offset

## doc
 [Spark Streaming + Kafka Integration Guide](http://spark.apache.org/docs/latest/streaming-kafka-integration.html)
 
 **Note: Kafka 0.8 support is deprecated as of Spark 2.3.0.**
 
 |                            | [spark-streaming-kafka-0-8](http://spark.apache.org/docs/latest/streaming-kafka-0-8-integration.html) | [spark-streaming-kafka-0-10](http://spark.apache.org/docs/latest/streaming-kafka-0-10-integration.html) |
 | :------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
 | Broker Version             | 0.8.2.1 or higher                                            | 0.10.0 or higher                                             |
 | API Maturity               | Deprecated                                                   | Stable                                                       |
 | Language Support           | Scala, Java, Python                                          | Scala, Java                                                  |
 | Receiver DStream           | Yes                                                          | No                                                           |
 | Direct DStream             | Yes                                                          | Yes                                                          |
 | SSL / TLS Support          | No                                                           | Yes                                                          |
 | Offset Commit API          | No                                                           | Yes                                                          |
 | Dynamic Topic Subscription | No                                                           | Yes                                                          |
 
Kafka集群的搭建

Kafka操作命令
    创建topic
        ./kafka-topics.sh  --zookeeper mynode3:2181,mynode4:2181,mynode5:2181 --create --topic t0721 --partitions 3 --replication-factor 3
    查看集群topic
        ./kafka-topics.sh  --zookeeper mynode3:2181,mynode4:2181,mynode5:2181 --list
    控制台当做kafka topic生产者
        ./kafka-console-producer.sh --broker-list mynode1:9092,mynode2:9092,mynode3:9092 --topic t0721
    控制台当做kafka topic消费者
        ./kafka-console-consumer.sh --zookeeper mynode3:2181,mynode4:2181,mynode5:2181  --topic t0721
    查看topic详细描述信息
        ./kafka-topics.sh --zookeeper mynode3:2181,mynode4:2181,mynode5:2181 --describe  --topic t0721
    删除topic

Kafka 的Leader均衡机制

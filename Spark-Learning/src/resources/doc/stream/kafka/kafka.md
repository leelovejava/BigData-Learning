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
        kafka 0.8 之后，consumer的offset是通过kafka 集群来维护的(默认topic: __consomer_offsets)
    
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
 
Kafka
    Kafka名词
        kafka是分布式的消息系统，默认将数据直接存储在磁盘，默认保存7天
        producer 
            消息的生产者，两种生产机制，1.轮询 2.基于key的hash
        broker
            组成kafka集群的节点，broker之间没有主从关系
            broker负责消息的读写和存储
            每个 broker可以管理多个partition
        topic
            一类消息的总称，消息队列
            topic是由partition组成，可以在创建topic时指定，一个topic有多个partition目的是为了并行
        partition
            partition是组成topic的单元
            partition是有副本的，可以在创建topic时指定
            一个partition只能由一个broker管理
        consumer
            消息的消费者，每个consumer都有自己的消费者组
            不同的组之间消费同一个topic时，互不影响
            同一个组内的不同的消费者消费同一个topic中的数据，相同的数据只能被消费一次
        zookeeper
            负责协调broker
            存储元数据，broker,topic，partition...
            在Kafka0.8.2 之前，负责存储消费者的offset

### Kafka集群的搭建

### 1.上传解压

### 2.在 kafka/config/server.properties
  borker.id
  port
  log.dir
  zookeeper.connect
            
### 3.发送到其他节点，修改broker.id

### 4.在每台节点启动Kafka
   

### Kafka操作命令
创建topic
> ./kafka-topics.sh  --zookeeper mynode3:2181,mynode4:2181,mynode5:2181 --create --topic t0721 --partitions 3 --replication-factor 3

查看集群topic
> ./kafka-topics.sh  --zookeeper mynode3:2181,mynode4:2181,mynode5:2181 --list

控制台当做kafka topic生产者
>  ./kafka-console-producer.sh --broker-list mynode1:9092,mynode2:9092,mynode3:9092 --topic t0721

控制台当做kafka topic消费者
> ./kafka-console-consumer.sh --zookeeper mynode3:2181,mynode4:2181,mynode5:2181  --topic t0721

查看topic详细描述信息
> ./kafka-topics.sh --zookeeper mynode3:2181,mynode4:2181,mynode5:2181 --describe  --topic t0721

删除topic
 在每台broker节点的/kafka/config/esrver.properties中配置delete.topic.enable = true
 重启每台节点生效
            
### Kafka 的Leader均衡机制
 当集群中broker节点有挂掉时，Kafka会自动为当前挂掉的broker节点管理的partition按照副本优先的规则寻找新的leader
     
  当挂掉的broker重新启动之后，kafka集群会自动将当前节点管理的paritition重新管理，将partition做一次均衡
     周期：leader.imbalance.check.interval.seconds   300s

### SparkStreaming + Kafka

#### SparkStreaming1.6 + Kafka0.8,2

##### Receiver
            Receiver模式采用了Receiver接收器的模式，需要local[2]
            Receiver模式主动将数据接收到Executor中，存储级别MEMORY_AND_DISK_SER_2
            Receiver模式使用zookeeper来管理消费者offset
            Receiver模式存在丢失数据问题，当Driver挂掉时，有可能丢失数据
            开启WAL机制，来确保不丢失数据
                1.开启WAL需要设置checkpoint
                2.开启WAL可以将接收来的数据存储级别降级 MEMORY_AND_DISKs_ser
                3.开启WAL机制之后，数据处理延迟加大
            Receiver模式顶层读取Kafka数据采用了Hight Level Consuer api 实现，不支持读取每批次数据offset
            Receiver模式的并行度： spark.streaming.blockInterval = 200ms ，可以降低这个参数，来增大并行度，最低不能低于50ms

##### Direct
            SparkStreaming1.6 +Kafka0.8.2 Direct模式 ，没有采用Receiver接收器模式处理数据，采用直连方式，直接从Kafka中获取当前批次的数据。
            Direct模式没有使用zookeeper维护消费者offset,使用Spark自己维护消费者offset。默认offset放在内存中，如果设置了checkpint那么在checkpointPoint中也有一份。
            Direct模式底层读取Kafka数据采用了Simple Consumer API 实现，可以获取每批次中数据的offset
            如果使用checkpoint这种方式管理消费者offset, 存在问题
                1.新逻辑不能执行
                2.重复处理数据问题
                
##### Receiver模式和Direct模式的区别
            1.Direct模式没有采用Receiver接收器模式，Receiver模式采用Receiver接收器模式
            2.Direct模式使用Spark管理offset,Receiver模式采用zookeeper管理消费者offset
            3.Direct模式底层读取kafka采用Simple Consumer API实现，Receiver模式采用High Level Consumer API实现。
            4.Direct模式的并行度与读取的Kafka中的topic是一对一的关系，Receiver模式的并行度是与spark.streamig.blockInterval有关系
            5.Direct模式可以手动维护消费者offset信息，Receiver模式不提供。

### 由kafka0.8.2 变换为kafka 0.11 
        
        1.删除每台节点的kafka信息
        
        2.删除数据存储目录
        
        3.进入zookeeper，删除原数据目录
        
#### SparkStreaming2.3.1 + Kafka0.11

##### Direct模式
            底层使用了新的consumerapi 
            可以利用Kafka维护消费者offset
            并行度与读取的topic的partition个数是一对一的
            
##### Direct模式 维护消费者offset
   1.利用spark checkpiont维护
        1.代码逻辑改变，新的代码逻辑不执行
        2.存在重复消费数据的问题
        
   2.Kafka维护
        enable.auto.commit = true,有可能丢失数据，最多消费一次数据
        enable.auto.commit = false
            需要在确保消息消费完成之后，异步提交offset
            问题：当Kafka停机超过1440分钟，消费者offset的信息会被清空
            
   3.手动维护消费者offset
        可以维护在外部的存储系统，比如Redis

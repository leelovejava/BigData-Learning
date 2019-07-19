SparkStreaming是Spark core api的扩展，支持处理流式数据，底层操作的是DStream

SparkStreaming & Strom
    1.Storm是实时处理数据，来一条处理一条数据，SparkStreaming微批处理数据，吞吐量大
    2.Storm擅长处理实时场景数据，SparkStreaming擅长处理复杂的流业务数据
    3.Storm的事务相对完善，SparkStreaming事务相对完善
    4.Storm支持动态的资源调度，Spark1.2之后也是支持的

SparkStreaming读取Socket数据
    1.SparkStreaming处理数据的流程
        1.首先启动一个job一直接收数据，将一段时间内的数据生成一个batch,batch封装到rdd中，rdd封装到DStream中
        2.这一段时间就是我们可以接受处理数据的延迟度，叫做batchInterval
        3.DStream有自己的transformation类算子，懒执行的，需要DStream的outputOperator类算子触发执行
        4.如果接收一批次数据的时间大于集群处理一批次数据的时间，集群资源不能充分利用
        5.如果接收一批次数据的时间小于集群处理一批次数据的时间，集群会有任务堆积
        6.最好集群接收一批次数据的时间与集群处理一批次数据的时间相同
    2.local[2]
    3.创建StreamingContext的两种方式 
        val ssc = new StreamingContext(SparkContext,Durations.Secondes(xx))
        val ssc = new StreamingContext(SparkConf,Durations.seconds(xx))
    4.batchInterval 的调节需要结合webui 调节
    5.DStream有自己的Transformation类算子，懒执行，需要DStream的outputOperator类算子触发执行
    6.StreamingContext一旦启动，不能添加新的业务逻辑
    7.StreamingContext.stop()之后，不能重新调用start启动，因为StreamingContext对象已经被销毁了
    8.StreamingContext.stop(true)默认关闭StreamingContext时，将SparkContext回收，也可以设置false,在关闭StreamingContext时，不会关闭SparkContext



算子
    Transformations
        flatMap
        
        map
        
        filter..
        
        updateStateByKey
            
            可以统计自从SparkStreaming启动以来所有key的状态，需要设置checkpoint保存状态
            
            默认状态是保存在内存中的，需要设置checkpoint来保存状态，多久将内存中的状态更新到checkpoint一次？
                如果batchInterval 小于10s，那么就10s更新一次
                如果batchInterval 大于10s,就batchInterval更新一次
        
        transform
            1.可以获取DStream中的RDD,对RDD使用RDD的transformation类算子执行，不必要使用action,需要返回RDD，返回的RDD被封装到DStream中
            2.transform算子内，获取的RDD的算子外的代码也是在Driver段执行，可以利用这个特点，做到动态改变广播变量
        
        reduceByKeyAndWindow
            窗口长度- window length -wl  , , 必须是batchInterval的整数倍
            滑动间隔 - sliding interval -si  , 必须是batchInterval的整数倍
            普通机制
            优化机制 ， 需要设置checkpoint
        
        window 
    
    outputOperator
        
        print
        
        foreachRDD
            
            1.foreachRDD可以获取DStream中的RDD，对获取的RDD可以使用RDD的transformation类算子执行，但是一定要使用action类算子触发执行
            
            2.foreachRDD算子内，获取的RDD的算子外的代码是在Driver端执行的,可以利用这个特点，做到动态的改变广播变量
        
        saveAsTextFile
        
        saveAsHadoopFile
        
        saveAsObjectFile

SparkStreaming 监控目录中的数据
    
    注意
        
        1.目录中的数据必须是原子性的产生到目录下，已经存在文件追加数据或者删除的数据行不能被监控到
        
        2.目录中数据格式要一致，文本格式
    
    监控目录中的数据没有采用Receiver接收器模式，不需要设置local[2]


Driver HA
    
    实现Driver HA 两部分实现
        1.在提交任务时指定 --supvise
        2.在代码中指定给新启动的Driver 恢复逻辑
    
    代码实现
        val ssc: StreamingContext = StreamingContext.getOrCreate(ckDir,CreateStreamingContext)
        checkpoint保存
            1.conf配置信息
            2.保存DStream 处理逻辑
            3.保存batch处理的位置信息，如果读取kafka数据 ，可以保存offset位置

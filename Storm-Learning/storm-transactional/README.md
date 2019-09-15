[Storm系列(十八)事务介绍](https://www.cnblogs.com/jianyuan/p/4910299.html)

[Transactional-topologies](http://storm.apache.org/releases/2.0.0-SNAPSHOT/Transactional-topologies.html)

Design 1
    强顺序流（强有序）

    引入事务（transaction）的概念，每个transaction（即每个tuple）关联一个transaction id。
    Transaction id从1开始，每个tuple会按照顺序+1。
    在处理tuple时，将处理成功的tuple结果以及transaction id同时写入数据库中进行存储。
    
    两种情况：
    1、当前transaction id与数据库中的transaction id不一致
    2、两个transaction id相同
    
    缺点：
    一次只能处理一个tuple，无法实现分布式计算
    
Design 2
    强顺序的Batch流 
    
    事务（transaction）以batch为单位，即把一批tuple称为一个batch，每次处理一个batch。
    每个batch（一批tuple）关联一个transaction id
    每个batch内部可以并行计算

Design 3
Storm's design

    将Topology拆分为两个阶段：
    1、Processing phase
    允许并行处理多个batch
    
    2、Commit phase
    保证batch的强有序，一次只能处理一个batch

Design details

Manages state - 状态管理
    
    Storm通过Zookeeper存储所有transaction相关信息（包含了：当前transaction id 以及batch的元数据信息）
    
    Coordinates the transactions - 协调事务
    Storm会管理决定transaction应该处理什么阶段（processing、committing）
    
    Fault detection - 故障检测
    Storm内部通过Acker机制保障消息被正常处理（用户不需要手动去维护）
    
    First class batch processing API
    Storm提供batch bolt接口

    

 三种事务：
1、普通事务
2、Partitioned Transaction - 分区事务
3、Opaque Transaction - 不透明分区事务

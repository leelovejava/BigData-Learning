package com.atguigu.sparkcore

/**
  * Spark持久化算子
  * 1. cache()    默认将数据持久化到内存中,懒执行算子
  * 2. persist()
  * 3. checkout()
  */
package object persist {
  /**
    * persist
    * 1).persist懒执行算子,可以手动指定存储级别
    * 2).常用存储级别
    * MEMORY_ONLY
    * MEMORY_ONLY_SER
    * MEMORY_AND_DISK
    * MEMORY_AND_DISK_SER
    * 注意:尽量避免使用DISK_ONLY级别和"_2"级别
    * _2: Spark容错性高
    * SER序列化,使用时需要反序列化,以空间换时间
    *
    * 3).persist使用场景
    * （a）某步骤计算特别耗时；
    * （b）计算链条特别长；
    * （c）checkpoint所在的RDD也一定要persist（在checkpoint之前，手动进行checkpoint）持久化数据，为什么？checkpoint的工作机制，是lazy级别的，在触发一个作业的时候，开始计算job，job算完之后，转过来spark的调度框架发现RDD有checkpoint标记，转过来框架本身又基于这个checkpoint再提交一个作业，checkpoint会触发一个新的作业，如果不进行持久化，进行checkpoint的时候会重算，如果第一次计算的时候就进行了persist，那么进行checkpoint的时候速度会非常的快。
    * （d）shuffle之后
    * （e）shuffle之前（框架默认帮助我们数据持久化到本地磁盘）
    */


  /**
    * cache
    * cache()=persist()=persist(StorageLevel.MEMORY_ONLY)
    * cache放在内存中只有一份副本,只放在内存中，放在内存的Heap中。不会保存在什么目录或者HDFS上，有可能在很多机器的内存，数据量比较小，也有可能在一台机器的内存中
    */

  /**
    * checkpoint
    * 当Spark Application linage非常长,且某些步骤计算非常复杂时，可以使用checkpoint
    *
    * cache和persist,由Spark管理,当Spark程序停止之后,数据会被清空
    * checkpoint需要指定目录,需要指定一个目录,这个目录在Spark程序停止之后不会被清空(由外部存储管理)
    * checkpoint经常用于状态的保存,而不是持久化
    */

  /**
    * 总结
    * cache&persist:
    *   cache和persist都是懒执行算子,需要action触发
    *   对RDD持久化时,可以赋值给一个变量,下次使用这个变量时，就是使用的持久化数据.
    *   持久化赋值变量,cache和persist后不能紧跟action算子(lines.cache().count() 此时返回的不是RDD)
    *   如果一个application中只有一个job,那么没必要使用持久化算子
    *   持久化数据在application执行完成之后会自动清空
    * checkpoint:
    *   懒执行算子,需要action触发执行,checkpoint需要执行外部的目录
    *   持久化的数据当application执行完成之后不会被清空,由外部的存储系统决定
    *   执行流程:
    */
}

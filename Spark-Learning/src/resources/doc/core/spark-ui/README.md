# WebUI

* 1. 如何查看数据有无数据倾斜
* 2. job->stage->tasks
* 3. 数据缓存 清除unpersist

## 开启历史日志服务器(配置historyServer)

http://spark.apache.org/docs/2.3.1/monitoring.html

spark-default.conf
```
# 1. 开启日志管理
# spark.eventLog.enabled           true
# 2. 日志存储路径 hdfs:mycluster/spark/log
# spark.eventLog.dir               hdfs://namenode:8021/directory
# 3. 日志恢复路径(恢复和存储同一个目录) hdfs:mycluster/spark/log
# spark.history.fs.logDirectory	   file:/tmp/spark-events
# 4. 日志优化选项,压缩日志
# spark.eventLog.compress         true
```

启动历史服务器
> sbin/start-history-server.sh

访问HistoryServer:
    node4:18080 ,之后所有提交的应用程序运行状况都会被记录
    
以json格式存储

开启日志压缩,节省空间
> spark.eventLog.compress         true

## Spark端口
4040  Spark application web-ui(Driver)
7077  Spark 的master与worker进行通讯的端口  standalone集群提交Application的端口
8080  Spark master web-ui 资源调度
8081  Spark worker web-ui 资源调度
18080 Spark history-server

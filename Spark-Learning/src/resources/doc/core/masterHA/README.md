# Master高可用搭建
[spark-standalone high-availability](https://spark.apache.org/docs/latest/spark-standalone.html#high-availability)

## Master是JVM进程中可能挂掉,当Master挂掉之后，不能提交启动Driver,所以需要搭建Master HA

## 1. 两种方式：
fileSystem(本地文件系统): 保存元数据,需要手动切换备用Master

zookeeper（分布式协调服务）: 保存元数据,自动切换

## 2. 配置
1)在Spark Master节点上配置主Master，配置spark-env.sh
```bash
# 1. 恢复模式
#spark.deploy.recoveryMode	Set to FILESYSTEM to enable single-node recovery mode (default: NONE).
#spark.deploy.recoveryDirectory	The directory in which Spark will store recovery state, accessible from the Master's perspective.

# 3. 存储master元数据目录 spark.deploy.zookeeper.dir
export SPARK_DAEMON_JAVA_OPTS="
-Dspark.deploy.recoveryMode=ZOOKEEPER
-Dspark.deploy.zookeeper.url=node3:2181,node4:2181,node5:2181 
-Dspark.deploy.zookeeper.dir=/sparkmaster0821"
```

2)发送到其他worker节点上
```bash
scp spark-env.sh root@node02:`pwd`
scp spark-env.sh root@node03:`pwd`
```

3)找一台节点（非主Master节点）配置备用 Master,修改spark-env.sh配置节点上的MasterIP
```bash
export SPARK_MASTER_IP=node02
```

4)启动集群之前启动zookeeper集群：
```bash
../zkServer.sh start
```

5)启动spark Standalone集群，启动备用Master

6)打开主Master和备用Master WebUI页面，观察状态

## 3.注意点
主备切换过程中不能提交Application。
主备切换过程中不影响已经在集群中运行的Application。因为Spark是粗粒度资源调度(Driver和worker通信)

影响Driver的注册和申请资源

## 4.测试验证
提交SparkPi程序，kill主Master(jps kill)观察现象。

提交任务命令改变

```bash
./spark-submit 
--master spark://node01:7077,node02:7077 
--class org.apache.spark.examples.SparkPi 
../lib/spark-examples-1.6.0-hadoop2.6.0.jar 
10000
```

status: standby、alive 


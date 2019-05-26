package com.cmcc.hbase.constant;

/**
 * 定义
 * @author tianhao
 */
public class Constants {
	public static final String TOPIC_NAME="test";
	/**
	 * hbase单独一个zookeeper，免得数据干扰
	 */
	public static final String HBASE_ZOOKEEPER_LIST = "node4:2181";

	/**
	 * zookeeper
	 */
	public static final String KAFKA_ZOOKEEPER_LIST = "node1:2181,node2:2181,node3:2181";
	/**
	 * kafka broker
	 */
	public static final String BROKER_LIST = "node1:9092,node2:9092,node3:9092";

	public static final String ZOOKEEPERS = "node1,node2,node3";
}

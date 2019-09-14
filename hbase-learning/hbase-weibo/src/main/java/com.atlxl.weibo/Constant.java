package com.atlxl.weibo;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * @author leelovejava
 */
public class Constant {


    /**
     * 命名空间
     */
    public static final String NAMESPACE = "weibo";

    /**
     * 内容表
     */
    public static final String CONTENT = "weibo:content";

    /**
     * 用户关系表
     */
    public static final String RELATIONS = "weibo:relations";

    /**
     * 收件箱表
     */
    public static final String INBOX = "weibo:inbox";

    /**
     * 微博内容表的表名
     */
    public static final byte[] TABLE_CONTENT = Bytes.toBytes("weibo:content");
    /**
     * 用户关系表的表名
     */
    public static final byte[] TABLE_RELATIONS = Bytes.toBytes("weibo:relations");
    /**
     * 微博收件箱表的表名
     */
    public static final byte[] TABLE_RECEIVE_CONTENT_EMAIL = Bytes.toBytes("weibo:receive_content_email");

    /**
     * hbase zookeeper主机
     */
    public static final String HBASE_ZOOKEEPER_QUORUM = "node01:2181,node02:2181,node03:2181";


}
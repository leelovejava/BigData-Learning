package com.leelovejava.hbase.optimiza;

import com.leelovejava.hbase.HBaseConn;
import org.apache.hadoop.hbase.client.Connection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hbase优化之写操作
 * @see 'https://www.cnblogs.com/panfeng412/archive/2012/03/08/hbase-performance-tuning-section2.html'
 */
public class HbaseWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private Connection connection;

    /**
     * 关闭自动flush
     */
    @Test
    public void setAutoFlush(){
        //HTable.setAutoFlush(boolean autoFlush, boolean clearBufferOnFail);
    }

    @Before
    public void setup() {
        connection = HBaseConn.getHBaseConn();
    }

    @After
    public void tearDown() {
        HBaseConn.closeConn();
    }
}

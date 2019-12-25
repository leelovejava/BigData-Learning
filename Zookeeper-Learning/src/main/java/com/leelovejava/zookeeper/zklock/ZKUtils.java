package com.leelovejava.zookeeper.zklock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * zookeeper工具类
 *
 * @author y.x
 * @date 2019/12/25
 */
public class ZKUtils {
    private static final String zkServerIps = "127.0.0.1:2181";

    public static synchronized CuratorFramework getClient() {
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(zkServerIps)
                .sessionTimeoutMs(6000).connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        return client;
    }
}

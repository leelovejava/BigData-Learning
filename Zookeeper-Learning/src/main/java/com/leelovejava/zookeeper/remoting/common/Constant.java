package com.leelovejava.zookeeper.remoting.common;

/**
 * @author leelovejava
 */
public interface Constant {

    String ZK_CONNECTION_STRING = "192.168.9.11:2181,192.168.9.12:2181,192.168.9.13:2181";
    int ZK_SESSION_TIMEOUT = 5000;
    String ZK_REGISTRY_PATH = "/registry";
    String ZK_PROVIDER_PATH = ZK_REGISTRY_PATH + "/provider";
}
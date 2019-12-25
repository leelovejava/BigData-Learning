package com.leelovejava.zookeeper.remoting.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author leelovejava
 */
public interface HelloService extends Remote {

    String sayHello(String name) throws RemoteException;
}
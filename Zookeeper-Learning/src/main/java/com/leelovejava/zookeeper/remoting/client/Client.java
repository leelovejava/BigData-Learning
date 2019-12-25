package com.leelovejava.zookeeper.remoting.client;


import com.leelovejava.zookeeper.remoting.common.HelloService;

/**
 * @author leelovejava
 */
public class Client {

    public static void main(String[] args) throws Exception {
        ServiceConsumer consumer = new ServiceConsumer();

        while (true) {
            HelloService helloService = consumer.lookup();
            String result = helloService.sayHello("Jack");
            System.out.println(result);
            Thread.sleep(3000);
        }
    }
}
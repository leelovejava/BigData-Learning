package com.leelovejava.zookeeper.zkdist;


/**
 * @author leelovejava
 */
public class Test {
    public static void main(String[] args) {

        System.out.println("主线程开始了");

        Thread thread = new Thread(() -> {
            System.out.println("线程开始了");
            while (true) {

            }
        });
        thread.setDaemon(true);
        thread.start();

    }


}

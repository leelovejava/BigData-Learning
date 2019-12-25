package com.leelovejava.zookeeper.zklock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.utils.CloseableUtils;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 可重入读写锁，一个读写锁管理一对相关的锁，一个负责读操作，另外一个负责写操作；读操作在写锁没被使用时可同时由多个进程使用，而写锁在使用时不允许读(阻塞)；此锁是可重入的；一个拥有写锁的线程可重入读锁，但是读锁却不能进入写锁，这也意味着写锁可以降级成读锁， 比如 请求写锁 --->读锁 ---->释放写锁；从读锁升级成写锁是不行的。
 * <p>
 * 可重入读写锁主要由两个类实现：InterProcessReadWriteLock、InterProcessMutex，使用时首先创建一个 InterProcessReadWriteLock 实例，然后再根据你的需求得到读锁或者写锁，读写锁的类型是 InterProcessMutex
 *
 * @author leelovejava
 */
public class SharedReentrantLockTest {
    private static final String LOCK_PATH = "/testZK/sharedreentrantlock";
    private static final Integer CLIENT_NUMS = 5;
    /**
     * 共享的资源
     */
    final static FakeLimitedResource resource = new FakeLimitedResource();
    private static CountDownLatch countDownLatch = new CountDownLatch(CLIENT_NUMS);

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < CLIENT_NUMS; i++) {
            String clientName = "client#" + i;
            new Thread(() -> {
                CuratorFramework client = ZKUtils.getClient();
                client.start();
                Random random = new Random();
                try {
                    final InterProcessMutex lock = new InterProcessMutex(client, LOCK_PATH);
                    // 每个客户端请求10次共享资源
                    for (int j = 0; j < 10; j++) {
                        if (!lock.acquire(10, TimeUnit.SECONDS)) {
                            throw new IllegalStateException(j + ". " + clientName + " 不能得到互斥锁");
                        }
                        try {
                            System.out.println(j + ". " + clientName + " 已获取到互斥锁");
                            resource.use(); // 使用资源
                            if (!lock.acquire(10, TimeUnit.SECONDS)) {
                                throw new IllegalStateException(j + ". " + clientName + " 不能再次得到互斥锁");
                            }
                            System.out.println(j + ". " + clientName + " 已再次获取到互斥锁");
                            // 申请几次锁就要释放几次锁
                            lock.release();
                        } finally {
                            System.out.println(j + ". " + clientName + " 释放互斥锁");
                            // 总是在finally中释放
                            lock.release();
                        }
                        Thread.sleep(random.nextInt(100));
                    }
                } catch (Throwable e) {
                    System.out.println(e.getMessage());
                } finally {
                    CloseableUtils.closeQuietly(client);
                    System.out.println(clientName + " 客户端关闭！");
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
        System.out.println("结束！");
    }
}
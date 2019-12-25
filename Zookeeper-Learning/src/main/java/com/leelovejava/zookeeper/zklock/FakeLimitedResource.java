package com.leelovejava.zookeeper.zklock;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 定义一个 FakeLimitedResource 类来模拟一个共享资源，该资源一次只能被一个线程使用，直到使用结束，下一个线程才能使用，否则会抛出异常
 * @author leelovejava
 */
public class FakeLimitedResource {
    private final AtomicBoolean inUse = new AtomicBoolean(false);

    /**
     * 模拟只能单线程操作的资源
     * @throws InterruptedException
     */
    public void use() throws InterruptedException {
        if (!inUse.compareAndSet(false, true)) {
            // 在正确使用锁的情况下，此异常不可能抛出
            throw new IllegalStateException("Needs to be used by one client at a time");
        }
        try {
            Thread.sleep((long) (100 * Math.random()));
        } finally {
            inUse.set(false);
        }
    }
}
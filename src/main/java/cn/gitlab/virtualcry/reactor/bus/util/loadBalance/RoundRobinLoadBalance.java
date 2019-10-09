package cn.gitlab.virtualcry.reactor.bus.util.loadBalance;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Somethings
 *
 * @author VirtualCry
 */
final class RoundRobinLoadBalance implements LoadBalance {

    private final Lock                                  readLock;
    private final Lock                                  writeLock;
    private final Map<Object, AtomicLong>               usageCounts;

    RoundRobinLoadBalance() {
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
        this.usageCounts = new HashMap<>();
    }

    @Override
    public <T> T get(List<T> items, Object... args) {
        Object key = args != null ? args[0] : null;
        Objects.requireNonNull(key, "'key' must not be null");
        if (items.isEmpty())
            return null;
        else
            return items.get((int)(getUsageCount(key).getAndIncrement() % (items.size())));
    }

    private AtomicLong getUsageCount(Object key) {
        readLock.lock();
        try {
            AtomicLong usageCount = this.usageCounts.get(key);
            if (usageCount == null) {
                readLock.unlock();
                writeLock.lock();
                try {
                    usageCount = this.usageCounts.get(key);
                    if (usageCount == null) {
                        usageCount = new AtomicLong();
                        this.usageCounts.put(key, usageCount);
                    }
                } finally {
                    writeLock.unlock();
                    readLock.lock();
                }
            }
            return usageCount;
        } finally {
            readLock.unlock();
        }
    }
}

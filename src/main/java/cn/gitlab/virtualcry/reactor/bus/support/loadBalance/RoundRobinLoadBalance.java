package cn.gitlab.virtualcry.reactor.bus.support.loadBalance;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Round-robin strategy, according to polling to get item.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
final class RoundRobinLoadBalance implements LoadBalance {

    private final Lock                                  readLock;
    private final Lock                                  writeLock;
    private final Map<Object, AtomicLong>               usageCounts;

    public RoundRobinLoadBalance() {
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
            AtomicLong usageCount = usageCounts.get(key);
            if (usageCount == null) {
                readLock.unlock();
                writeLock.lock();
                try {
                    usageCount = usageCounts.get(key);
                    if (usageCount == null) {
                        usageCount = new AtomicLong();
                        usageCounts.put(key, usageCount);
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

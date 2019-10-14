package cn.gitlab.virtualcry.reactor.bus.support.loadBalance;

import java.util.List;

/**
 * Generic interface for getting item with different load balance strategy.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public interface LoadBalance {

    <T> T get(List<T> items, Object... args);
}

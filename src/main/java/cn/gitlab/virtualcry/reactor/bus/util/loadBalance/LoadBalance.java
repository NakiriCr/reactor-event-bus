package cn.gitlab.virtualcry.reactor.bus.util.loadBalance;

import java.util.List;

/**
 * Somethings
 *
 * @author VirtualCry
 */
public interface LoadBalance {

    <T> T get(List<T> items, Object... args);
}

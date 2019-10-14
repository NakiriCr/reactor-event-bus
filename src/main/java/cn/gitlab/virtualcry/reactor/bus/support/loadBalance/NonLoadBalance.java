package cn.gitlab.virtualcry.reactor.bus.support.loadBalance;

import java.util.List;

/**
 * Without load balancing, take the first item.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
final class NonLoadBalance implements LoadBalance {

    @Override
    public <T> T get(List<T> items, Object... args) {
        if (items.isEmpty())
            return null;
        else
            return items.get(0);
    }
}

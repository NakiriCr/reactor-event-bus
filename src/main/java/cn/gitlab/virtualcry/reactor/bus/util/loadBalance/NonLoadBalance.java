package cn.gitlab.virtualcry.reactor.bus.util.loadBalance;

import java.util.List;

/**
 * Somethings
 *
 * @author VirtualCry
 */
public class NonLoadBalance implements LoadBalance {

    @Override
    public <T> T get(List<T> items, Object... args) {
        if (items.isEmpty())
            return null;
        else
            return items.get(0);
    }
}

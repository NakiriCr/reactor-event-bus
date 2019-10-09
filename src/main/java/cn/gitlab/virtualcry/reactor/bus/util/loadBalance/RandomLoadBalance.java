package cn.gitlab.virtualcry.reactor.bus.util.loadBalance;

import java.util.List;
import java.util.Random;

/**
 * Somethings
 *
 * @author VirtualCry
 */
final class RandomLoadBalance implements LoadBalance {

    @Override
    public <T> T get(List<T> items, Object... args) {
        if (items == null || items.isEmpty())
            return null;
        else
            return items.get(new Random().nextInt(items.size()));
    }
}

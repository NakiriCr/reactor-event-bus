package cn.gitlab.virtualcry.reactor.bus.support.loadBalance;

import java.util.List;
import java.util.Random;

/**
 * Random strategy, according to the random number to get item.
 *
 * @author VirtualCry
 * @since 3.2.2
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

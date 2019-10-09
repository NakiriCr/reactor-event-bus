package cn.gitlab.virtualcry.reactor.bus.registry;

import cn.gitlab.virtualcry.reactor.bus.selector.Selector;
import lombok.Getter;

/**
 * @author VirtualCry
 * @since 3.2.2
 */
@Getter
public class CacheableRegistration<K ,V> implements Registration<K, V> {

    private final Selector<K>                           selector;
    private final V                                     object;

    private volatile boolean                            cancelled;
    private volatile boolean                            cancelAfterUse;
    private volatile boolean                            paused;

    CacheableRegistration(Selector<K> selector, V object) {
        this.selector = selector;
        this.object = object;
        this.cancelled = false;
        this.cancelAfterUse = false;
        this.paused = false;
    }
}

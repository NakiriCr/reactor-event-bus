package cn.gitlab.virtualcry.reactor.bus.registry;

import cn.gitlab.virtualcry.reactor.bus.selector.Selector;

/**
 * A {@code Registration} represents an object that has been {@link Registry#register(Selector,
 * Object) registered} with a {@link Registry}.
 *
 * @param <K> The type of object that is matched by selectors
 * @param <V> The type of object that is registered
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public interface Registration<K, V> {

    /**
     * The {@link Selector} that was used when the registration was made.
     *
     * @return the registration's selector
     */
    Selector<K> getSelector();


    /**
     * The object that was registered
     *
     * @return the registered object
     */
    V getObject();
}

package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.registry.Registration;
import cn.gitlab.virtualcry.reactor.bus.selector.Selector;

import java.util.function.Consumer;

/**
 * Basic unit of event handling in Reactor.
 *
 * @author VirtualCry
 */
public interface Bus<T> {

    /**
     * Are there any {@link Registration}s with {@link Selector Selectors} that match the given {@code key}.
     *
     * @param key The key to be matched by {@link Selector Selectors}
     * @return {@literal true} if there are any matching {@literal Registration}s, {@literal false} otherwise
     */
    boolean respondsToKey(final Object key);


    /**
     * Register a {@link Consumer} to be triggered when a notification matches the given {@link
     * Selector}.
     *
     * @param selector The {@literal Selector} to be used for matching
     * @param consumer The {@literal Consumer} to be triggered
     * @return A {@link Registration} object that allows the caller to interact with the given mapping
     */
    <V extends T> Registration<Object, Consumer<? extends T>> on(final Selector selector,
                                                                         final Consumer<V> consumer);


    /**
     * Notify this component that an {@link Event} is ready to be processed.
     *
     * @param key The key to be matched by {@link Selector Selectors}
     * @param ev  The {@literal Event}
     * @return {@literal this}
     */
    Bus<T> notify(Object key, T ev);
}

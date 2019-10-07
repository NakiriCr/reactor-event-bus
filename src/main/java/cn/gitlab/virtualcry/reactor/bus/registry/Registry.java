package cn.gitlab.virtualcry.reactor.bus.registry;

import cn.gitlab.virtualcry.reactor.bus.selector.Selector;

import java.util.List;

/**
 * Implementations of this interface manage a registry of objects that works sort of like a Map, except Registries don't
 * use simple keys, they use {@link Selector}s to map their objects.
 *
 * @param <K> the type of objects that can be matched
 * @param <V> the type of objects that can be registered
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public interface Registry<K, V> extends Iterable<Registration<K, ? extends V>> {

    /**
     * Assign the given {@link Selector} with the given object.
     *
     * @param sel The left-hand side of the {@literal Selector} comparison check.
     * @param obj The object to assign.
     * @return {@literal this}
     */
    Registration<K, V> register(Selector<K> sel, V obj);


    /**
     * Remove any objects matching this {@code key}. This will unregister <b>all</b> objects matching the given
     * {@literal key}. There's no provision for removing only a specific object.
     *
     * @param key The key to be matched by the Selectors
     * @return {@literal true} if any objects were unassigned, {@literal false} otherwise.
     */
    boolean unregister(K key);


    /**
     * Select {@link Registration}s whose {@link Selector} {@link Selector#matches(Object)} the given {@code key}.
     *
     * @param key The key for the Selectors to match
     * @return A {@link List} of {@link Registration}s whose {@link Selector} matches the given key.
     */
    List<Registration<K, ? extends V>> select(K key);


    /**
     * Clear the {@link Registry}, resetting its state and calling {@link Registration#cancel()} for any active {@link
     * Registration}.
     */
    void clear();
}

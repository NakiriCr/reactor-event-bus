package cn.gitlab.virtualcry.reactor.bus.registry;

import cn.gitlab.virtualcry.reactor.bus.dispatch.Dispatcher;
import cn.gitlab.virtualcry.reactor.bus.selector.Selector;

/**
 * A {@code Registration} represents an object that has been {@link Registry#register(Selector,
 * Object) registered} with a {@link Registry}.
 *
 * @param <K> The type of object that is matched by selectors
 * @param <V> The type of object that is registered
 *
 * @author Jon Brisbin
 * @author Stephane Maldini
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


    /**
     * Cancel this {@link Registration} after it has been selected and used. {@link
     * Dispatcher} implementations should
     * respect this value and perform the cancellation.
     *
     * @return {@literal this}
     */
    Registration<K, V> cancelAfterUse();


    /**
     * Whether to cancel this {@link Registration} after use or not.
     *
     * @return {@literal true} if the registration will be cancelled after use, {@literal false}
     * otherwise.
     */
    boolean isCancelAfterUse();


    /**
     * Cancel this {@literal Registration} by removing it from its registry.
     *
     * @return {@literal this}
     */
    Registration<K, V> cancel();


    /**
     * Has this been cancelled?
     *
     * @return {@literal true} if this has been cancelled, {@literal false} otherwise.
     */
    boolean isCancelled();
}

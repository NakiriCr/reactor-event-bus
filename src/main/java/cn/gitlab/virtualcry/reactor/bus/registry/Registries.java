package cn.gitlab.virtualcry.reactor.bus.registry;

import java.util.function.Consumer;

/**
 * @author VirtualCry
 * @since 3.2.2
 */
public abstract class Registries {

    public static <K, V> Registry<K, V> create(Consumer<Registration<K, ? extends V>> onRegister,
                                               Consumer<Registration<K, ? extends V>> onUnregister) {
        return create(true, null, onRegister, onUnregister);
    }

    public static <K, V> Registry<K, V> create(boolean useL2Cache,
                                               Consumer<K> onNotFound,
                                               Consumer<Registration<K, ? extends V>> onRegister,
                                               Consumer<Registration<K, ? extends V>> onUnregister) {
        return new CachingRegistry<>(useL2Cache, onNotFound, onRegister, onUnregister);
    }
}

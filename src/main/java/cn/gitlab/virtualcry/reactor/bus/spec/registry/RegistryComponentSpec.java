package cn.gitlab.virtualcry.reactor.bus.spec.registry;

import cn.gitlab.virtualcry.reactor.bus.env.BuiltInEnvironment;
import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.support.PayloadConsumer;

/**
 * A generic environment-aware class for specifying components tha  need to be configured
 * with an {@link BuiltInEnvironment} and {@link Registry},
 *
 * @author VirtualCry
 * @see BuiltInEnvironment#registryConfig()
 * @since 3.2.2
 */
public interface RegistryComponentSpec {

    /**
     * Create a registry center.
     *
     * @return   A new {@link Registry} used in matching event consumers.
     */
    Registry<Object, PayloadConsumer<?>> create();
}

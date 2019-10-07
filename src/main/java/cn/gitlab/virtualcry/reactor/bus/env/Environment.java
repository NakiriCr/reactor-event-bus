package cn.gitlab.virtualcry.reactor.bus.env;

import cn.gitlab.virtualcry.reactor.bus.Bus;
import cn.gitlab.virtualcry.reactor.bus.spec.registry.RegistryComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.stream.EventStreamComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.consumer.EventConsumerComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.BusProcessor;

/**
 * Environment use in {@link cn.gitlab.virtualcry.reactor.bus.Bus} to config {@link
 * BusProcessor}
 *
 * @author VirtualCry
 */
public interface Environment {

    Environment ASYNCHRONOUS = new BuiltInAsyncEnvironment();
    Environment SYNCHRONOUS  = new BuiltInSyncEnvironment();


    /**
     * Create an environment builder.
     *
     @return   A new env builder {@link EventStreamComponentSpec} uses in {@link Bus}.
     */
    static BuiltInEnvironment.BuiltInEnvironmentBuilder builder() {
        return BuiltInEnvironment.builder();
    }


    /**
     * Create a registry center config .
     *
     * @return  A new config {@link RegistryComponentSpec} uses in registry center.
     * @since 3.2.2
     */
    RegistryComponentSpec registryConfig();


    /**
     * Create an event stream config .
     *
     * @return  A new config {@link EventStreamComponentSpec} uses in event stream.
     * @since 3.2.2
     */
    EventStreamComponentSpec eventStreamConfig();


    /**
     * Create an event consumer config .
     *
     * @return   A new config {@link EventStreamComponentSpec} uses in event consumer.
     * @since 3.2.2
     */
    EventConsumerComponentSpec eventConsumerConfig();
}

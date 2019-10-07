package cn.gitlab.virtualcry.reactor.bus.env;

import cn.gitlab.virtualcry.reactor.bus.spec.BuiltInEventConsumerComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.BuiltInEventStreamComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.BuiltInRegistryComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.consumer.EventConsumerComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.registry.RegistryComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.stream.EventStreamComponentSpec;

/**
 * A built-in synchronous environment. Use {@link reactor.core.publisher.ReplayProcessor} to receive
 {@link cn.gitlab.virtualcry.reactor.bus.Event} and use single {@link reactor.core.scheduler.Scheduler} to consume.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
final class BuiltInSyncEnvironment implements Environment {

    @Override
    public RegistryComponentSpec registryConfig() {
        return BuiltInRegistryComponentSpec.CACHING_REGISTRY;
    }

    @Override
    public EventStreamComponentSpec eventStreamConfig() {
        return BuiltInEventStreamComponentSpec.REPLAY_PROCESSOR;
    }

    @Override
    public EventConsumerComponentSpec eventConsumerConfig() {
        return BuiltInEventConsumerComponentSpec.SINGLE_SCHEDULER;
    }
}

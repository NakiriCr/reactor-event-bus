package cn.gitlab.virtualcry.reactor.bus.env;

import cn.gitlab.virtualcry.reactor.bus.spec.BuiltInEventStreamComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.BuiltInEventConsumerComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.BuiltInRegistryComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.registry.RegistryComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.stream.EventStreamComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.consumer.EventConsumerComponentSpec;

/**
 * A built-in asynchronous environment. Use {@link reactor.core.publisher.WorkQueueProcessor} to receive
 {@link cn.gitlab.virtualcry.reactor.bus.Event} and use parallel {@link reactor.core.scheduler.Scheduler} to consume.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
final class BuiltInAsyncEnvironment implements Environment {

    @Override
    public RegistryComponentSpec registryConfig() {
        return BuiltInRegistryComponentSpec.CACHING_REGISTRY;
    }

    @Override
    public EventStreamComponentSpec eventStreamConfig() {
        return BuiltInEventStreamComponentSpec.WORK_QUEUE_PROCESSOR;
    }

    @Override
    public EventConsumerComponentSpec eventConsumerConfig() {
        return BuiltInEventConsumerComponentSpec.PARALLEL_SCHEDULER;
    }
}

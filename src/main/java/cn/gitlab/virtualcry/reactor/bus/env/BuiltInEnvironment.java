package cn.gitlab.virtualcry.reactor.bus.env;

import cn.gitlab.virtualcry.reactor.bus.spec.BuiltInEventStreamComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.BuiltInEventConsumerComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.BuiltInRegistryComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.registry.RegistryComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.stream.EventStreamComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.consumer.EventConsumerComponentSpec;
import lombok.Builder;

/**
 * A built-in environment that allows custom configuration of {@link cn.gitlab.virtualcry.reactor.bus.Bus} env.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
@Builder
public final class BuiltInEnvironment implements Environment {

    private RegistryComponentSpec               registryComponentSpec;
    private EventStreamComponentSpec            eventStreamComponentSpec;
    private EventConsumerComponentSpec          eventConsumerComponentSpec;


    private BuiltInEnvironment(RegistryComponentSpec registryComponentSpec,
                               EventStreamComponentSpec eventStreamComponentSpec,
                               EventConsumerComponentSpec eventConsumerComponentSpec) {
        this.registryComponentSpec = registryComponentSpec == null ?
                BuiltInRegistryComponentSpec.CACHING_REGISTRY :registryComponentSpec;
        this.eventStreamComponentSpec = eventStreamComponentSpec == null ?
                BuiltInEventStreamComponentSpec.WORK_QUEUE_PROCESSOR : eventStreamComponentSpec;
        this.eventConsumerComponentSpec = eventConsumerComponentSpec == null ?
                BuiltInEventConsumerComponentSpec.PARALLEL_SCHEDULER : eventConsumerComponentSpec;
    }


    @Override
    public RegistryComponentSpec registryConfig() {
        return this.registryComponentSpec;
    }

    @Override
    public EventStreamComponentSpec eventStreamConfig() {
        return this.eventStreamComponentSpec;
    }

    @Override
    public EventConsumerComponentSpec eventConsumerConfig() {
        return this.eventConsumerComponentSpec;
    }
}

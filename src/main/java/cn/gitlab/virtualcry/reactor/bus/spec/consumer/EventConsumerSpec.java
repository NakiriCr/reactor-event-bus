package cn.gitlab.virtualcry.reactor.bus.spec.consumer;

import cn.gitlab.virtualcry.reactor.bus.env.BuiltInEnvironment;

/**
 * A helper class for configuring {@link EventConsumerComponentSpec} used in {@link BuiltInEnvironment}.
 *
 * @author VirtualCry
 * @see BuiltInEnvironment#eventConsumerConfig()
 * @since 3.2.2
 */
public interface EventConsumerSpec {

    /**
     * Generate a {@link reactor.core.publisher.TopicProcessor} configuration builder.
     *
     * @return  {@literal the configuration builder}
     */
    static SchedulerComponentSpec.SchedulerComponentSpecBuilder scheduler() {
        return SchedulerComponentSpec.builder();
    }
}

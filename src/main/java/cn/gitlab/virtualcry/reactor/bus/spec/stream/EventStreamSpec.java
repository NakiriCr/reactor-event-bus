package cn.gitlab.virtualcry.reactor.bus.spec.stream;

import cn.gitlab.virtualcry.reactor.bus.env.BuiltInEnvironment;

/**
 * A helper class for configuring {@link EventStreamComponentSpec} used in {@link BuiltInEnvironment}.
 *
 * @author VirtualCry
 * @see BuiltInEnvironment#eventStreamConfig()
 * @since 3.2.2
 */
public interface EventStreamSpec {

    /**
     * Generate a {@link reactor.core.publisher.TopicProcessor} configuration builder.
     *
     * @return {@literal the configuration builder}
     */
    static TopicProcessorComponentSpec.TopicProcessorComponentSpecBuilder topicProcessor() {
        return TopicProcessorComponentSpec.builder();
    }


    /**
     * Generate a {@link reactor.core.publisher.WorkQueueProcessor} configuration builder.
     *
     * @return {@literal the configuration builder}
     */
    static WorkQueueProcessorComponentSpec.WorkQueueProcessorComponentSpecBuilder workQueueProcessor() {
        return WorkQueueProcessorComponentSpec.builder();
    }


    /**
     * Generate a {@link reactor.core.publisher.ReplayProcessor} configuration builder.
     *
     * @return {@literal the configuration builder}
     */
    static ReplayProcessorComponentSpec.ReplayProcessorComponentSpecBuilder replayProcessor() {
        return ReplayProcessorComponentSpec.builder();
    }
}

package cn.gitlab.virtualcry.reactor.bus.spec.receiver;

/**
 * A helper class for configuring a new {@link reactor.core.publisher.FluxProcessor}.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public interface EventReceiverSpec {

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

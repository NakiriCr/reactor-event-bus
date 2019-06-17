package cn.gitlab.virtualcry.reactor.bus.spec;

/**
 * A helper class for configuring a new {@link reactor.core.publisher.FluxProcessor}.
 *
 * @author VirtualCry
 */
public interface EventProcessorSpec {

    /**
     * Generate a {@link reactor.core.publisher.TopicProcessor} configuration builder.
     *
     * @return {@literal the configuration builder}
     */
    static EventTopicProcessorComponentSpec.EventTopicProcessorComponentSpecBuilder topicProcessor() {
        return EventTopicProcessorComponentSpec.builder();
    }


    /**
     * Generate a {@link reactor.core.publisher.ReplayProcessor} configuration builder.
     *
     * @return {@literal the configuration builder}
     */
    static EventReplayProcessorComponentSpec.EventReplayProcessorComponentSpecBuilder replayProcessor() {
        return EventReplayProcessorComponentSpec.builder();
    }

}

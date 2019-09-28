package cn.gitlab.virtualcry.reactor.bus.spec.subscriber;

/**
 * A helper class for configuring a new {@link reactor.core.publisher.FluxProcessor}.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public interface EventSubscriberSpec {

    /**
     * Generate a {@link reactor.core.publisher.TopicProcessor} configuration builder.
     *
     * @return {@literal the configuration builder}
     */
    static SchedulerComponentSpec.SchedulerComponentSpecBuilder scheduler() {
        return SchedulerComponentSpec.builder();
    }
}

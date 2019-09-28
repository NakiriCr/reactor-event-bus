package cn.gitlab.virtualcry.reactor.bus.spec.subscriber;

import reactor.core.publisher.FluxProcessor;
import reactor.core.scheduler.Scheduler;

/**
 * Common interface for generate a {@link FluxProcessor} that uses for {@link
 * cn.gitlab.virtualcry.reactor.bus.support.EventProcessor}.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public interface EventSubscriberComponentSpec {

    /**
     * Create a {@link Scheduler} that uses for event subscriber.
     *
     * @return  {@literal the receiver or subscriber processor}
     */
    Scheduler create();
}

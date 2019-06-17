package cn.gitlab.virtualcry.reactor.bus.spec;

import cn.gitlab.virtualcry.reactor.bus.Event;
import reactor.core.publisher.FluxProcessor;

/**
 * Common interface for generate a {@link reactor.core.publisher.FluxProcessor} that uses for {@link
 * cn.gitlab.virtualcry.reactor.bus.processor.EventProcessor}.
 *
 * @author VirtualCry
 */
public interface EventProcessorComponentSpec {

    /**
     * Create a {@link FluxProcessor} that uses for event receiver and event subscriber.
     *
     * @param <T> the event type
     * @return  {@literal the receiver or subscriber processor}
     */
    <T extends Event> FluxProcessor<T, T> create();

}

package cn.gitlab.virtualcry.reactor.bus.spec.receiver;

import cn.gitlab.virtualcry.reactor.bus.Event;
import lombok.Builder;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.ReplayProcessor;

/**
 * A generic environment-aware class for specifying components tha  need to be configured with an {@link
 * cn.gitlab.virtualcry.reactor.bus.env.Environment},
 *
 * @author VirtualCry
 * @since 3.2.2
 */
@Builder
final class ReplayProcessorComponentSpec implements EventReceiverComponentSpec {

    private int                                         historySize;
    private boolean                                     unbounded;

    @Override
    public <T extends Event> FluxProcessor<T, T> create() {
        return ReplayProcessor.create(historySize, unbounded);
    }
}

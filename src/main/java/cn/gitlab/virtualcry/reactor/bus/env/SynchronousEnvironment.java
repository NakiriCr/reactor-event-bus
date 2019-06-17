package cn.gitlab.virtualcry.reactor.bus.env;

import cn.gitlab.virtualcry.reactor.bus.spec.EventProcessorComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.EventProcessorSpec;
import reactor.util.concurrent.Queues;

/**
 * A synchronous environment. Use {@link reactor.core.publisher.ReplayProcessor} to receive and subscribe {@link
 * cn.gitlab.virtualcry.reactor.bus.Event}s
 *
 * @author VirtualCry
 */
final class SynchronousEnvironment implements Environment {

    @Override
    public EventProcessorComponentSpec eventReceiverConfig() {
        return EventProcessorSpec.replayProcessor()
                .historySize(Queues.SMALL_BUFFER_SIZE)
                .unbounded(true)
                .build();
    }

    @Override
    public EventProcessorComponentSpec eventSubscriberConfig() {
        return EventProcessorSpec.replayProcessor()
                .historySize(Queues.SMALL_BUFFER_SIZE)
                .unbounded(true)
                .build();
    }

}

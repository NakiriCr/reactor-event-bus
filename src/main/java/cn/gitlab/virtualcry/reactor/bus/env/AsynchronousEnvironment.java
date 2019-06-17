package cn.gitlab.virtualcry.reactor.bus.env;

import cn.gitlab.virtualcry.reactor.bus.spec.EventProcessorComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.EventProcessorSpec;
import reactor.util.concurrent.Queues;

/**
 * A asynchronous environment. Use {@link reactor.core.publisher.TopicProcessor} to receive and subscribe {@link
 * cn.gitlab.virtualcry.reactor.bus.Event}s
 *
 * @author VirtualCry
 */
final class AsynchronousEnvironment implements Environment {

    @Override
    public EventProcessorComponentSpec eventReceiverConfig() {
        return EventProcessorSpec.topicProcessor()
                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                .share(true)
                .autoCancel(false)
                .build();
    }

    @Override
    public EventProcessorComponentSpec eventSubscriberConfig() {
        return EventProcessorSpec.topicProcessor()
                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                .share(true)
                .autoCancel(false)
                .build();
    }

}

package cn.gitlab.virtualcry.reactor.bus.env;

import cn.gitlab.virtualcry.reactor.bus.spec.receiver.EventReceiverComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.receiver.EventReceiverSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.subscriber.EventSubscriberComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.subscriber.EventSubscriberSpec;
import reactor.core.scheduler.Schedulers;
import reactor.util.concurrent.Queues;

/**
 * An asynchronous environment. Use {@link reactor.core.publisher.TopicProcessor} to receive
 * {@link cn.gitlab.virtualcry.reactor.bus.Event} and use {@link reactor.core.scheduler.Scheduler} to subscribe it.
 *
 * @author VirtualCry
 */
final class AsynchronousEnvironment implements Environment {

    @Override
    public EventReceiverComponentSpec eventReceiverConfig() {
        return EventReceiverSpec.workQueueProcessor()
                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                .share(true)
                .autoCancel(false)
                .build();
    }

    @Override
    public EventSubscriberComponentSpec eventSubscriberConfig() {
        return EventSubscriberSpec.scheduler()
                .scheduler(Schedulers.parallel())
                .build();
    }
}

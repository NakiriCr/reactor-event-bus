package cn.gitlab.virtualcry.reactor.bus.spec.receiver;

import cn.gitlab.virtualcry.reactor.bus.Event;
import lombok.Builder;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.WorkQueueProcessor;
import reactor.util.concurrent.WaitStrategy;

import java.util.concurrent.ExecutorService;

/**
 * A generic environment-aware class for specifying components tha  need to be configured with an {@link
 * cn.gitlab.virtualcry.reactor.bus.env.Environment},
 *
 * @author VirtualCry
 * @since 3.2.2
 */
@Builder
final class WorkQueueProcessorComponentSpec implements EventReceiverComponentSpec {

    private ExecutorService                             executor;
    private ExecutorService                             requestTaskExecutor;
    private int                                         bufferSize;
    private WaitStrategy waitStrategy;
    private boolean                                     share;
    private boolean                                     autoCancel;

    @Override
    public <T extends Event> FluxProcessor<T, T> create() {
        return WorkQueueProcessor.<T>builder()
                .name("EventReceiver")
                .executor(executor)
                .requestTaskExecutor(requestTaskExecutor)
                .bufferSize(bufferSize)
                .waitStrategy(waitStrategy)
                .share(share)
                .autoCancel(autoCancel)
                .build();
    }
}

package cn.gitlab.virtualcry.reactor.bus.spec.stream;

import cn.gitlab.virtualcry.reactor.bus.env.BuiltInEnvironment;
import lombok.Builder;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.WorkQueueProcessor;
import reactor.util.concurrent.WaitStrategy;

import java.util.concurrent.ExecutorService;

/**
 * A {@link WorkQueueProcessor} will be created as an event stream.
 *
 * @author VirtualCry
 * @see BuiltInEnvironment#eventStreamConfig()
 * @since 3.2.2
 */
@Builder
final class WorkQueueProcessorComponentSpec implements EventStreamComponentSpec {

    private ExecutorService                         executor;
    private ExecutorService                         requestTaskExecutor;
    private int                                     bufferSize;
    private WaitStrategy                            waitStrategy;
    private boolean                                 share;
    private boolean                                 autoCancel;


    @Override
    public <T> FluxProcessor<T, T> create() {
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

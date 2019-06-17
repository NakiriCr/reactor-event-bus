package cn.gitlab.virtualcry.reactor.bus.spec;

import cn.gitlab.virtualcry.reactor.bus.Event;
import lombok.Builder;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.TopicProcessor;
import reactor.util.concurrent.WaitStrategy;

import java.util.concurrent.ExecutorService;

/**
 * A generic environment-aware class for specifying components tha  need to be configured with an {@link
 * cn.gitlab.virtualcry.reactor.bus.env.Environment},
 *
 * @author VirtualCry
 */
@Builder
final class EventTopicProcessorComponentSpec implements EventProcessorComponentSpec {

    private String                                      name;
    private ExecutorService                             executor;
    private ExecutorService                             requestTaskExecutor;
    private int                                         bufferSize;
    private WaitStrategy waitStrategy;
    private boolean                                     share;
    private boolean                                     autoCancel;

    @Override
    public <T extends Event> FluxProcessor<T, T> create() {
        return TopicProcessor.<T>builder()
                .name(name)
                .executor(executor)
                .requestTaskExecutor(requestTaskExecutor)
                .bufferSize(bufferSize)
                .waitStrategy(waitStrategy)
                .share(share)
                .autoCancel(autoCancel)
                .build();
    }

}

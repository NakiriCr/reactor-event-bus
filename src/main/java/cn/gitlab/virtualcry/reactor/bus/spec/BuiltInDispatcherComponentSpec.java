package cn.gitlab.virtualcry.reactor.bus.spec;

import cn.gitlab.virtualcry.reactor.bus.Event;
import reactor.core.publisher.*;
import reactor.util.concurrent.Queues;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A generic environment-aware class for specifying components that need to be configured
 * with a {@link FluxProcessor}.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
@SuppressWarnings("unchecked")
public abstract class BuiltInDispatcherComponentSpec<SPEC extends
        BuiltInDispatcherComponentSpec<SPEC, TARGET>, TARGET> extends EventRoutingComponentSpec<SPEC, TARGET> {

    private DispatcherType                          dispatcherType;
    private int                                     dispatcherCount;


    BuiltInDispatcherComponentSpec() {
        this.dispatcherCount = Runtime.getRuntime().availableProcessors();
    }


    /**
     * Configures the component to use the default dispatcher
     * - {@link reactor.core.publisher.UnicastProcessor}.
     *
     * @return {@code this}
     */
    public final SPEC defaultDispatcher() {
        this.unicastDispatcher();
        return (SPEC) this;
    }


    /**
     * Configures the component to use the dispatcher
     * - {@link reactor.core.publisher.UnicastProcessor}.
     *
     * @return {@code inner this}
     */
    public final SPEC unicastDispatcher() {
        this.dispatcherType = DispatcherType.UNICAST_PROCESSOR;
        return (SPEC) this;
    }


    /**
     * Configures the component to use the dispatcher
     * - {@link reactor.core.publisher.ReplayProcessor}.
     *
     * @return {@code inner this}
     */
    public final SPEC replayDispatcher() {
        this.dispatcherType = DispatcherType.REPLAY_PROCESSOR;
        return (SPEC) this;
    }


    /**
     * Configures the component to use the dispatcher
     * - {@link reactor.core.publisher.TopicProcessor}.
     *
     * @return {@code inner this}
     */
    public final SPEC topicDispatcher() {
        this.dispatcherType = DispatcherType.TOPIC_PROCESSOR;
        return (SPEC) this;
    }


    /**
     * Configures the component to use the dispatcher
     * - {@link reactor.core.publisher.WorkQueueProcessor}.
     *
     * @return {@code inner this}
     */
    public final SPEC workQueueDispatcher() {
        this.dispatcherType = DispatcherType.WORK_QUEUE_PROCESSOR;
        return (SPEC) this;
    }


    /**
     * Configures the component to use the given {@code dispatcherCount}.
     *
     * @param dispatcherCount The count to use
     *
     * @return {@code this}
     */
    public final SPEC dispatcherCount(int dispatcherCount) {
        this.dispatcherCount = dispatcherCount;
        return (SPEC) this;
    }


    protected final List<FluxProcessor<Event<?>, Event<?>>> createBuiltInDispatchers() {
        if (dispatcherType == null)
            defaultDispatcher();
        return Stream.iterate(0, i -> i + 1)
                .limit(dispatcherCount)
                .map(i -> {
                    switch (dispatcherType) {
                        case UNICAST_PROCESSOR:
                            return UnicastProcessor.<Event<?>>create();
                        case REPLAY_PROCESSOR:
                            return ReplayProcessor.<Event<?>>create(Queues.SMALL_BUFFER_SIZE, true);
                        case TOPIC_PROCESSOR:
                            return TopicProcessor.<Event<?>>builder()
                                    .bufferSize(Queues.SMALL_BUFFER_SIZE)
                                    .share(true)
                                    .autoCancel(false)
                                    .build();
                        case WORK_QUEUE_PROCESSOR:
                            return WorkQueueProcessor.<Event<?>>builder()
                                    .bufferSize(Queues.SMALL_BUFFER_SIZE)
                                    .share(true)
                                    .autoCancel(false)
                                    .build();
                        default:
                            throw new IllegalArgumentException("Unknown dispatcher type.");
                    }
                })
                .collect(Collectors.toList());
    }

    protected DispatcherType getDispatcherType() {
        return dispatcherType;
    }

    protected enum DispatcherType {
        UNICAST_PROCESSOR, REPLAY_PROCESSOR, TOPIC_PROCESSOR, WORK_QUEUE_PROCESSOR
    }
}

package cn.gitlab.virtualcry.reactor.bus.spec;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.util.loadBalance.LoadBalanceStrategy;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;
import reactor.util.concurrent.Queues;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A generic environment-aware class for specifying components that need to be configured
 * with a {@link Scheduler}.
 *
 * @param <SPEC>
 * 		The DispatcherComponentSpec subclass
 * @param <TARGET>
 * 		The type that this spec will create
 *
 * @author VirtualCry
 */
@SuppressWarnings("unchecked")
public abstract class DispatcherComponentSpec<SPEC extends DispatcherComponentSpec<SPEC, TARGET>, TARGET> implements Supplier<TARGET> {

    private Class<? extends FluxProcessor>          dispatcherType;
    private List<FluxProcessor<Event<?>,
            Event<?>>>                              dispatchers;
    private LoadBalanceStrategy                     loadBalanceStrategy;
    private int                                     dispatcherCount;


    DispatcherComponentSpec() {
        this.defaultDispatcher();
        this.dispatcherCount = Runtime.getRuntime().availableProcessors();
        this.loadBalanceStrategy = LoadBalanceStrategy.RANDOM;
    }


    /**
     * Configures the component to use the default dispatcher - {@link UnicastProcessor}.
     *
     * @return {@code this}
     */
    public final SPEC defaultDispatcher() {
        this.unicastDispatcher();
        return (SPEC) this;
    }

    /**
     * Configures the component to use the dispatcher - {@link UnicastProcessor}.
     *
     * @return {@code this}
     */
    public final SPEC unicastDispatcher() {
        this.dispatcherType = UnicastProcessor.class;
        this.dispatchers = null;
        return (SPEC) this;
    }


    /**
     * Configures the component to use the dispatcher - {@link ReplayProcessor}.
     *
     * @return {@code this}
     */
    public final SPEC replayDispatcher() {
        this.dispatcherType = ReplayProcessor.class;
        this.dispatchers = null;
        return (SPEC) this;
    }

    /**
     * Configures the component to use the dispatcher - {@link TopicProcessor}.
     *
     * @return {@code this}
     */
    public final SPEC topicDispatcher() {
        this.dispatcherType = TopicProcessor.class;
        this.dispatchers = null;
        return (SPEC) this;
    }

    /**
     * Configures the component to use the dispatcher - {@link WorkQueueProcessor}.
     *
     * @return {@code this}
     */
    public final SPEC workQueueDispatcher() {
        this.dispatcherType = WorkQueueProcessor.class;
        this.dispatchers = null;
        return (SPEC) this;
    }

    public final SPEC dispatchers(List<FluxProcessor<Event<?>, Event<?>>> dispatchers) {
        this.dispatchers = dispatchers;
        return (SPEC) this;
    }

    /**
     * Configures the component to use the given {@code loadBalanceStrategy}.
     *
     * @param loadBalanceStrategy Strategy to use
     *
     * @return {@code this}
     */
    public final SPEC loadBalanceStrategy(LoadBalanceStrategy loadBalanceStrategy) {
        this.loadBalanceStrategy = loadBalanceStrategy;
        return (SPEC) this;
    }

    /**
     * Configures the component to use the given {@code loadBalanceStrategy}.
     *
     * @param dispatcherCount Count to use
     *
     * @return {@code this}
     */
    public final SPEC dispatcherCount(int dispatcherCount) {
        this.dispatcherCount = dispatcherCount;
        return (SPEC) this;
    }


    @Override
    public final TARGET get() {
        return this.configure(
                Optional.ofNullable(this.dispatchers)
                        .orElseGet(this::createDispatchers),
                Optional.ofNullable(this.loadBalanceStrategy)
                        .orElse(LoadBalanceStrategy.RANDOM)
        );
    }

    private List<FluxProcessor<Event<?>, Event<?>>> createDispatchers() {
        List<FluxProcessor<Event<?>, Event<?>>> dispatchers = new ArrayList<>();
        if (UnicastProcessor.class.equals(this.dispatcherType)) {
            for (int i = 0; i < this.dispatcherCount; i++) {
                dispatchers.add(
                        UnicastProcessor.create()
                );
            }
        }
        else if (ReplayProcessor.class.equals(this.dispatcherType)) {
            for (int i = 0; i < this.dispatcherCount; i++) {
                dispatchers.add(
                        ReplayProcessor.create(Queues.SMALL_BUFFER_SIZE, true)
                );
            }
        }
        else if (TopicProcessor.class.equals(this.dispatcherType)) {
            for (int i = 0; i < this.dispatcherCount; i++) {
                dispatchers.add(
                        TopicProcessor.<Event<?>>builder()
                                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                                .share(true)
                                .autoCancel(false)
                                .build()
                );
            }
        }
        else if (WorkQueueProcessor.class.equals(this.dispatcherType)) {
            for (int i = 0; i < this.dispatcherCount; i++) {
                dispatchers.add(
                        WorkQueueProcessor.<Event<?>>builder()
                                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                                .share(true)
                                .autoCancel(false)
                                .build()
                );
            }
        }
        return dispatchers;
    }

    protected abstract TARGET configure(List<FluxProcessor<Event<?>, Event<?>>> dispatcherProcessors,
                                        LoadBalanceStrategy loadBalanceStrategy);
}

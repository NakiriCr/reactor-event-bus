package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.filter.PassThroughFilter;
import cn.gitlab.virtualcry.reactor.bus.registry.CachingRegistry;
import cn.gitlab.virtualcry.reactor.bus.registry.Registration;
import cn.gitlab.virtualcry.reactor.bus.registry.Registries;
import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.routing.ConsumerFilteringRouter;
import cn.gitlab.virtualcry.reactor.bus.routing.Router;
import cn.gitlab.virtualcry.reactor.bus.selector.Selector;
import cn.gitlab.virtualcry.reactor.bus.spec.EventBusSpec;
import cn.gitlab.virtualcry.reactor.bus.support.*;
import cn.gitlab.virtualcry.reactor.bus.util.loadBalance.LoadBalanceStrategy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.UnicastProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static cn.gitlab.virtualcry.reactor.bus.spec.EventRoutingComponentSpec.ON_REGISTER;
import static cn.gitlab.virtualcry.reactor.bus.spec.EventRoutingComponentSpec.ON_UNREGISTER;

/**
 * A reactor is an event gateway that allows other components to register {@link Event} {@link PayloadConsumer}s
 * that can subsequently be notified of events.
 *
 * @author VirtualCry
 */
public class EventBus implements Bus<Event<?>>, Consumer<Event<?>> {

    private final UUID                                  id;
    private final Registry<Object,
            PayloadConsumer<?>>                         consumerRegistry;
    private final Dispatcher<Event<?>>                  dispatcher;


    /**
     * Create a new {@link EventBusSpec} to configure a Reactor.
     *
     * @return The Reactor spec
     */
    public static EventBusSpec config() {
        return new EventBusSpec();
    }


    /**
     * Create a new {@link EventBus} using the given {@link FluxProcessor}.
     *
     * @param dispatcher The name of the {@link FluxProcessor} to use.
     * @return A new {@link EventBus}
     */
    public static EventBus create(List<FluxProcessor<Event<?>, Event<?>>> dispatchers) {
        return new EventBusSpec().dispatchers(dispatchers).get();
    }



    /**
     * Create a new {@literal Reactor} that uses the given {@link Scheduler}. The reactor will use a default {@link
     * Router} that broadcast events to all of the registered consumers that {@link
     * Selector#matches(Object) match}
     * the notification key and does not perform any type conversion.
     *
     * @param dispatcher The {@link Scheduler} to use. May be {@code null} in which case a new {@link
     *                   reactor.core.publisher.FluxProcessor} is used
     */
    public EventBus(@Nullable List<Dispatcher<Event<?>>> dispatchers) {
        this(dispatchers, null);
    }



    /**
     * Create a new {@literal Reactor} that uses the given {@link Scheduler}. The reactor will use a default {@link
     * CachingRegistry}.
     *
     * @param dispatcher The {@link FluxProcessor} to use. May be {@code null} in which case a new synchronous  dispatcher
     *                   is used.
     * @param router     The {@link Router} used to route events to {@link Consumer Consumers}. May be {@code null} in
     *                   which case the
     *                   default event router that broadcasts events to all of the registered consumers that {@link
     *                   Selector#matches(Object) match} the notification key and does not perform any type conversion
     *                   will be used.
     */
    public EventBus(@Nullable List<Dispatcher<Event<?>>> dispatchers,
                    @Nullable Router router) {
        this(dispatchers, router, null);
    }

    public EventBus(@Nullable List<Dispatcher<Event<?>>> dispatchers,
                    @Nullable Router router,
                    @Nullable Consumer<Throwable> dispatchErrorHandler) {
        this(Registries.create(ON_REGISTER, ON_UNREGISTER), dispatchers, router, dispatchErrorHandler, LoadBalanceStrategy.RANDOM);
    }

    /**
     * Create a new {@literal Reactor} that uses the given {@code dispatcher} and {@code eventRouter}.
     *
     * @param dispatcher       The {@link FluxProcessor} to use. May be {@code null} in which case a new synchronous
     *                         dispatcher is used.
     * @param router           The {@link Router} used to route events to {@link Consumer Consumers}. May be {@code
     *                         null} in which case the
     *                         default event router that broadcasts events to all of the registered consumers that {@link
     *                         Selector#matches(Object) match} the notification key and does not perform any type
     *                         conversion will be used.
     * @param consumerRegistry The {@link Registry} to be used to match {@link Selector} and dispatch to {@link
     *                         Consumer}.
     */
    public EventBus(@NonNull Registry<Object, PayloadConsumer<?>> consumerRegistry,
                    @Nullable List<Dispatcher<Event<?>>> dispatchers,
                    @Nullable Router router,
                    @Nullable Consumer<Throwable> dispatchErrorHandler,
                    @NonNull LoadBalanceStrategy loadBalanceStrategy) {
        this.id = UUID.randomUUID();
        this.consumerRegistry = consumerRegistry;
        List<Dispatcher<Event<?>>> finalDispatchers = Optional.ofNullable(dispatchers)
                .orElseGet(() -> {
                    Router finalRouter = Optional.ofNullable(router)
                            .orElseGet(() -> new ConsumerFilteringRouter(
                                    new PassThroughFilter(), Schedulers.newParallel("EventReceiver"))
                            );
                    DispatcherSubscriber<Event<?>> subscriber = new EventDispatcherSubscriber(
                            consumerRegistry,
                            finalRouter,
                            dispatchErrorHandler
                    );
                    List<Dispatcher<Event<?>>> dispatcherList = new ArrayList<>();
                    for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
                        Dispatcher<Event<?>> dispatcher = new EventDispatcher(UnicastProcessor.create(), subscriber);
                        dispatcherList.add(dispatcher);
                    }
                    return dispatcherList;
                });
        this.dispatcher = new LoadBalanceEventDispatcher<>(finalDispatchers, loadBalanceStrategy);
    }

    /**
     * Get the unique, time-used {@link UUID} of this {@literal Reactor}.
     *
     * @return The {@link UUID} of this {@literal Reactor}.
     */
    public UUID getId() {
        return id;
    }


    @Override
    public boolean respondsToKey(final Object key) {
        return this.consumerRegistry.select(key).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Registration<Object, PayloadConsumer<V>> on(Selector selector, PayloadConsumer<V> consumer) {
        return this.consumerRegistry.register(selector, consumer);
    }

    @Override
    public Bus<Event<?>> notify(@NonNull final Object key,
                                @NonNull final Event<?> ev) {
        ev.setKey(key);
        this.dispatcher.onNext(ev);
        return this;
    }


    /**
     * Pass values accepted by this {@code Stream} into the given {@link Bus}, notifying with the given key.
     *
     * @param key the key to notify on
     * @param source the {@link Publisher} to consume
     * @return {@literal new Stream}
     * @since 3.2.2
     */
    public final EventBus notify(@NonNull final Publisher<? extends Event> source,
                                 @NonNull final Object key) {
        return this.notify(source, mapper -> key);
    }

    /**
     * Pass values accepted by this {@code Stream} into the given {@link Bus}, notifying with the given key.
     *
     * @param source the {@link Publisher} to consume
     * @param keyMapper  the key function mapping each incoming data to a key to notify on
     * @return {@literal new Stream}
     * @since 3.2.2
     */
    public final <T extends Event> EventBus notify(@NonNull final Publisher<? extends T> source,
                                                   @NonNull final Function<? super T, ?> keyMapper) {
        Flux.from(source).doOnNext(event -> this.notify(keyMapper.apply(event), event)).subscribe();
        return this;
    }

    /**
     * Notify this component that the given {@link Supplier} can provide an event that's ready to be
     * processed.
     *
     * @param key      The key to be matched by {@link Selector Selectors}
     * @param supplier The {@link Supplier} that will provide the actual {@link Event}
     * @return {@literal this}
     * @since 3.2.2
     */
    public EventBus notify(Object key, Supplier<? extends Event> supplier) {
        this.notify(key, supplier.get());
        return this;
    }

    /**
     * Notify this component that the consumers registered with a {@link Selector} that matches the {@code key} should be
     * triggered with a {@literal null} input argument.
     *
     * @param key The key to be matched by {@link Selector Selectors}
     * @return {@literal this}
     * @since 3.2.2
     */
    public EventBus notify(Object key) {
        this.notify(key, new Event<>(Void.class));
        return this;
    }

    @Override
    public void accept(Event ev) {
        this.notify(ev.getClass(), ev);
    }
}

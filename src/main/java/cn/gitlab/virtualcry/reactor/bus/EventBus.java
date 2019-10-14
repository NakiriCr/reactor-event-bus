package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.dispatch.*;
import cn.gitlab.virtualcry.reactor.bus.filter.PassThroughFilter;
import cn.gitlab.virtualcry.reactor.bus.registry.CachingRegistry;
import cn.gitlab.virtualcry.reactor.bus.registry.Registration;
import cn.gitlab.virtualcry.reactor.bus.registry.Registries;
import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.routing.ConsumerFilteringRouter;
import cn.gitlab.virtualcry.reactor.bus.routing.Router;
import cn.gitlab.virtualcry.reactor.bus.selector.Selector;
import cn.gitlab.virtualcry.reactor.bus.selector.Selectors;
import cn.gitlab.virtualcry.reactor.bus.spec.EventBusSpec;
import cn.gitlab.virtualcry.reactor.bus.support.Assert;
import cn.gitlab.virtualcry.reactor.bus.support.loadBalance.LoadBalanceStrategy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.UnicastProcessor;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.gitlab.virtualcry.reactor.bus.spec.EventRoutingComponentSpec.ON_REGISTER;
import static cn.gitlab.virtualcry.reactor.bus.spec.EventRoutingComponentSpec.ON_UNREGISTER;

/**
 * A reactor is an event gateway that allows other components to register {@link Event} {@link Consumer}s that can
 * subsequently be notified of events. A consumer is typically registered with a {@link Selector} which, by matching on
 * the notification key, governs which events the consumer will receive. </p> When a {@literal Reactor} is notified of
 * an {@link Event}, a task is dispatched using the reactor's {@link Dispatcher} which causes it to be executed on a
 * thread based on the implementation of the {@link Dispatcher} being used.
 *
 * @author VirtualCry
 */
@SuppressWarnings("unchecked")
public class EventBus implements Bus<Event<?>>, Consumer<Event<?>> {

    private final UUID                                  id;
    private final Registry<Object,
            Consumer<? extends Event<?>>>               consumerRegistry;
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
     * @param dispatchers The {@link FluxProcessor}s to use.
     * @return A new {@link EventBus}
     */
    public static EventBus create(List<FluxProcessor<Event<?>, Event<?>>> dispatchers) {
        return new EventBusSpec().dispatchers(dispatchers).get();
    }



    /**
     * Create a new {@literal Reactor} that uses the given {@link Dispatcher}. The reactor will use a default {@link
     * Router} that broadcast events to all of the registered consumers that {@link
     * Selector#matches(Object) match}
     * the notification key and does not perform any type conversion.
     *
     * @param dispatchers The {@link Dispatcher}s to use. May be {@code null} in which case use {@link UnicastProcessor}
     *                    to create new {@link Dispatcher}s is used
     */
    public EventBus(@Nullable List<Dispatcher<Event<?>>> dispatchers) {
        this(dispatchers, null);
    }



    /**
     * Create a new {@literal Reactor} that uses the given {@link Dispatcher}. The reactor will use a default {@link
     * CachingRegistry}.
     *
     * @param dispatchers The {@link Dispatcher} to use. May be {@code null} in which case use {@link UnicastProcessor}
     *                    to create new {@link Dispatcher}s is used
     * @param router     The {@link Router} used to route events to {@link Consumer Consumers}. May be {@code null}
     *                   in which case the
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
     * @param dispatchers      The {@link Dispatcher} to use. May be {@code null} in which case use {@link UnicastProcessor}
     *                         to create new {@link Dispatcher}s is used
     * @param router           The {@link Router} used to route events to {@link Consumer Consumers}. May be {@code
     *                         null} in which case the
     *                         default event router that broadcasts events to all of the registered consumers that {@link
     *                         Selector#matches(Object) match} the notification key and does not perform any type
     *                         conversion will be used.
     * @param consumerRegistry The {@link Registry} to be used to match {@link Selector} and dispatch to {@link
     *                         Consumer}.
     */
    public EventBus(@NonNull Registry<Object, Consumer<? extends Event<?>>> consumerRegistry,
                    @Nullable List<Dispatcher<Event<?>>> dispatchers,
                    @Nullable Router router,
                    @Nullable Consumer<Throwable> dispatchErrorHandler,
                    @NonNull LoadBalanceStrategy loadBalanceStrategy) {
        this.id = UUID.randomUUID();
        this.consumerRegistry = consumerRegistry;
        List<Dispatcher<Event<?>>> finalDispatchers = Optional.ofNullable(dispatchers)
                .orElseGet(() -> {
                    Router finalRouter = router != null ? router : new ConsumerFilteringRouter(
                            new PassThroughFilter(),
                            Schedulers.immediate(),
                            Schedulers.newParallel("EventConsumer")
                    );
                    DispatcherSubscriber<Event<?>> subscriber = new EventDispatcherSubscriber(
                            consumerRegistry,
                            finalRouter,
                            dispatchErrorHandler
                    );
                    return Stream.iterate(0, i -> i + 1)
                            .limit(Runtime.getRuntime().availableProcessors())
                            .map(i -> new EventDispatcher(UnicastProcessor.create(), subscriber))
                            .collect(Collectors.toList());
                });
        this.dispatcher = new EventLoadBalanceDispatcher<>(finalDispatchers, loadBalanceStrategy);
    }


    /**
     * Get the unique, time-used {@link UUID} of this {@literal Reactor}.
     *
     * @return The {@link UUID} of this {@literal Reactor}.
     */
    public UUID getId() {
        return id;
    }


    /**
     * Get the {@link Registry} is use to maintain the {@link Consumer}s currently listening for events on this {@literal
     * Reactor}.
     *
     * @return The {@link Registry} in use.
     */
    public Registry<Object, Consumer<? extends Event<?>>> getConsumerRegistry() {
        return consumerRegistry;
    }


    /**
     * Get the {@link Dispatcher} currently in use.
     *
     * @return The {@link Dispatcher}.
     */
    public Dispatcher<Event<?>> getDispatcher() {
        return dispatcher;
    }


    @Override
    public boolean respondsToKey(final Object key) {
        return consumerRegistry.select(key).isEmpty();
    }

    @Override
    public <V extends Event<?>> Registration<Object,
            Consumer<? extends Event<?>>> on(final Selector selector,
                                                     final Consumer<V> consumer) {
        Assert.notNull(selector, "Selector cannot be null.");
        Assert.notNull(consumer, "Consumer cannot be null.");
        // proxy.
        Consumer<V> proxyConsumer = ev -> {
            if (null != selector.getHeaderResolver()) {
                ev.getHeaders().setAll(selector.getHeaderResolver().resolve(ev.getKey()));
            }
            consumer.accept(ev);
        };
        // register.
        return consumerRegistry.register(selector, proxyConsumer);
    }

    @Override
    public EventBus notify(Object key, Event<?> ev) {
        Assert.notNull(key, "Key cannot be null.");
        Assert.notNull(ev, "Event cannot be null.");
        ev.setKey(key);
        dispatcher.onNext(ev);
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
    public final EventBus notify(@NonNull final Publisher<?> source, @NonNull final Object key) {
        return notify(source, mapper -> key);
    }
    
    /**
     * Pass values accepted by this {@code Stream} into the given {@link Bus}, notifying with the given key.
     *
     * @param source the {@link Publisher} to consume
     * @param keyMapper  the key function mapping each incoming data to a key to notify on
     * @return {@literal new Stream}
     * @since 3.2.2
     */
    public final <T> EventBus notify(@NonNull final Publisher<? extends T> source, @NonNull final Function<? super T, ?> keyMapper) {
        Flux.from(source).doOnNext(t -> notify(keyMapper.apply(t), Event.wrap(t))).subscribe();
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
        return notify(key, supplier.get());
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
        return notify(key, new Event<>(Void.class));
    }


    /**
     * Assign a {@link Function} to receive an {@link Event} and produce a reply of the given type.
     *
     * @param sel The {@link Selector} to be used for matching
     * @param fn  The transformative {@link Function} to call to receive an {@link Event}
     * @return A {@link Registration} object that allows the caller to interact with the given mapping
     */
    public <T extends Event<?>, V> Registration<?, Consumer<? extends Event<?>>> receive(Selector sel, Function<T, V> fn) {
        return on(sel, new ReplyToConsumer<>(fn));
    }


    /**
     * Notify this component of the given {@link Event} and register an internal {@link Consumer} that will take the
     * output of a previously-registered {@link Function} and respond using the key set on the {@link Event}'s {@literal
     * replyTo} property.
     *
     * @param key The key to be matched by {@link Selector Selectors}
     * @param ev  The {@literal Event}
     * @return {@literal this}
     */
    public EventBus send(Object key, Event<?> ev) {
        return notify(key, new ReplyToEvent(ev));
    }


    /**
     * Notify this component that the given {@link Supplier} will provide an {@link Event} and register an internal
     * {@link
     * Consumer} that will take the output of a previously-registered {@link Function} and respond using the key set on
     * the {@link Event}'s {@literal replyTo} property.
     *
     * @param key      The key to be matched by {@link Selector Selectors}
     * @param supplier The {@link Supplier} that will provide the actual {@link Event} instance
     * @return {@literal this}
     */
    public EventBus send(Object key, Supplier<? extends Event<?>> supplier) {
        return notify(key, new ReplyToEvent(supplier.get()));
    }


    /**
     * Register the given {@link Consumer} on an anonymous {@link Selector} and
     * set the given event's {@code replyTo} property to the corresponding anonymous key,
     * then register the consumer to receive replies from the {@link Function} assigned to
     * handle the given key.
     *
     * @param key   The key to be matched by {@link Selector Selectors}
     * @param event The event to notify.
     * @param reply The consumer to register as a reply handler.
     * @return {@literal this}
     */
    public <T extends Event<?>> EventBus sendAndReceive(Object key, Event<?> event, Consumer<T> reply) {
        Selector sel = Selectors.anonymous();
        on(sel, reply).cancelAfterUse();
        return notify(key, event.setReplyTo(sel.getObject()));
    }

    /**
     * Register the given {@link Consumer} on an anonymous {@link Selector} and
     * set the event's {@code replyTo} property to the corresponding anonymous key,
     * then register the consumer to receive replies from the {@link Function} assigned to
     * handle the given key.
     *
     * @param key      The key to be matched by {@link Selector Selectors}
     * @param supplier The supplier to supply the event.
     * @param reply    The consumer to register as a reply handler.
     * @return {@literal this}
     */
    public <T extends Event<?>> EventBus sendAndReceive(Object key, Supplier<? extends Event<?>> supplier, Consumer<T> reply) {
        return this.sendAndReceive(key, supplier.get(), reply);
    }


    @Override
    public void accept(Event ev) {
        notify(ev.getKey(), ev);
    }


    public static class ReplyToEvent<T> extends Event<T> {

        private ReplyToEvent(Headers headers, T data, Object replyTo) {
            super(headers, data);
            setReplyTo(replyTo);
        }

        private ReplyToEvent(Event<T> delegate) {
            this(delegate.getHeaders(), delegate.getData(), delegate.getReplyTo());
        }

        @Override
        public <X> Event<X> copy(X data) {
            return new ReplyToEvent<>(getHeaders(), data, getReplyTo());
        }
    }

    public class ReplyToConsumer<E extends Event<?>, V> implements Consumer<E> {
        private final Function<E, V>                        fn;

        private ReplyToConsumer(Function<E, V> fn) {
            this.fn = fn;
        }

        @Override
        public void accept(E ev) {
            try {
                V reply = fn.apply(ev);
                Event<?> replyEv = (reply == null) ?
                        new Event<>(Void.class) :
                        (Event.class.isAssignableFrom(reply.getClass()) ? ((Event<?>) reply).copy() : Event.wrap(reply));
                EventBus.this.notify(ev.getReplyTo(), replyEv);
            } catch (Throwable t) {
                EventBus.this.notify(t.getClass(), Event.wrap(t));
            }
        }
    }
}

package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.env.Environment;
import cn.gitlab.virtualcry.reactor.bus.registry.Registration;
import cn.gitlab.virtualcry.reactor.bus.selector.Selector;
import cn.gitlab.virtualcry.reactor.bus.support.PayloadConsumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A reactor is an event gateway that allows other components to register {@link Event} {@link PayloadConsumer}s
 * that can subsequently be notified of events.
 *
 * @author VirtualCry
 */
public class EventBus implements Bus<Event<?>>, Consumer<Event<?>> {

    private final BusProcessor processor;


    private EventBus(@NonNull Environment env) {
        this.processor = new EventBusProcessor(env);
    }

    private EventBus(@NonNull BusProcessor processor) {
        this.processor = processor;
    }


    /**
     * Create a new {@link EventBus} using the given {@link Environment}
     *
     * @param env The {@link Environment} to use.
     * @return  A new {@link EventBus}
     */
    public static EventBus create(final Environment env) {
        return new EventBus(env);
    }

    /**
     * Create a new {@link EventBus} using the given {@link BusProcessor}
     *
     * @param processor The {@link BusProcessor} to use.
     * @return  A new {@link EventBus}
     * @since 3.2.2
     */
    public static EventBus create(final BusProcessor processor) {
        return new EventBus(processor);
    }



    @Override
    public boolean respondsToKey(final Object key) {
        return this.processor.getRegistry().select(key).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Registration<Object, PayloadConsumer<V>> on(Selector selector, PayloadConsumer<V> consumer) {
        return this.processor.getRegistry().register(selector, consumer);
    }

    @Override
    public Bus<Event<?>> notify(@NonNull final Object key,
                                @NonNull final Event<?> ev) {
        ev.setKey(key);
        this.processor.onNext(ev);
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

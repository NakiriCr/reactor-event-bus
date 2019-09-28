package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.env.Environment;
import cn.gitlab.virtualcry.reactor.bus.support.EventProcessor;
import cn.gitlab.virtualcry.reactor.bus.support.EventProcessorCore;
import cn.gitlab.virtualcry.reactor.bus.support.EventSubscriber;
import cn.gitlab.virtualcry.reactor.bus.support.StickyEventHolder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * A reactor is an event gateway that allows other components to register {@link Event} {@link EventSubscriber}s
 * that can subsequently be notified of events.
 *
 * @author VirtualCry
 */
public class EventBus implements Bus {

    private final EventProcessor                        processor;


    private EventBus(Environment env) {
        this.processor = new EventProcessorCore(env);
    }

    private EventBus(EventProcessor processor) {
        this.processor = processor;
    }


    @Override
    public <T extends Event> Bus on(Class<T> eventType, EventSubscriber<T> subscriber) {
        this.processor.subscribe(eventType, subscriber);
        if (StickyEvent.class.isAssignableFrom(eventType))
            Optional.ofNullable(StickyEventHolder.getSingleton().get(eventType))   // republish if it's sticky event subscriber.
                    .ifPresent(this::post);
        return this;
    }

    @Override
    public <T extends Event> Bus on(Class<T> eventType, List<EventSubscriber<T>> subscribers) {
        subscribers.forEach(subscriber -> this.on(eventType, subscriber));
        return this;
    }

    @Override
    public Bus cancel(Class<? extends Event> eventType, String subscriberID) {
        this.processor.cancel(eventType, subscriberID);
        return this;
    }

    @Override
    public Bus cancel(Class<? extends Event> eventType, Collection<String> subscriberIDs) {
        subscriberIDs.forEach(subscriberID -> this.cancel(eventType, subscriberID));
        return this;
    }

    @Override
    public <T extends Event> Bus post(T event) {
        this.processor.onNext(event);
        return this;
    }

    @Override
    public <T extends StickyEvent> Bus postSticky(T event) {
        this.post(event);
        StickyEventHolder.getSingleton().put(event.getClass(), event);
        return this;
    }


    /**
     * Create a new {@link EventBus} using the given {@link Environment}
     *
     * @param env The {@link Environment} to use.
     * @return  A new {@link EventBus}
     */
    public static EventBus create(Environment env) {
        return new EventBus(env);
    }

    /**
     * Create a new {@link EventBus} using the given {@link Environment}
     *
     * @param processor The {@link EventProcessor} to use.
     * @return  A new {@link EventBus}
     * @since 3.2.2
     */
    public static EventBus create(EventProcessor processor) {
        return new EventBus(processor);
    }
}

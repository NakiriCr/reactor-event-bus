package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.subscriber.EventSubscriber;
import cn.gitlab.virtualcry.reactor.bus.subscriber.SubscriberID;

import java.util.List;

/**
 * Basic unit of event handling in Reactor.
 *
 * @author VirtualCry
 */
public interface Bus {

    /**
     * Register an {@link EventSubscriber} to be triggered when a notification matches the given {@link
     * Event}.
     *
     * @param eventType The {@literal Class<Event>} to be used for matching
     * @param subscriber The {@literal EventSubscriber} to be triggered
     * @return {@literal this}
     */
    <T extends Event> Bus on(Class<T> eventType, EventSubscriber<T> subscriber);


    /**
     * Register an {@link EventSubscriber} to be triggered when a notification matches the given {@link
     * Event}.
     *
     * @param eventType The {@literal Class<Event>} to be used for matching
     * @param subscribers The {@literal EventSubscriber}s to be triggered
     * @return {@literal this}
     */
    <T extends Event> Bus on(Class<T> eventType, List<EventSubscriber<T>> subscribers);


    /**
     * UnSubscribe an event stream with the given {@link SubscriberID}
     *
     * @param eventType The {@literal Class<Event>} to be used for matching
     * @param subscriberID  The {@literal SubscriberID} to be used for matching
     * @return {@literal this}
     */
    Bus unSubscribe(Class<? extends Event> eventType, SubscriberID subscriberID);


    /**
     * Publish an {@link Event} to {@link org.reactivestreams.Processor}
     *
     * @param event  The {@literal Event} to be processed
     * @return {@literal this}
     */
    <T extends Event> Bus publish(T event);

}

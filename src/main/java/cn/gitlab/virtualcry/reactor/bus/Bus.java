package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.support.EventSubscriber;

import java.util.Collection;
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
     * Cancel subscription by the {@literal SubscriberID}.
     *
     * @param eventType The {@literal Class<Event>} to be used for matching
     * @param subscriberID  The {@literal SubscriberID} to be used for matching
     * @return {@literal this}
     * @since 3.2.2
     */
    Bus cancel(Class<? extends Event> eventType, String subscriberID);


    /**
     * Cancel subscription by the {@literal SubscriberID}s.
     *
     * @param eventType The {@literal Class<Event>} to be used for matching
     * @param subscriberIDs  The {@literal SubscriberID} to be used for matching
     * @return {@literal this}
     * @since 3.2.2
     */
    Bus cancel(Class<? extends Event> eventType, Collection<String> subscriberIDs);


    /**
     * Publish an {@link Event} to {@link org.reactivestreams.Processor}
     *
     * @param event  The {@literal Event} to be processed
     * @return {@literal this}
     * @since 3.2.2
     */
    <T extends Event> Bus post(T event);


    /**
     * Publish an {@link StickyEvent} to {@link org.reactivestreams.Processor}
     *
     * @param event  The {@literal Event} to be processed
     * @return {@literal this}
     * @since 3.2.2
     */
    <T extends StickyEvent> Bus postSticky(T event);
}

package cn.gitlab.virtualcry.reactor.bus.support;

import cn.gitlab.virtualcry.reactor.bus.Event;
import org.reactivestreams.Subscription;
import reactor.core.publisher.FluxProcessor;

import java.util.Collection;

/**
 * Processing Center for event publishing and subscription.
 * <p></p>
 * This stream will never emit a {@link org.reactivestreams.Subscriber#onComplete()}.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public interface EventProcessor {

    /**
     * Using {@literal eventType} to get {@link EventSubscriber<T>}s
     * @param eventType The {@literal eventType} to be used for matching..
     * @param <T> Type of {@link Event}
     * @return An collection of {@link EventSubscriber<T>}.
     */
    <T extends Event> Collection<EventSubscriber<T>> getSubscribers(Class<T> eventType);


    /**
     * Using {@link EventSubscriber} to consume {@link Event}s from {@link FluxProcessor} stream.
     *
     * @param eventType The {@literal eventType} to be used for matching..
     * @param subscriber The {@literal subscriber} to be used for consuming events.
     */
    <T extends Event> void subscribe(Class<T> eventType, EventSubscriber<T> subscriber);


    /**
     * Using {@link EventSubscriber} to consume {@link Event}s from {@link FluxProcessor} stream.
     *
     * @param eventType The {@literal eventType} to be used for matching..
     * @param subscribers The {@literal subscribers} to be used for consuming events.
     * @see #subscribe(Class, EventSubscriber) .
     */
    <T extends Event> void subscribe(Class<T> eventType, Collection<EventSubscriber<T>> subscribers);


    /**
     * Cancel subscription by the {@literal SubscriberID}.
     *
     * @param eventType The {@literal eventType} to be used for matching..
     * @param subscriberID The {@literal subscriberID} to be used for matching.
     */
    <T extends Event> void cancel(Class<T> eventType, String subscriberID);


    /**
     * Cancel subscription by the {@literal SubscriberIDs}.
     *
     * @param eventType The {@literal eventType} to be used for matching..
     * @param subscriberIDs The {@literal subscriberIDs} to be used for matching.
     */
    <T extends Event> void cancel(Class<T> eventType, Collection<String> subscriberIDs);


    /**
     * Event sent by the {@link FluxProcessor} in response to requests to {@link Subscription#request(long)}.
     *
     * @param event The {@literal subscriberIDs} to be used for element signaled
     */
    void onNext(Event event);


    /**
     * Successful terminal state.
     * <p></p>
     * No further events will be sent even if {@link Subscription#request(long)} is invoked again.
     */
    void onComplete();


    /**
     * Cancel event stream subscription.
     *
     */
    void onCancel();
}

package cn.gitlab.virtualcry.reactor.bus.support;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.env.Environment;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.FluxProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Core of {@link EventProcessor}
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public class EventProcessorCore implements EventProcessor {
    private final Logger                                logger;

    private final Environment                           env;
    private final FluxProcessor<Event, Event>           eventReceiver;
    private final Scheduler                             subscriberScheduler;
    private final Disposable                            disposable;
    private final EventSubscriberHolder                 subscriberHolder;


    public EventProcessorCore(Environment env) {
        this.env = env;
        this.logger = Loggers.getLogger(this.getClass());
        this.eventReceiver = this.env.eventReceiverConfig().create();
        this.subscriberScheduler = this.env.eventSubscriberConfig().create();
        this.disposable = this.initialize();
        this.subscriberHolder = EventSubscriberHolder.getSingleton();
    }

    /**
     *  Subscribe to event streams and define event-related processing.
     *
     * @return a new {@link Disposable} that can be used to cancel the underlying {@link Subscription}
     */
    private Disposable initialize() {
        return this.eventReceiver
                .doOnNext(event -> this.handleEvent(event, subscriber ->
                        subscriber.getEventConsumer().accept(
                                this.env.forceImmutableEvent() ? (Event) event.clone() : event
                        )
                ))
                .doOnSubscribe(subscription -> {
                    if (logger.isDebugEnabled())
                        logger.debug("Subscribed on Event Bus.");
                })
                .doOnComplete(() -> {
                    if (logger.isDebugEnabled())
                        logger.debug("Completed on Event Bus.");
                })
                .doOnCancel(() -> {
                    if (logger.isDebugEnabled())
                        logger.debug("Canceled on Event Bus.");
                })
                .subscribe();
    }

    /**
     * Handle event.
     * @param event The {@literal eventType} to be used for matching event subscribers.
     * @param subscriberConsumer The {@literal subscriberConsumer} to be used for consuming event.
     * @param <T> Type of {@link Event}
     */
    @SuppressWarnings("unchecked")
    private <T extends Event> void handleEvent(T event, Consumer<EventSubscriber<T>> subscriberConsumer) {
        this.getSubscribers((Class<T>) event.getClass())
                .stream().sorted()
                .peek(subscriber -> {
                    if (this.logger.isDebugEnabled())
                        this.logger.debug("Delivery event"
                                + " { " + event.getClass().getSimpleName() + ": " + event.getEventId() + " }"
                                + " to subscriber { " + subscriber.getSubscriberID() + " }");
                })
                .forEach(subscriber -> subscriberScheduler.schedule(() -> {
                    try { subscriberConsumer.accept(subscriber); }
                    catch (Exception ex) { logger.error("Error in handling event"
                            + " { " + event.getClass().getSimpleName() + ": " + event.getEventId() +" }.", ex); }
                }));
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event> Collection<EventSubscriber<T>> getSubscribers(Class<T> eventType) {
        return Collections.unmodifiableCollection(
                this.subscriberHolder.getOrDefault(eventType, new HashSet<>()).stream()
                        .map(eventSubscriber -> (EventSubscriber<T>) eventSubscriber)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public final <T extends Event> void subscribe(Class<T> eventType, EventSubscriber<T> subscriber) {
        if (logger.isDebugEnabled())
            logger.debug("Subscriber { " + subscriber.getSubscriberID() + " } subscribed.");
        this.subscriberHolder.computeIfAbsent(eventType, aClass -> new HashSet<>()).add(subscriber);
    }

    @Override
    public final <T extends Event> void subscribe(Class<T> eventType, Collection<EventSubscriber<T>> subscribers) {
        subscribers.forEach(subscriber -> this.subscribe(eventType, subscriber));
    }

    @Override
    public final <T extends Event> void cancel(Class<T> eventType, String subscriberID) {
        if (logger.isDebugEnabled())
            logger.debug("Subscriber { " + subscriberID + " } unsubscribed.");
        this.subscriberHolder.getOrDefault(eventType, new HashSet<>())
                .removeIf(subscriber -> subscriberID.equals(subscriber.getSubscriberID()));
    }

    @Override
    public  <T extends Event> void cancel(Class<T> eventType, Collection<String> subscriberIDs) {
        subscriberIDs.forEach(subscriberID -> this.cancel(eventType, subscriberID));
    }

    @Override
    public void onNext(Event event) {
        if (this.logger.isDebugEnabled())
            this.logger.debug("Event { " + event.getClass().getSimpleName() + ": " + event.getEventId()
                    + " } has been published.");
        this.eventReceiver.onNext(event);
    }

    @Override
    public void onComplete() {
        this.eventReceiver.onComplete();
    }

    @Override
    public void onCancel() {
        this.disposable.dispose();
    }
}

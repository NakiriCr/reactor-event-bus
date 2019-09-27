package cn.gitlab.virtualcry.reactor.bus.processor;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.env.Environment;
import cn.gitlab.virtualcry.reactor.bus.subscriber.EventSubscriber;
import cn.gitlab.virtualcry.reactor.bus.subscriber.SubscriberID;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.FluxProcessor;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Emit signals whenever an Event arrives from the {@link EventRouter} topic from the {@link
 * EventRecorder} from the {@link cn.gitlab.virtualcry.reactor.bus.Bus}.
 *
 * This stream will never emit a {@link org.reactivestreams.Subscriber#onComplete()}.
 *
 * @author VirtualCry
 */
public final class EventProcessor<T extends Event> {
    private final Logger                                logger;

    private final Environment                           env;
    private final FluxProcessor<T, T>                   eventReceiver;
    private final Map<SubscriberID, Disposable>         subscriberDisposables;


    EventProcessor(Environment env) {
        this.env = env;
        this.logger = Loggers.getLogger(this.getClass());
        this.eventReceiver = this.env.eventReceiverConfig().create();
        this.subscriberDisposables = new ConcurrentHashMap<>();
    }


    /**
     * Using {@link EventSubscriber}s to consume {@link Event}s from {@link FluxProcessor} stream.
     *
     * @param subscribers subscribers
     *
     * @return a new {@link Disposable} that can be used to cancel the underlying {@link Subscription}
     */
    public final List<Disposable> subscribe(List<EventSubscriber<T>> subscribers) {
        return subscribers.stream()
                .map(this::subscribe)
                .collect(Collectors.toList());
    }


    /**
     * Using {@link EventSubscriber} to consume {@link Event}s from {@link FluxProcessor} stream.
     *
     * @param subscriber the subscriber
     *
     * @return a new {@link Disposable} that can be used to cancel the underlying {@link Subscription}
     */
    @SuppressWarnings("unchecked")
    public final Disposable subscribe(EventSubscriber<T> subscriber) {
        return subscriberDisposables.compute(subscriber.getId(), (subscriberID, disposable) -> {
            if (disposable != null) {
                throw new RuntimeException("Subscriber { " + subscriberID.getId()
                        + " } has been subscribed.");
            }
            else {
                // create eventReceiver to subscribe and consume event stream.
                FluxProcessor<T, T> eventSubscriber = this.env.eventSubscriberConfig().create();
                this.eventReceiver.subscribe(eventSubscriber::onNext);
                return eventSubscriber
                        .doOnNext(event -> {
                            if (logger.isDebugEnabled())
                                logger.debug("Subscriber { " + subscriberID.getId() + " } consumed event.");
                            subscriber.getEventConsumer().accept(
                                    this.env.forceImmutableEvent() ? (T) event.clone() : event
                            );
                        })
                        .onErrorContinue(Throwable.class, (throwable, event) -> {
                            logger.error("", throwable);
                            subscriber.getErrorConsumer().accept(throwable, (T) event);
                        })
                        .doOnSubscribe(subscription -> {
                            if (logger.isDebugEnabled())
                                logger.debug("Subscriber { " + subscriberID.getId() + " } subscribed.");
                            subscriber.getSubscriptionConsumer().accept(subscription);
                        })
                        .doOnComplete(() -> {
                            if (logger.isDebugEnabled())
                                logger.debug("Subscriber { " + subscriberID.getId() + " } onCompleted.");
                            subscriber.getCompleteConsumer().run();
                        })
                        .doOnCancel(() -> {
                            if (logger.isDebugEnabled())
                                logger.debug("Subscriber { " + subscriberID.getId() + " } onCanceled.");
                            subscriber.getCancelConsumer().run();
                        })
                        .subscribe();
            }
        });
    }


    /**
     * Event sent by the {@link FluxProcessor} in response to requests to {@link Subscription#request(long)}.
     *
     * @param event the element signaled
     */
    final void onNext(T event) {
        this.eventReceiver.onNext(event);
    }


    /**
     * Successful terminal state.
     * <p>
     * No further events will be sent even if {@link Subscription#request(long)} is invoked again.
     */
    public final void onComplete() {
        this.eventReceiver.onComplete();
    }


    /**
     * Cancel subscribe the event stream by the {@literal SubscriberId}
     *
     * @param subscriberID the subscriber id
     */
    public final void onCancel(SubscriberID subscriberID) {
        Disposable disposable = this.subscriberDisposables.get(subscriberID);
        if (disposable == null) {
            logger.warn("Fail to dispose cause could not find Subscriber: { " + subscriberID.getId() + " }");
        }
        else {
            disposable.dispose();
            this.subscriberDisposables.remove(subscriberID);
        }
    }

}

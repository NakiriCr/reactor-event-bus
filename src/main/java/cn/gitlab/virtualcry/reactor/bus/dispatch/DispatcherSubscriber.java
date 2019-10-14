package cn.gitlab.virtualcry.reactor.bus.dispatch;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Subscribe to the dispatcher and make callback processing
 * when doing the corresponding operation.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public interface DispatcherSubscriber<T> {

    /**
     * Add behavior (side-effect) triggered when the {@link Publisher} emits an item.
     *
     * @param t The callback to call on {@link Subscriber#onNext}
     */
    void doOnNext(T t);


    /**
     * Add behavior (side-effect) triggered when the {@link Publisher} completes with an error.
     *
     * @param t The callback to call on {@link Subscriber#onError}
     */
    void doOnError(Throwable t);


    /**
     * Add behavior (side-effect) triggered when the {@link Publisher} is subscribed.
     *
     * @param s The callback to call on {@link Subscriber#onSubscribe}
     */
    void doOnSubscribe(Subscription s);


    /**
     * Add behavior (side-effect) triggered when the {@link Publisher} completes successfully.
     */
    void doOnComplete();


    /**
     * Add behavior (side-effect) triggered when the {@link Publisher} is cancelled.
     */
    void doOnCancel();
}

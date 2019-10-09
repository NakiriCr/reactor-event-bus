package cn.gitlab.virtualcry.reactor.bus.support;

import org.reactivestreams.Subscription;

/**
 * Somethings
 *
 * @author VirtualCry
 */
public interface DispatcherSubscriber<T> {

    void doOnNext(T t);

    void doOnError(Throwable t);

    void doOnSubscribe(Subscription s);

    void doOnComplete();

    void doOnCancel();
}

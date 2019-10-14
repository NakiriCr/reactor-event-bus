package cn.gitlab.virtualcry.reactor.bus.dispatch;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

/**
 * Emit signals whenever an Event arrives from the {@link cn.gitlab.virtualcry.reactor.bus.selector.Selector}
 * topic from the {@link cn.gitlab.virtualcry.reactor.bus.Bus}.
 * This stream will never emit a {@link org.reactivestreams.Subscriber#onComplete()}.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public interface Dispatcher<E> {

    /**
     * Data notification sent by the {@link Publisher} in response to requests to {@link Subscription#request(long)}.
     *
     * @param ev the element signaled
     */
    void onNext(E ev);


    /**
     * Successful terminal state.
     * <p>
     * No further events will be sent even if {@link Subscription#request(long)} is invoked again.
     */
    void onComplete();


    /**
     * Cancel event stream subscription.
     * <p>
     * No further events will be sent even if {@link Subscription#request(long)} is invoked again.
     */
    void onCancel();
}

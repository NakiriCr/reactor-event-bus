package cn.gitlab.virtualcry.reactor.bus.support;

import org.reactivestreams.Subscription;
import reactor.core.publisher.FluxProcessor;

/**
 * Emit signals whenever an Event arrives from the {@link cn.gitlab.virtualcry.reactor.bus.Bus}.
 * This stream will never emit a {@link org.reactivestreams.Subscriber#onComplete()}.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public interface Dispatcher<E> {

    /**
     * Event sent by the {@link FluxProcessor} in response to requests to {@link Subscription#request(long)}.
     *
     * @param ev The {@literal subscriberIDs} to be used for element signaled
     */
    void onNext(E ev);


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

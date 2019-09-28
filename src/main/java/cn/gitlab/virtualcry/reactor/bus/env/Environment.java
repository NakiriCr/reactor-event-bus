package cn.gitlab.virtualcry.reactor.bus.env;

import cn.gitlab.virtualcry.reactor.bus.spec.receiver.EventReceiverComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.subscriber.EventSubscriberComponentSpec;

/**
 * Environment use in {@link cn.gitlab.virtualcry.reactor.bus.Bus} to config {@link
 * cn.gitlab.virtualcry.reactor.bus.support.EventProcessor}
 *
 * @author VirtualCry
 */
public interface Environment {

    Environment ASYNCHRONOUS = new AsynchronousEnvironment();
    Environment SYNCHRONOUS  = new SynchronousEnvironment();


    /**
     * Force events to be immutable, event will be published after serialize and deserialize,
     * and it must cost a certain amount of performance.
     *
     * @return  {@literal default false}
     */
    default Boolean forceImmutableEvent() {
        return false;
    }


    /**
     * Create a event receiver config .
     *
     * @return  A new processor config {@link EventReceiverComponentSpec} uses in event receiver.
     */
    EventReceiverComponentSpec eventReceiverConfig();


    /**
     * Create a event subscriber config .
     *
     * @return   A new processor config {@link EventReceiverComponentSpec} uses in event subscriber.
     */
    EventSubscriberComponentSpec eventSubscriberConfig();
}

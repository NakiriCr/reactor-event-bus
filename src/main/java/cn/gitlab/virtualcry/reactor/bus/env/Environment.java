package cn.gitlab.virtualcry.reactor.bus.env;

import cn.gitlab.virtualcry.reactor.bus.spec.EventProcessorComponentSpec;

/**
 * Environment use in {@link cn.gitlab.virtualcry.reactor.bus.Bus} to config {@link
 * cn.gitlab.virtualcry.reactor.bus.processor.EventProcessor}
 *
 * @author VirtualCry
 */
public interface Environment {

    Environment ASYNCHRONOUS  = new AsynchronousEnvironment();
    Environment SYNCHRONOUS = new SynchronousEnvironment();


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
     * @return  A new processor config {@link EventProcessorComponentSpec} uses in event receiver.
     */
    EventProcessorComponentSpec eventReceiverConfig();


    /**
     * Create a event subscriber config .
     *
     * @return   A new processor config {@link EventProcessorComponentSpec} uses in event subscriber.
     */
    EventProcessorComponentSpec eventSubscriberConfig();

}

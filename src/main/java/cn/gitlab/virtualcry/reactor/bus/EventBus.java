package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.env.Environment;
import cn.gitlab.virtualcry.reactor.bus.processor.EventRouter;
import cn.gitlab.virtualcry.reactor.bus.subscriber.EventSubscriber;
import cn.gitlab.virtualcry.reactor.bus.subscriber.SubscriberID;

import java.util.List;

/**
 * A reactor is an event gateway that allows other components to register {@link Event} {@link EventSubscriber}s
 * that can subsequently be notified of events.
 *
 * @author VirtualCry
 */
public class EventBus implements Bus {

    private final EventRouter eventRouter;


    private EventBus(Environment env) {
        this.eventRouter = new EventRouter(env);
    }


    @Override
    public <T extends Event> Bus on(Class<T> eventType, EventSubscriber<T> subscriber) {
        this.eventRouter.route(eventType)
                .subscribe(subscriber);
        return this;
    }

    @Override
    public <T extends Event> Bus on(Class<T> eventType, List<EventSubscriber<T>> subscribers) {
        this.eventRouter.route(eventType)
                .subscribe(subscribers);
        return this;
    }

    @Override
    public Bus unSubscribe(Class<? extends Event> eventType, SubscriberID subscriberID) {
        this.eventRouter.route(eventType)
                .onCancel(subscriberID);
        return this;
    }

    @Override
    public <T extends Event> Bus publish(T event) {
        this.eventRouter.publish(event);
        return this;
    }


    /**
     * Create a new {@link EventBus} using the given {@link Environment}
     *
     * @param env The {@link Environment} to use.
     * @return  A new {@link EventBus}
     */
    public static EventBus create(Environment env) {
        return new EventBus(env);
    }

}

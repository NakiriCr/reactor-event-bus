package cn.gitlab.virtualcry.reactor.bus.support;

import cn.gitlab.virtualcry.reactor.bus.Event;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Somethings
 *
 * @author VirtualCry
 * @since 3.2.2
 */
final class EventSubscriberHolder extends
        ConcurrentHashMap<Class<? extends Event>, Set<EventSubscriber>> {

    private static class EventSubscriberHolderInstance {
        private static final EventSubscriberHolder INSTANCE = new EventSubscriberHolder();
    }

    private EventSubscriberHolder() { }

    static EventSubscriberHolder getSingleton() {  // singleton
        return EventSubscriberHolder.EventSubscriberHolderInstance.INSTANCE;
    }
}

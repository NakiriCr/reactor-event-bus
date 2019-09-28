package cn.gitlab.virtualcry.reactor.bus.support;

import cn.gitlab.virtualcry.reactor.bus.Event;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Subscribe an event stream.
 *
 * @author VirtualCry
 */
@Getter
@Builder
public final class EventSubscriber<T extends Event> implements Comparable<EventSubscriber<T>>, Serializable {

    private final String                            subscriberID;
    private final Integer                           priority;
    private final EventConsumer<? super T>          eventConsumer;

    public EventSubscriber(
            String subscriberID,
            Integer priority,
            EventConsumer<? super T> eventConsumer) {
        this.subscriberID = (Objects.isNull(subscriberID)) ? UUID.randomUUID().toString() : subscriberID;
        this.priority = (Objects.isNull(priority)) ? 0 : priority;
        this.eventConsumer = (Objects.isNull(eventConsumer)) ? t -> { } : eventConsumer;
    }

    @Override
    public int compareTo(EventSubscriber<T> subscriber) {
        return subscriber.getPriority() - this.getPriority();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EventSubscriber<?> that = (EventSubscriber<?>) object;
        return subscriberID.equals(that.subscriberID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriberID);
    }
}

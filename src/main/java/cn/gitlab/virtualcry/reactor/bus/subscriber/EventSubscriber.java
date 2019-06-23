package cn.gitlab.virtualcry.reactor.bus.subscriber;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.support.EventConsumer;
import lombok.Builder;
import lombok.Getter;
import org.reactivestreams.Subscription;
import reactor.util.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Subscribe an event stream.
 *
 * @author VirtualCry
 */
@Getter
@Builder
public final class EventSubscriber<T extends Event> implements Serializable {

    private final SubscriberID                      id;
    private final EventConsumer<? super T>          eventConsumer;
    private final BiConsumer<? super Throwable, ? super T> errorConsumer;
    private final Runnable                          completeConsumer;
    private final Consumer<? super Subscription>    subscriptionConsumer;

    public EventSubscriber(
            @Nullable SubscriberID subscriberID,
            @Nullable EventConsumer<? super T> eventConsumer,
            @Nullable BiConsumer<? super Throwable, ? super T> errorConsumer,
            @Nullable Runnable completeConsumer,
            @Nullable Consumer<? super Subscription> subscriptionConsumer) {
        this.id = (Objects.isNull(subscriberID)) ? SubscriberID.create() : subscriberID;
        this.eventConsumer = (Objects.isNull(eventConsumer)) ? t -> { } : eventConsumer;
        this.errorConsumer = (Objects.isNull(errorConsumer)) ? (throwable, t) -> { } : errorConsumer;
        this.completeConsumer = completeConsumer;
        this.subscriptionConsumer = subscriptionConsumer;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EventSubscriber<?> that = (EventSubscriber<?>) object;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

package cn.gitlab.virtualcry.reactor.bus.support;

import cn.gitlab.virtualcry.reactor.bus.Event;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Simple {@link Consumer} implementation that pulls the data from an {@link Event} and
 * passes it to a delegate {@link Consumer}.
 *
 * @param <T> the type of the event that can be handled by the consumer and the type that
 *            can be handled by the delegate
 *
 * @author VirtualCry
 * @since 3.2.2
 */
@Getter
@Builder
public class PayloadConsumer<T> implements Consumer<T>, Comparable<PayloadConsumer<T>>, Serializable {

    private final String                            id;
    private final Integer                           priority;
    private final Consumer<? super T>               delegate;


    public PayloadConsumer(
            String id,
            Integer priority,
            Consumer<? super T> delegate) {
        this.id = (Objects.isNull(id)) ? UUID.randomUUID().toString() : id;
        this.priority = (Objects.isNull(priority)) ? 0 : priority;
        this.delegate = (Objects.isNull(delegate)) ? t -> { } : delegate;
    }


    @Override
    public void accept(T event) {
        this.delegate.accept(event);
    }

    @Override
    public int compareTo(PayloadConsumer<T> consumer) {
        return consumer.priority - this.priority;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PayloadConsumer<?> that = (PayloadConsumer<?>) object;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

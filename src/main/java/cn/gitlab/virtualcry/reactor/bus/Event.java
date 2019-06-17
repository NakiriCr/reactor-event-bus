package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.support.EventConsumer;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import static cn.gitlab.virtualcry.reactor.bus.util.SerializationUtils.deserialize;
import static cn.gitlab.virtualcry.reactor.bus.util.SerializationUtils.serialize;

/**
 * Event will be processed by {@link EventConsumer}s.
 *
 * @author VirtualCry
 */
@Getter
public abstract class Event implements Serializable, Cloneable {

    private final String                            eventId
            = UUID.randomUUID().toString();
    private final Date                              eventCreationDate
            = new Date();

    @Override
    public Object clone() {
        return deserialize(serialize(this));
    }

}

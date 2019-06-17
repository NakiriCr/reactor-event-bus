package cn.gitlab.virtualcry.reactor.bus.subscriber;

import java.io.Serializable;
import java.util.UUID;

/**
 * Unique key for each {@link EventSubscriber}
 *
 * @author VirtualCry
 */
public class SubscriberID implements Serializable {

    private final String id;

    private SubscriberID(String id) {
        this.id = id;
    }

    public final String getId() {
        return id;
    }

    public static SubscriberID create() {
        return new SubscriberID(UUID.randomUUID().toString());
    }

    public static SubscriberID create(String id) {
        return new SubscriberID(id);
    }

}

package cn.gitlab.virtualcry.reactor.bus.routing;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.registry.Registration;
import org.checkerframework.checker.nullness.qual.NonNull;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Jon Brisbin
 * @author VirtualCry
 */
public class TraceableDelegatingRouter implements Router {

    private final Logger                                logger;
    private final Router                                delegate;

    public TraceableDelegatingRouter(@NonNull Router delegate) {
        this.logger = Loggers.getLogger(this.getClass());

        Objects.requireNonNull(delegate, "Delegate Router cannot be null.");
        this.delegate = delegate;
    }


    @Override
    public <E extends Event<?>> void route(Object key, E event,
                                           List<Registration<Object, ? extends Consumer<? extends Event<?>>>> registrations,
                                           Consumer<Throwable> errorConsumer) {
        if(logger.isTraceEnabled())
            logger.trace("route({}, {}, {}, {})", key, event, registrations, errorConsumer);
        delegate.route(key, event, registrations, errorConsumer);
    }
}

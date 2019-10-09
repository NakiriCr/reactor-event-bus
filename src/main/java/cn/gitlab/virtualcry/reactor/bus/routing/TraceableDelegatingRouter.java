package cn.gitlab.virtualcry.reactor.bus.routing;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.registry.Registration;
import cn.gitlab.virtualcry.reactor.bus.support.PayloadConsumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author VirtualCry
 */
public class TraceableDelegatingRouter implements Router {

    private final Logger                                logger;
    private final Router                                delegate;

    public TraceableDelegatingRouter(@NonNull Router delegate) {
        this.logger = Loggers.getLogger(this.getClass());
        this.delegate = delegate;
    }


    @Override
    public <E extends Event<V>, V> void route(Object key, E event,
                                              List<Registration<Object, ? extends PayloadConsumer<? super V>>> consumers,
                                              Consumer<Throwable> errorConsumer) {
        if(this.logger.isTraceEnabled())
            this.logger.trace("route({}, {}, {}, {})", key, event, consumers, errorConsumer);
        this.delegate.route(key, event, consumers, errorConsumer);
    }
}

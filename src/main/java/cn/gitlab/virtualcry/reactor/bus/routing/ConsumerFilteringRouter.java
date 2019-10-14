package cn.gitlab.virtualcry.reactor.bus.routing;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.filter.Filter;
import cn.gitlab.virtualcry.reactor.bus.registry.Registration;
import cn.gitlab.virtualcry.reactor.bus.support.Assert;
import lombok.Getter;
import reactor.core.scheduler.Scheduler;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.List;
import java.util.function.Consumer;

/**
 * An {@link Router} that {@link Filter#filter filters} consumers before routing events to
 * them.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
@Getter
public class ConsumerFilteringRouter implements Router {
    private final Logger                                logger;

    private final Filter                                filter;
    private final Scheduler                             routerScheduler;
    private final Scheduler                             consumerScheduler;


    public ConsumerFilteringRouter(Filter filter,
                                   Scheduler routerScheduler,
                                   Scheduler consumerScheduler) {
        this.logger = Loggers.getLogger(this.getClass());

        Assert.notNull(filter, "filter must not be null.");
        Assert.notNull(consumerScheduler, "Consumer Scheduler must not be null.");
        this.filter = filter;
        this.routerScheduler = routerScheduler;
        this.consumerScheduler = consumerScheduler;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <E extends Event<?>> void route(Object key, E ev,
                                              List<Registration<Object, ? extends Consumer<? extends Event<?>>>> registrations,
                                              Consumer<Throwable> errorConsumer) {
        routerScheduler.schedule(() -> filter.filter(registrations, key).stream()
                .filter(registration -> !registration.isCancelled())
                .peek(registration -> {
                    if (logger.isDebugEnabled())
                        logger.debug("Delivered event. - {}: {} → {}", ev.getKey(), ev.getId(), registration.getObject());
                })
                .forEach(registration -> consumerScheduler.schedule(() -> {
                    try {
                        ((Consumer<E>) registration.getObject()).accept(ev);
                    }
                    catch (Throwable t) {
                        if (null != errorConsumer)
                            errorConsumer.accept(t);
                        else
                            logger.error("Error in handling event. - " + ev.getKey() + ": " + ev.getId(), t);
                    }
                    finally {
                        if (registration.isCancelAfterUse())
                            registration.cancel();
                    }
                }))
        );
    }
}

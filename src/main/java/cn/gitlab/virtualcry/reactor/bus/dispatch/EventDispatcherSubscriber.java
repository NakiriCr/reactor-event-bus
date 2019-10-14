package cn.gitlab.virtualcry.reactor.bus.dispatch;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.routing.Router;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Subscription;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Implementation of {@link DispatcherSubscriber} that uses {@link Registry} and {@link Router}.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public class EventDispatcherSubscriber implements DispatcherSubscriber<Event<?>> {
    private final Logger                                logger;

    private final UUID                                  id;
    private final Registry<Object,
            Consumer<? extends Event<?>>>               consumerRegistry;
    private final Router                                router;
    private final Consumer<Throwable>                   dispatchErrorHandler;


    public EventDispatcherSubscriber(@NonNull Registry<Object, Consumer<? extends Event<?>>> consumerRegistry,
                                     @NonNull Router router,
                                     @Nullable Consumer<Throwable> dispatchErrorHandler) {
        this.logger = Loggers.getLogger(this.getClass());

        this.id = UUID.randomUUID();
        this.consumerRegistry = consumerRegistry;
        this.router = router;
        this.dispatchErrorHandler = dispatchErrorHandler;
    }


    @Override
    public void doOnNext(Event<?> ev) {
        if (logger.isDebugEnabled())
            logger.debug("Published event. - {}: {}", ev.getKey(), ev.getId());
        router.route(ev.getKey(), ev, consumerRegistry.select(ev.getKey()), dispatchErrorHandler);
    }

    @Override
    public void doOnError(Throwable t) {
        logger.error("Errored. - processor: " + id, t);
    }

    @Override
    public void doOnSubscribe(Subscription s) {
        if (logger.isDebugEnabled())
            logger.debug("Subscribed. - processor: {}", id);
    }

    @Override
    public void doOnComplete() {
        if (logger.isDebugEnabled())
            logger.debug("Completed. - processor: {}", id);
    }

    @Override
    public void doOnCancel() {
        if (logger.isDebugEnabled())
            logger.debug("Subscribed. - processor: {}", id);
    }
}

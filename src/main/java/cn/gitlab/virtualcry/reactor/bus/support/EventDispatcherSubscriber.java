package cn.gitlab.virtualcry.reactor.bus.support;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.routing.Router;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Subscription;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Somethings
 *
 * @author VirtualCry
 */
public class EventDispatcherSubscriber implements DispatcherSubscriber<Event<?>> {
    private final Logger                                logger;

    private final UUID                                  id;
    private final Registry<Object,
            PayloadConsumer<?>>                         consumerRegistry;
    private final Router                                router;
    private final Consumer<Throwable>                   dispatchErrorHandler;


    public EventDispatcherSubscriber(@NonNull Registry<Object, PayloadConsumer<?>> consumerRegistry,
                                     @NonNull Router router,
                                     @Nullable Consumer<Throwable> dispatchErrorHandler) {
        this.logger = Loggers.getLogger(this.getClass());

        this.id = UUID.randomUUID();
        this.consumerRegistry = consumerRegistry;
        this.router = router;
        this.dispatchErrorHandler = dispatchErrorHandler;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void doOnNext(Event<?> ev) {
        if (this.logger.isDebugEnabled())
            this.logger.debug("Published event. - {}: {}", ev.getKey(), ev.getId());
        this.router.route(
                ev.getKey(),
                ev,
                (List) this.consumerRegistry.select(ev.getKey()),
                dispatchErrorHandler
        );
    }

    @Override
    public void doOnError(Throwable t) {
        this.logger.error("Errored. - processor: " + this.id, t);
    }

    @Override
    public void doOnSubscribe(Subscription s) {
        if (this.logger.isDebugEnabled())
            this.logger.debug("Subscribed. - processor: {}", this.id);
    }

    @Override
    public void doOnComplete() {
        if (this.logger.isDebugEnabled())
            this.logger.debug("Completed. - processor: {}", this.id);
    }

    @Override
    public void doOnCancel() {
        if (this.logger.isDebugEnabled())
            this.logger.debug("Subscribed. - processor: {}", this.id);
    }
}

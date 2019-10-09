package cn.gitlab.virtualcry.reactor.bus.routing;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.filter.Filter;
import cn.gitlab.virtualcry.reactor.bus.registry.Registration;
import cn.gitlab.virtualcry.reactor.bus.support.PayloadConsumer;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
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
    private final Scheduler                             consumerScheduler;


    public ConsumerFilteringRouter(@NonNull Filter filter,
                                   @NonNull Scheduler consumerScheduler) {
        this.logger = Loggers.getLogger(this.getClass());

        this.filter = filter;
        this.consumerScheduler = consumerScheduler;
    }


    @Override
    public <E extends Event<V>, V> void route(Object key, E ev,
                                              List<Registration<Object, ? extends PayloadConsumer<? super V>>> consumers,
                                              Consumer<Throwable> errorConsumer) {
        this.filter.filter(consumers, key).stream()
                .map(Registration::getObject)
                .sorted()
                .peek(consumer -> {
                    if (this.logger.isDebugEnabled())
                        this.logger.debug("Delivered event. - {}: {} → {}", ev.getKey(), ev.getId(), consumer.getId());
                })
                .forEach(consumer -> consumerScheduler.schedule(() -> {
                    try {
                        consumer.accept(ev.getData());
                    }
                    catch (Throwable t) {
                        if (null != errorConsumer)
                            errorConsumer.accept(t);
                        else
                            this.logger.error("Error in handling event. - " + ev.getKey() + ": " + ev.getId(), t);
                    }
                }));
    }
}

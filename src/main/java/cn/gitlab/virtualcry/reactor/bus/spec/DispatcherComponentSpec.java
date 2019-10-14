package cn.gitlab.virtualcry.reactor.bus.spec;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.EventBus;
import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.routing.Router;
import cn.gitlab.virtualcry.reactor.bus.dispatch.Dispatcher;
import cn.gitlab.virtualcry.reactor.bus.dispatch.EventDispatcher;
import cn.gitlab.virtualcry.reactor.bus.support.loadBalance.LoadBalanceStrategy;
import reactor.core.publisher.FluxProcessor;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A generic environment-aware class for specifying components that need to be configured
 * with a {@link Dispatcher}.
 *
 * @param <SPEC>
 * 		The DispatcherComponentSpec subclass
 * @param <TARGET>
 * 		The type that this spec will create
 *
 * @author VirtualCry
 * @since 3.2.2
 */
@SuppressWarnings("unchecked")
public abstract class DispatcherComponentSpec<SPEC extends
        DispatcherComponentSpec<SPEC, TARGET>, TARGET> extends BuiltInDispatcherComponentSpec<SPEC, TARGET> implements Supplier<TARGET> {

    private List<FluxProcessor<Event<?>,
            Event<?>>>                              dispatchers;
    private LoadBalanceStrategy                     loadBalanceStrategy;


    /**
     * Configures the component to use the given {@code dispatchers}
     *
     * @param dispatchers The dispatchers to use
     *
     * @return {@code this}
     */
    public final SPEC dispatchers(List<FluxProcessor<Event<?>, Event<?>>> dispatchers) {
        this.dispatchers = dispatchers;
        return (SPEC) this;
    }


    /**
     * Configures the component to use the given {@code loadBalanceStrategy}.
     *
     * @param loadBalanceStrategy The strategy to use
     *
     * @return {@code this}
     */
    public final SPEC loadBalanceStrategy(LoadBalanceStrategy loadBalanceStrategy) {
        this.loadBalanceStrategy = loadBalanceStrategy;
        return (SPEC) this;
    }


    @Override
    public final TARGET get() {
        if (dispatchers != null && getDispatcherType() != null)
            throw new IllegalArgumentException("Because both the dispatcher type and the dispatcher instances are set, " +
                    "it is impossible to determine the type used for the dispatcher.");
        return configure(createReactor(
                dispatchers != null ? dispatchers : createBuiltInDispatchers(),
                loadBalanceStrategy != null ? loadBalanceStrategy : LoadBalanceStrategy.RANDOM
        ));
    }


    protected abstract TARGET configure(EventBus reactor);


    private EventBus createReactor(List<FluxProcessor<Event<?>, Event<?>>> dispatcherProcessors,
                                   LoadBalanceStrategy loadBalanceStrategy) {
        EventRoutingComponent routingComponent  = super.createEventRoutingComponent();
        Registry<Object, Consumer<? extends Event<?>>>
                consumerRegistry                 = routingComponent.getConsumerRegistry();
        Router router                            = routingComponent.getRouter();
        Consumer<Throwable> dispatchErrorHandler = routingComponent.getDispatchErrorHandler();
        List<Dispatcher<Event<?>>> dispatchers   = dispatcherProcessors.stream()
                .map(dispatcher -> new EventDispatcher(dispatcher, consumerRegistry, router, dispatchErrorHandler))
                .collect(Collectors.toList());
        return new EventBus(
                consumerRegistry,
                dispatchers,
                router,
                dispatchErrorHandler,
                loadBalanceStrategy
        );
    }
}

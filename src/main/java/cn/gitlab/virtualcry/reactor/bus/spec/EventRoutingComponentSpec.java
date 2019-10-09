package cn.gitlab.virtualcry.reactor.bus.spec;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.EventBus;
import cn.gitlab.virtualcry.reactor.bus.filter.*;
import cn.gitlab.virtualcry.reactor.bus.registry.Registration;
import cn.gitlab.virtualcry.reactor.bus.registry.Registries;
import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.routing.ConsumerFilteringRouter;
import cn.gitlab.virtualcry.reactor.bus.routing.Router;
import cn.gitlab.virtualcry.reactor.bus.routing.TraceableDelegatingRouter;
import cn.gitlab.virtualcry.reactor.bus.support.Dispatcher;
import cn.gitlab.virtualcry.reactor.bus.support.EventDispatcher;
import cn.gitlab.virtualcry.reactor.bus.support.PayloadConsumer;
import cn.gitlab.virtualcry.reactor.bus.util.loadBalance.LoadBalanceStrategy;
import reactor.core.publisher.FluxProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A generic environment-aware class for specifying components that need to be configured with a {@link FluxProcessor},
 * and {@link Router}.
 *
 * @param <SPEC>
 * 		The DispatcherComponentSpec subclass
 * @param <TARGET>
 * 		The type that this spec will create
 *
 * @author VirtualCry
 */
@SuppressWarnings("unchecked")
public abstract class EventRoutingComponentSpec<SPEC extends
		EventRoutingComponentSpec<SPEC, TARGET>, TARGET> extends DispatcherComponentSpec<SPEC, TARGET> {

	private EventRoutingStrategy  					eventRoutingStrategy;
	private Scheduler 								routerScheduler;
	private Router 									router;
	private Filter                					eventFilter;
	private Consumer<Throwable> 					dispatchErrorHandler;
	private Registry<Object, PayloadConsumer<?>>	consumerRegistry;
	private boolean 								traceEventPath = false;


	public static final Consumer<Registration<Object, ? extends PayloadConsumer<?>>> ON_REGISTER = registration -> {
		final Logger logger = Loggers.getLogger(Registry.class);
		if (logger.isDebugEnabled())
			logger.debug("Registered. - notify: {}, consumer: {}.",
					registration.getSelector().getObject(),
					registration.getObject().getId());
	};

	public static final Consumer<Registration<Object, ? extends PayloadConsumer<?>>> ON_UNREGISTER = registration -> {
		final Logger logger = Loggers.getLogger(Registry.class);
		if (logger.isDebugEnabled())
			logger.debug("Unregistered. - notify: {}, consumer: {}.",
					registration.getSelector().getObject(),
					registration.getObject().getId());
	};


	/**
	 * Configures the component to use the configured Environment's default dispatcher
	 *
	 * @return {@code this}
	 */
	public final SPEC defaultRouterScheduler() {
		this.routerScheduler = Schedulers.newElastic("EventRouter");
		return (SPEC) this;
	}

	/**
	 * Configures the component to use the given {@code routerScheduler}
	 *
	 * @param routerScheduler The dispatcher to use
	 *
	 * @return {@code this}
	 */
	public final SPEC routerScheduler(Scheduler routerScheduler) {
		this.routerScheduler = routerScheduler;
		return (SPEC) this;
	}


	/**
	 * Assigns the component's Filter
	 *
	 * @return {@code this}
	 */
	public final SPEC eventFilter(Filter filter) {
		if (this.router == null)
			throw new IllegalArgumentException("Cannot set both a filter and a router. Use one or the other.");
		this.eventFilter = filter;
		return (SPEC) this;
	}

	/**
	 * Assigns the component's EventRouter
	 *
	 * @return {@code this}
	 */
	public final SPEC eventRouter(Router router) {
		if (this.eventFilter == null)
			throw new IllegalArgumentException("Cannot set both a filter and a router. Use one or the other.");
		this.router = router;
		return (SPEC) this;
	}

	/**
	 * Configures the component's EventRouter to broadcast events to all matching consumers
	 *
	 * @return {@code this}
	 */
	public final SPEC broadcastEventRouting() {
		this.eventRoutingStrategy = EventRoutingStrategy.BROADCAST;
		return (SPEC) this;
	}

	/**
	 * Configures the component's EventRouter to route events to one consumer that's randomly selected from that matching
	 * consumers
	 *
	 * @return {@code this}
	 */
	public final SPEC randomEventRouting() {
		this.eventRoutingStrategy = EventRoutingStrategy.RANDOM;
		return (SPEC) this;
	}

	/**
	 * Configures the component's EventRouter to route events to the first of the matching consumers
	 *
	 * @return {@code this}
	 */
	public final SPEC firstEventRouting() {
		this.eventRoutingStrategy = EventRoutingStrategy.FIRST;
		return (SPEC) this;
	}

	/**
	 * Configures the component's EventRouter to route events to one consumer selected from the matching consumers using a
	 * round-robin algorithm consumers
	 *
	 * @return {@code this}
	 */
	public final SPEC roundRobinEventRouting() {
		this.eventRoutingStrategy = EventRoutingStrategy.ROUND_ROBIN;
		return (SPEC) this;
	}

	public final SPEC deDuplicationEventRouting() {
		this.eventRoutingStrategy = EventRoutingStrategy.DE_DUPLICATION;
		return (SPEC) this;
	}

	/**
	 * Configures the component's error handler for any errors occurring during dispatch (e.g. Exceptions resulting from
	 * calling a {@code Consumer#accept} method.
	 *
	 * @param schedulerErrorHandler
	 * 		the error handler for dispatching errors
	 *
	 * @return {@code this}
	 */
	public SPEC dispatchErrorHandler(Consumer<Throwable> schedulerErrorHandler) {
		this.dispatchErrorHandler = schedulerErrorHandler;
		return (SPEC) this;
	}


	/**
	 * Configures this component to provide event tracing when dispatching and routing an event.
	 *
	 * @return {@code this}
	 */
	public final SPEC traceEventPath() {
		return traceEventPath(true);
	}

	/**
	 * Configures this component to provide or not provide event tracing when dispatching and routing an event.
	 *
	 * @param b
	 * 		whether to trace the event path or not
	 *
	 * @return {@code this}
	 */
	public final SPEC traceEventPath(boolean b) {
		this.traceEventPath = b;
		return (SPEC) this;
	}

	/**
	 * Configures the {@link Registry} to use when creating this component. Registries can be
	 * shared to reduce GC pressure and potentially be persisted across restarts.
	 *
	 * @param consumerRegistry
	 * 		the consumer registry to use
	 *
	 * @return {@code this}
	 */
	public SPEC consumerRegistry(Registry<Object, PayloadConsumer<?>> consumerRegistry) {
		this.consumerRegistry = consumerRegistry;
		return (SPEC) this;
	}

	/**
	 * Configures the callback to invoke if a notification key is sent into this component and there are no consumers
	 * registered to respond to it.
	 *
	 * @param consumerNotFoundHandler
	 * 		the not found handler to use
	 *
	 * @return {@code this}
	 */
	public SPEC consumerNotFoundHandler(Consumer<Object> consumerNotFoundHandler) {
		this.consumerRegistry = Registries.create(true, consumerNotFoundHandler, ON_REGISTER, ON_UNREGISTER);
		return (SPEC) this;
	}

	protected abstract TARGET configure(EventBus reactor);

	@Override
	protected final TARGET configure(List<FluxProcessor<Event<?>, Event<?>>> dispatcherProcessors,
									 LoadBalanceStrategy loadBalanceStrategy) {
		return this.configure(this.createReactor(dispatcherProcessors, loadBalanceStrategy));
	}

	private EventBus createReactor(List<FluxProcessor<Event<?>, Event<?>>> dispatcherProcessors,
								   LoadBalanceStrategy loadBalanceStrategy) {
		Registry<Object, PayloadConsumer<?>> consumerRegistry = Optional
				.ofNullable(this.consumerRegistry)
				.orElseGet(this::createRegistry);
		Router router = Optional
				.ofNullable(this.router)
				.orElseGet(this::createEventRouter);
		List<Dispatcher<Event<?>>> dispatchers = this.createEventDispatchers(
				dispatcherProcessors,
				consumerRegistry,
				router
		);
		return new EventBus(
				consumerRegistry,
				dispatchers,
				router,
				dispatchErrorHandler,
				loadBalanceStrategy
		);
	}

	private List<Dispatcher<Event<?>>> createEventDispatchers(List<FluxProcessor<Event<?>, Event<?>>> dispatcherProcessors,
															  Registry<Object, PayloadConsumer<?>> consumerRegistry,
															  Router router) {
		return dispatcherProcessors.stream()
				.map(dispatcher ->
						new EventDispatcher(dispatcher, consumerRegistry, router, dispatchErrorHandler)
				)
				.collect(Collectors.toList());
	}

	private Router createEventRouter() {
		Router evr = new ConsumerFilteringRouter(
				Optional.ofNullable(eventFilter)
						.orElseGet(this::createFilter),
				Optional.ofNullable(routerScheduler)
						.orElseGet(() -> { defaultRouterScheduler(); return routerScheduler; })
		);
		return traceEventPath ? new TraceableDelegatingRouter(evr) : evr;
	}

	private Filter createFilter() {
		Filter filter;
		if (EventRoutingStrategy.ROUND_ROBIN == eventRoutingStrategy) {
			filter = new RoundRobinFilter();
		} else if (EventRoutingStrategy.RANDOM == eventRoutingStrategy) {
			filter = new RandomFilter();
		} else if (EventRoutingStrategy.FIRST == eventRoutingStrategy) {
			filter = new FirstFilter();
		} else if (EventRoutingStrategy.DE_DUPLICATION == eventRoutingStrategy) {
			filter = new DeDuplicationFilter();
		} else {
			filter = new PassThroughFilter();
		}
		return (traceEventPath ? new TraceableDelegatingFilter(filter) : filter);
	}

	private Registry createRegistry() {
		return Registries.create(true, null, ON_REGISTER, ON_UNREGISTER);
	}


	protected enum EventRoutingStrategy {
		BROADCAST, RANDOM, ROUND_ROBIN, FIRST, DE_DUPLICATION
	}
}

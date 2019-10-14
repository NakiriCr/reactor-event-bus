package cn.gitlab.virtualcry.reactor.bus.spec;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.filter.*;
import cn.gitlab.virtualcry.reactor.bus.registry.Registration;
import cn.gitlab.virtualcry.reactor.bus.registry.Registries;
import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.routing.ConsumerFilteringRouter;
import cn.gitlab.virtualcry.reactor.bus.routing.Router;
import cn.gitlab.virtualcry.reactor.bus.routing.TraceableDelegatingRouter;
import lombok.Builder;
import lombok.Getter;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.function.Consumer;

/**
 * A generic environment-aware class for specifying components that need to be configured with a {@link Registry},
 * and {@link Router}.
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
public abstract class EventRoutingComponentSpec<SPEC extends
		EventRoutingComponentSpec<SPEC, TARGET>, TARGET> {

	private EventRoutingStrategy  					eventRoutingStrategy;
	private Scheduler 								eventConsumerScheduler;
	private Scheduler 								routerScheduler;
	private Router 									router;
	private Filter                					eventFilter;
	private Consumer<Throwable> 					dispatchErrorHandler;
	private Registry<Object,
			Consumer<? extends Event<?>>>			consumerRegistry;
	private boolean 								traceEventPath = false;


	public static final Consumer<Registration<Object,
			? extends Consumer<? extends Event<?>>>> ON_REGISTER = registration -> {
		final Logger logger = Loggers.getLogger(Registry.class);
		if (logger.isDebugEnabled())
			logger.debug("Registered. - selector: {}, consumer: {}.",
					registration.getSelector(),
					registration.getObject()
			);
	};

	public static final Consumer<Registration<Object,
			? extends Consumer<? extends Event<?>>>> ON_UNREGISTER = registration -> {
		final Logger logger = Loggers.getLogger(Registry.class);
		if (logger.isDebugEnabled())
			logger.debug("Unregistered. - selector: {}, consumer: {}.",
					registration.getSelector(),
					registration.getObject()
			);
	};


	/**
	 * Configures the component to use the default scheduler.
	 *
	 * @return {@code this}
	 */
	public final SPEC defaultEventConsumerScheduler() {
		this.eventConsumerScheduler = Schedulers.newElastic("EventConsumer");
		return (SPEC) this;
	}


	/**
	 * Configures the component to use the given {@code eventConsumerScheduler}
	 *
	 * @param eventConsumerScheduler The scheduler to use
	 *
	 * @return {@code this}
	 */
	public final SPEC eventConsumerScheduler(Scheduler eventConsumerScheduler) {
		this.eventConsumerScheduler = eventConsumerScheduler;
		return (SPEC) this;
	}


	/**
	 * Configures the component to use the default scheduler.
	 *
	 * @return {@code this}
	 */
	public final SPEC defaultRouterScheduler() {
		this.routerScheduler = Schedulers.immediate();
		return (SPEC) this;
	}


	/**
	 * Configures the component to use the given {@code routerScheduler}
	 *
	 * @param routerScheduler The scheduler to use
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
		if (router == null)
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
		if (eventFilter == null)
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


	/**
	 * Configures the component's EventRouter to broadcast events to all matching consumers after
	 * de-duplication.
	 *
	 * @return {@code this}
	 */
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
	public SPEC consumerRegistry(Registry<Object, Consumer<? extends Event<?>>> consumerRegistry) {
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


	protected EventRoutingComponent createEventRoutingComponent() {
		return EventRoutingComponent.builder()
				.router(router != null ? router : createEventRouter())
				.consumerRegistry(consumerRegistry != null ? consumerRegistry : createRegistry())
				.dispatchErrorHandler(this.dispatchErrorHandler)
				.build();
	}

	private Router createEventRouter() {
		Router evr = new ConsumerFilteringRouter(
				eventFilter != null ? eventFilter : createFilter(),
				routerScheduler != null ? routerScheduler : Schedulers.immediate(),
				eventConsumerScheduler != null ? eventConsumerScheduler : Schedulers.newElastic("EventConsumer")
		);
		return traceEventPath ? new TraceableDelegatingRouter(evr) : evr;
	}

	private Filter createFilter() {
		Filter filter;
		if (this.eventRoutingStrategy == EventRoutingStrategy.ROUND_ROBIN) {
			filter = new RoundRobinFilter();
		}
		else if (this.eventRoutingStrategy == EventRoutingStrategy.RANDOM) {
			filter = new RandomFilter();
		}
		else if (this.eventRoutingStrategy == EventRoutingStrategy.FIRST) {
			filter = new FirstFilter();
		}
		else if (this.eventRoutingStrategy == EventRoutingStrategy.DE_DUPLICATION) {
			filter = new DeDuplicationFilter();
		}
		else {
			filter = new PassThroughFilter();
		}
		return (this.traceEventPath ? new TraceableDelegatingFilter(filter) : filter);
	}

	private Registry createRegistry() {
		return Registries.create(true, null, ON_REGISTER, ON_UNREGISTER);
	}


	@Builder @Getter
	protected static class EventRoutingComponent {
		private Router									router;
		private Registry<Object,
				Consumer<? extends Event<?>>>			consumerRegistry;
		private Consumer<Throwable> 					dispatchErrorHandler;
	}

	protected enum EventRoutingStrategy {
		BROADCAST, RANDOM, ROUND_ROBIN, FIRST, DE_DUPLICATION
	}
}

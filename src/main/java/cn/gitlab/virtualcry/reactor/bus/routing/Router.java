package cn.gitlab.virtualcry.reactor.bus.routing;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.registry.Registration;

import java.util.List;
import java.util.function.Consumer;

/**
 * An {@code Router} is used to route an {@code Object} to {@link Consumer Consumers}.
 *
 * @author Andy Wilkinson
 * @author Stephane Maldini
 * @author VirtualCry
 * @since 3.2.2
 */
public interface Router {

	/**
	 * Routes the {@code event}, triggered by a notification with the given {@code key} to the
	 * {@code consumers}. Depending on the router implementation, zero or more of the consumers
	 * will receive the event. In the event of an exception during routing the {@code errorConsumer}
	 * is invoked. {@code errorConsumer} may be null, in which case the exception is swallowed.
	 *
	 * @param key The notification key
	 * @param data The {@code Object} to route
	 * @param registrations The {@code Registration}s to route the event to.
	 * @param errorConsumer The {@code Consumer} to invoke when an error occurs during event routing
	 */
	<E extends Event<?>> void route(Object key, E data,
									   List<Registration<Object, ? extends Consumer<? extends Event<?>>>> registrations,
									   Consumer<Throwable> errorConsumer);
}
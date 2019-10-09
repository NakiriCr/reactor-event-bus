package cn.gitlab.virtualcry.reactor.bus.routing;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.registry.Registration;
import cn.gitlab.virtualcry.reactor.bus.support.PayloadConsumer;

import java.util.List;
import java.util.function.Consumer;

/**
 * An {@code Router} is used to route an {@code Object} to {@link Consumer Consumers}.
 *
 * @author VirtualCry
 * @since 3.2.2
 *
 */
public interface Router {

	/**
	 * Routes the {@code event}, triggered by a notification with the given {@code key} to the
	 * {@code consumers}. Depending on the router implementation, zero or more of the consumers
	 * will receive the event. Upon successful completion of the event routing, the
	 * {@code completionConsumer} will be invoked. {@code completionConsumer} may be null. In the
	 * event of an exception during routing the {@code errorConsumer} is invoked.
	 * {@code errorConsumer} may be null, in which case the exception is swallowed.
	 *
	 * @param key The notification key
	 * @param data The {@code Object} to route
	 * @param consumers The {@code Consumer}s to route the event to.
	 */
	<E extends Event<V>, V> void route(Object key, E data,
									   List<Registration<Object, ? extends PayloadConsumer<? super V>>> consumers,
									   Consumer<Throwable> errorConsumer);
}
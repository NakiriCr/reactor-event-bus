package cn.gitlab.virtualcry.reactor.bus.spec;

import cn.gitlab.virtualcry.reactor.bus.EventBus;

/**
 * A helper class for configuring a new {@link EventBus}.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public class EventBusSpec extends DispatcherComponentSpec<EventBusSpec, EventBus> {

	@Override
	protected final EventBus configure(EventBus reactor) {
		return reactor;
	}
}

package cn.gitlab.virtualcry.reactor.bus.spec;

import cn.gitlab.virtualcry.reactor.bus.EventBus;

/**
 * A helper class for configuring a new {@link EventBus}.
 *
 * @author VirtualCry
 */
public class EventBusSpec extends EventRoutingComponentSpec<EventBusSpec, EventBus> {

	@Override
	protected final EventBus configure(EventBus reactor) {
		return reactor;
	}
}

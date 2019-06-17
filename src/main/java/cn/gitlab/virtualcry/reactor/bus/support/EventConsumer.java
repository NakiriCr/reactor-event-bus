package cn.gitlab.virtualcry.reactor.bus.support;

import cn.gitlab.virtualcry.reactor.bus.Event;

import java.util.function.Consumer;

/**
 * @author VirtualCry
 */
public interface EventConsumer<T extends Event> extends Consumer<T> {

}

package cn.gitlab.virtualcry.reactor.bus.support;

import cn.gitlab.virtualcry.reactor.bus.Event;

import java.util.function.Consumer;

/**
 * Interface - consume {@link Event}.
 *
 * @author VirtualCry
 */
public interface EventConsumer<T extends Event> extends Consumer<T> {

}

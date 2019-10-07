package cn.gitlab.virtualcry.reactor.bus.spec.stream;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.env.BuiltInEnvironment;
import reactor.core.publisher.FluxProcessor;

/**
 * A generic environment-aware class for specifying components tha  need to be configured
 * with an {@link BuiltInEnvironment} and {@link FluxProcessor},
 *
 * @author VirtualCry
 * @see BuiltInEnvironment#eventStreamConfig()
 * @since 3.2.2
 */
public interface EventStreamComponentSpec {

    /**
     * Create an event stream.
     *
     * @param <T> The type of {@link Event}
     * @return   A new {@link FluxProcessor} used in receiving and subscribing events.
     */
    <T> FluxProcessor<T, T> create();
}

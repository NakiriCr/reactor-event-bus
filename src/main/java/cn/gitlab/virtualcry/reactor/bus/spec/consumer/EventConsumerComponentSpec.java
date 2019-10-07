package cn.gitlab.virtualcry.reactor.bus.spec.consumer;

import cn.gitlab.virtualcry.reactor.bus.env.BuiltInEnvironment;
import reactor.core.scheduler.Scheduler;

/**
 * A generic environment-aware class for specifying components tha  need to be configured
 * with an {@link BuiltInEnvironment} and {@link Scheduler},
 *
 * @author VirtualCry
 * @see BuiltInEnvironment#eventConsumerConfig()
 * @since 3.2.2
 */
public interface EventConsumerComponentSpec {

    /**
     * Create a scheduler.
     *
     * @return   A new {@link Scheduler} used in consuming events.
     */
    Scheduler create();
}

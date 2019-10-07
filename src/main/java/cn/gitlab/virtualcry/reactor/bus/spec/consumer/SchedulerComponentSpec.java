package cn.gitlab.virtualcry.reactor.bus.spec.consumer;

import cn.gitlab.virtualcry.reactor.bus.env.BuiltInEnvironment;
import lombok.Builder;
import reactor.core.scheduler.Scheduler;

/**
 * A helper class for configuring {@link EventConsumerComponentSpec} used in {@link BuiltInEnvironment}.
 *
 * @author VirtualCry
 * @see BuiltInEnvironment#eventConsumerConfig()
 * @since 3.2.2
 */
@Builder
final class SchedulerComponentSpec implements EventConsumerComponentSpec {

    private Scheduler                               scheduler;


    @Override
    public Scheduler create() {
        return scheduler;
    }
}

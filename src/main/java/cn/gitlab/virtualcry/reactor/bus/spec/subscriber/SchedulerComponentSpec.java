package cn.gitlab.virtualcry.reactor.bus.spec.subscriber;

import lombok.Builder;
import reactor.core.scheduler.Scheduler;

/**
 * A generic environment-aware class for specifying components tha  need to be configured with an {@link
 * cn.gitlab.virtualcry.reactor.bus.env.Environment},
 *
 * @author VirtualCry
 * @since 3.2.2
 */
@Builder
final class SchedulerComponentSpec implements EventSubscriberComponentSpec {

    private Scheduler                                   scheduler;


    @Override
    public Scheduler create() {
        return scheduler;
    }
}

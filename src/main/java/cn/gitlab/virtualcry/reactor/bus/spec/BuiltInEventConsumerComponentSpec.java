package cn.gitlab.virtualcry.reactor.bus.spec;

import cn.gitlab.virtualcry.reactor.bus.env.BuiltInEnvironment;
import cn.gitlab.virtualcry.reactor.bus.env.thread.ConsumerThreadFactory;
import cn.gitlab.virtualcry.reactor.bus.spec.consumer.EventConsumerComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.consumer.EventConsumerSpec;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

/**
 * A built-in {@link EventConsumerComponentSpec} used to configure {@link BuiltInEnvironment}.
 *
 * @author VirtualCry
 * @see BuiltInEnvironment#eventConsumerConfig()
 * @since 3.2.2
 */
public class BuiltInEventConsumerComponentSpec {
    private static final String SUBSCRIBER_THREAD_NAME = "EventSubscriber";

    public static final EventConsumerComponentSpec SINGLE_SCHEDULER;
    public static final EventConsumerComponentSpec ELASTIC_SCHEDULER;
    public static final EventConsumerComponentSpec PARALLEL_SCHEDULER;
    public static final EventConsumerComponentSpec UNLIMITED_PARALLEL_SCHEDULER;


    static {
        SINGLE_SCHEDULER = EventConsumerSpec.scheduler()
                .scheduler(Schedulers.newSingle(SUBSCRIBER_THREAD_NAME))
                .build();
        ELASTIC_SCHEDULER = EventConsumerSpec.scheduler()
                .scheduler(Schedulers.newElastic(SUBSCRIBER_THREAD_NAME))
                .build();
        PARALLEL_SCHEDULER = EventConsumerSpec.scheduler()
                .scheduler(Schedulers.newParallel(SUBSCRIBER_THREAD_NAME))
                .build();
        UNLIMITED_PARALLEL_SCHEDULER = EventConsumerSpec.scheduler()
                .scheduler(Schedulers.fromExecutor(Executors.newCachedThreadPool(new ConsumerThreadFactory())))
                .build();
    }
}

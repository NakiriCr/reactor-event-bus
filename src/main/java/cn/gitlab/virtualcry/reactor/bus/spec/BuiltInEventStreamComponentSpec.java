package cn.gitlab.virtualcry.reactor.bus.spec;

import cn.gitlab.virtualcry.reactor.bus.env.BuiltInEnvironment;
import cn.gitlab.virtualcry.reactor.bus.spec.stream.EventStreamComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.stream.EventStreamSpec;
import reactor.util.concurrent.Queues;

/**
 * A built-in {@link EventStreamComponentSpec} used to configure {@link BuiltInEnvironment}.
 *
 * @author VirtualCry
 * @see BuiltInEnvironment#eventStreamConfig()
 * @since 3.2.2
 */
public class BuiltInEventStreamComponentSpec {

    public static final EventStreamComponentSpec REPLAY_PROCESSOR;
    public static final EventStreamComponentSpec TOPIC_PROCESSOR;
    public static final EventStreamComponentSpec WORK_QUEUE_PROCESSOR;


    static {
        REPLAY_PROCESSOR = EventStreamSpec.replayProcessor()
                .historySize(Queues.SMALL_BUFFER_SIZE)
                .unbounded(true)
                .build();
        TOPIC_PROCESSOR = EventStreamSpec.topicProcessor()
                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                .share(true)
                .autoCancel(false)
                .build();
        WORK_QUEUE_PROCESSOR = EventStreamSpec.workQueueProcessor()
                .bufferSize(Queues.SMALL_BUFFER_SIZE)
                .share(true)
                .autoCancel(false)
                .build();
    }
}

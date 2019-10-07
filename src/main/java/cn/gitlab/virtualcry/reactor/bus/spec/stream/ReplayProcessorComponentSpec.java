package cn.gitlab.virtualcry.reactor.bus.spec.stream;

import cn.gitlab.virtualcry.reactor.bus.env.BuiltInEnvironment;
import lombok.Builder;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.ReplayProcessor;

/**
 * A {@link ReplayProcessor} will be created as an event stream.
 *
 * @author VirtualCry
 * @see BuiltInEnvironment#eventStreamConfig()
 * @since 3.2.2
 */
@Builder
final class ReplayProcessorComponentSpec implements EventStreamComponentSpec {

    private int                                     historySize;
    private boolean                                 unbounded;


    @Override
    public <T> FluxProcessor<T, T> create() {
        return ReplayProcessor.create(historySize, unbounded);
    }
}

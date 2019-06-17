package cn.gitlab.virtualcry.reactor.bus.processor;

import cn.gitlab.virtualcry.reactor.bus.Event;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * Record events whenever an Event published. And will be supplied for {@link EventRouter}
 *
 * @author VirtualCry
 */
public final class EventRecorder<T extends Event> implements Consumer<FluxSink<T>> {

    private final Executor                          executor;
    private final BlockingQueue<T>                  queue;


    public EventRecorder(Executor executor) {
        this.executor = executor;
        this.queue = new LinkedBlockingQueue<>();
    }


    /**
     * Record an {@link Event} in queue.
     *
     * @param event The {@literal Event} to be used for publishing
     */
    public void record(T event) {
        queue.offer(event);
    }


    /**
     * Supply {@link Event}s for {@link EventProcessor} to publish.
     *
     * @param fluxSink The {@literal FluxSink<Event>} to be used for publishing
     */
    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void accept(FluxSink<T> fluxSink) {
        this.executor.execute(() -> {
            while (true) {
                try { fluxSink.next(queue.take()); }
                catch (InterruptedException ex) {throw new RuntimeException(ex); }
            }
        });
    }

}

package cn.gitlab.virtualcry.reactor.bus.processor;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.env.Environment;
import cn.gitlab.virtualcry.reactor.bus.support.EventProcessorStore;
import reactor.core.publisher.Flux;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.concurrent.Executors;

/**
 * An {@code Router} is used to route an {@code Event} to {@link EventProcessor EventProcessor}.
 *
 * @author VirtualCry
 */
public final class EventRouter {
    private final Logger                                logger;

    private final Environment                           env;
    private final EventRecorder<Event>                  recorder;
    private final EventProcessorStore                   processorStore;


    public EventRouter(Environment env) {
        this.env = env;
        this.logger = Loggers.getLogger(this.getClass());
        this.recorder = new EventRecorder<>(Executors.newFixedThreadPool(1));
        this.processorStore = new EventProcessorStore();

        this.initialize();
    }


    /**
     * Listen event stream from {@link EventRecorder} and push event to the {@link EventProcessor}.
     * Each type of event corresponds to a processor.
     */
    @SuppressWarnings("unchecked")
    private void initialize() {
        Flux.create(this.recorder)
                .doOnNext(event -> {
                    if (this.logger.isDebugEnabled())
                        this.logger.debug("Event { " + event.getClass().getName() + ": " + event.getEventId()
                                + " } has been published.");
                })
                .subscribe(event -> ((EventProcessor) route(event.getClass()))
                        .onNext(event)
                );
    }


    /**
     * Get one kind of {@link EventProcessor} by {@literal Class<Event> event type}.
     *
     * @param eventType The {@literal Class<T>} to be used for routing.
     * @param <T> Type of {@link Event}
     * @return The event processor {@link EventProcessor} to handle event.
     */
    public <T extends Event> EventProcessor<T> route(Class<T> eventType) {
        return this.processorStore.computeIfAbsent(eventType, tEventType -> new EventProcessor<>(env));
    }


    /**
     * Publish {@link Event} to {@link EventRecorder}.
     *
     * @param event The {@literal Event} to be used for publishing
     */
    public final void publish(Event event) {
        this.recorder.record(event);
    }

}

package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.env.Environment;
import cn.gitlab.virtualcry.reactor.bus.registry.Registration;
import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.support.PayloadConsumer;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.FluxProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Core of {@link BusProcessor}
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public class EventBusProcessor implements BusProcessor<Event<?>> {
    private final Logger                                logger;

    private final Environment                           env;
    private final Registry<Object,
            PayloadConsumer<?>>                         registry;
    private final FluxProcessor<Event,
            Event>                                      eventStream;
    private final Scheduler                             consumerScheduler;
    private final Disposable                            disposable;


    public EventBusProcessor(Environment env) {
        this.logger = Loggers.getLogger(this.getClass());

        this.env = env;
        this.registry = this.env.registryConfig().create();
        this.eventStream = this.env.eventStreamConfig().create();
        this.consumerScheduler = this.env.eventConsumerConfig().create();
        this.disposable = this.initialize();
    }

    private static final PayloadConsumer NO_OPS = PayloadConsumer.builder()
            .id("NO_OPS Consumer")
            .delegate(event -> { })
            .build();

    /**
     *  Subscribe to event streams and define event-related processing.
     *
     * @return a new {@link Disposable} that can be used to cancel the underlying {@link Subscription}
     */
    private Disposable initialize() {
        return this.eventStream
                .doOnNext(event -> {
                    if (this.logger.isDebugEnabled())
                        this.logger.debug("Published event. - [{}: {}]",
                                event.getKey(),
                                event.getId()
                        );
                    this.handleEvent(event, consumer -> consumer.accept(event.getData()));
                })
                .doOnSubscribe(subscription -> {
                    if (this.logger.isDebugEnabled())
                        this.logger.debug("Subscribed on event bus.");
                })
                .doOnComplete(() -> {
                    if (this.logger.isDebugEnabled())
                        this.logger.debug("Completed on event bus.");
                })
                .doOnCancel(() -> {
                    if (this.logger.isDebugEnabled())
                        this.logger.debug("Canceled on event bus.");
                })
                .subscribe();
    }

    /**
     * Handle event.
     * @param event The {@literal event} to be used for matching event subscribers.
     * @param eventConsumer The {@literal consumer} to be used for consuming event.
     */
    @SuppressWarnings("unchecked")
    private void handleEvent(Event event, Consumer<PayloadConsumer<Object>> eventConsumer) {
        this.getEventConsumers(event).stream()
                .sorted()
                .peek(consumer -> {
                    if (this.logger.isDebugEnabled())
                        this.logger.debug("Delivered event. - [{}: {}] → [{}]",
                                event.getKey(),
                                event.getId(),
                                consumer.getId()
                        );
                })
                .forEach(consumer -> consumerScheduler.schedule(() -> {
                    try { eventConsumer.accept(consumer); }
                    catch (Exception ex) {
                        this.logger.error("Error in handling event. - ["
                                + event.getKey() + ": " + event.getId() + "]", ex);
                    }
                }));
    }

    private Set<PayloadConsumer> getEventConsumers(Event event) {
        final Set<PayloadConsumer> consumers = this.registry.select(event.getKey()).stream()
                .map(Registration::getObject)
                .collect(Collectors.toSet());
        if (consumers.isEmpty())
            consumers.add(NO_OPS);
        return consumers;
    }


    @Override
    public Registry<Object, PayloadConsumer<?>> getRegistry() {
        return this.registry;
    }

    @Override
    public void onNext(Event<?> event) {
        this.eventStream.onNext(event);
    }

    @Override
    public void onComplete() {
        this.eventStream.onComplete();
    }

    @Override
    public void onCancel() {
        this.disposable.dispose();
    }
}

package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.env.Environment;
import cn.gitlab.virtualcry.reactor.bus.event.TestEvent;
import cn.gitlab.virtualcry.reactor.bus.selector.Selector;
import cn.gitlab.virtualcry.reactor.bus.spec.BuiltInEventConsumerComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.BuiltInEventStreamComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.support.PayloadConsumer;
import org.junit.Before;
import org.junit.Test;
import reactor.util.Loggers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

import static cn.gitlab.virtualcry.reactor.bus.selector.Selectors.$;

/**
 * Test for {@link EventBus}
 *
 * @author VirtualCry
 */
public class EventBusTest {
    private EventBus bus;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Before
    public void initialize() {
        this.bus = EventBus.create(Environment.builder()
                .eventStreamComponentSpec(BuiltInEventStreamComponentSpec.WORK_QUEUE_PROCESSOR)
                .eventConsumerComponentSpec(BuiltInEventConsumerComponentSpec.UNLIMITED_PARALLEL_SCHEDULER)
                .build());
    }

    @Test
    public void testEventBus() throws Exception {
        // create semaphore.
        Semaphore semaphore = new Semaphore(0);

        try { this.testEvent(); }
        catch (Exception ex) { throw new RuntimeException(ex); }

        // block.
        semaphore.acquire();
    }

    private void testEvent() throws Exception {

        // create event.
        List<TestEvent> testEvents = new ArrayList<>();
        for (int i = 1; i < 2; i++) {
            testEvents.add(TestEvent.builder().content("TestContent " + i).build());
        }

        // create event subscriber.
        List<PayloadConsumer<TestEvent>> subscribers = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            subscribers.add(testSubscriberFunc.apply(i));
        }

        // subscribe on event bus.
        Selector selector = $(TestEvent.class);
        subscribers.forEach(subscriber -> this.bus.on(selector, subscriber));
        subscribers.forEach(subscriber -> this.bus.on(selector, subscriber));

        // publish event.
        testEvents.forEach(testEvent -> this.bus.notify(TestEvent.class, Event.wrap(testEvent)));

//        Thread.sleep(Duration.ofSeconds(3).toMillis());

//         subscribe on event bus.
//        subscribers.forEach(subscriber -> this.bus.on($(TestEvent.class), subscriber));

//        this.bus.cancel(TestEvent.class);

        // publish event.
        testEvents.forEach(testEvent -> this.bus.notify(TestEvent.class, Event.wrap(testEvent)));


//        this.bus.notify(Flux.fromIterable(testEvents), TestEvent::getClass);

        //         subscribe on event bus.
//        subscribers.forEach(subscriber -> this.bus.on(Selectors.$(TestEvent.class), subscriber));
    }

    private Function<Integer, PayloadConsumer<TestEvent>> testSubscriberFunc = index ->
            PayloadConsumer.<TestEvent>builder()
                    .id("TestEventSubscriber: " + index)
                    .priority(index)
                    .delegate(event -> {
                        try {
                            Loggers.getLogger(this.getClass()).info(index + ": Start to sleep 3s.");
                            Thread.sleep(Duration.ofSeconds(1).toMillis());
//                            if (index / 2 != 0)
//                                throw new RuntimeException("Test error.");
                                System.err.println("{ TestEventSubscriber: " + index + " }: End. | " + event.getContent());
                        }
                        catch (Exception ex) { throw new RuntimeException(ex); }
                    })
                    .build();
}

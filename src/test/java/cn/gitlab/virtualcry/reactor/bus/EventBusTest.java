package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.env.Environment;
import cn.gitlab.virtualcry.reactor.bus.event.TestEvent;
import cn.gitlab.virtualcry.reactor.bus.event.TestStickyEvent;
import cn.gitlab.virtualcry.reactor.bus.support.EventSubscriber;
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
import java.util.stream.Collectors;

/**
 * Test for {@link EventBus}
 *
 * @author VirtualCry
 */
public class EventBusTest {
    private Bus bus;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Before
    public void initialize() {
        this.bus = EventBus.create(Environment.ASYNCHRONOUS);
    }

    @Test
    public void testEventBus() throws Exception {
        // create semaphore.
        Semaphore semaphore = new Semaphore(0);

        for (int i = 0; i < 10; i++) {

            executorService.execute(() -> {
                try { this.testEvent(); }
                catch (Exception ex) { throw new RuntimeException(ex); }
            });

            executorService.execute(() -> {
                try { this.testStickyEvent(); }
                catch (Exception ex) { throw new RuntimeException(ex); }
            });

        }

        // block.
        semaphore.acquire();
    }




    private void testEvent() throws Exception {

        // create event.
        List<TestEvent> testEvents = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            testEvents.add(TestEvent.builder().content("TestContent " + i).build());
        }

        // create event subscriber.
        List<EventSubscriber<TestEvent>> subscribers = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            subscribers.add(testSubscriberFunc.apply(i));
        }

        // subscribe on event bus.
        this.bus.on(TestEvent.class, subscribers);

        // publish event.
        testEvents.forEach(this.bus::post);

//        Thread.sleep(Duration.ofSeconds(3).toMillis());

        // subscribe on event bus.
        this.bus.cancel(TestStickyEvent.class, subscribers.stream().map(EventSubscriber::getSubscriberID).collect(Collectors.toList()));

        // subscribe on event bus.
        this.bus.on(TestEvent.class, subscribers);

        // publish event.
        testEvents.forEach(this.bus::post);
    }



    private void testStickyEvent() throws Exception {

        // create event.
        List<TestStickyEvent> testStickyEvents = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            testStickyEvents.add(TestStickyEvent.builder().content("TestContent " + i).build());
        }

        // create event subscriber.
        List<EventSubscriber<TestStickyEvent>> subscribers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            subscribers.add(testStickysubscriberFunc.apply(i));
        }

        // publish event.
        testStickyEvents.forEach(this.bus::post);

        // subscribe on event bus.
        this.bus.on(TestStickyEvent.class, subscribers);

//        Thread.sleep(Duration.ofSeconds(6).toMillis());

        // publish event.
        testStickyEvents.forEach(this.bus::postSticky);

        // unsubscribe.
        this.bus.cancel(TestStickyEvent.class, subscribers.stream().map(EventSubscriber::getSubscriberID).collect(Collectors.toList()));
//        Thread.sleep(Duration.ofSeconds(6).toMillis());
        // subscribe on event bus.
        this.bus.on(TestStickyEvent.class, testStickysubscriberFunc.apply(11));
    }



    private Function<Integer, EventSubscriber<TestEvent>> testSubscriberFunc = index ->
            EventSubscriber.<TestEvent>builder()
                    .subscriberID("TestEventSubscriber: " + index)
                    .priority(index)
                    .eventConsumer(event -> {
                        try {
                            Loggers.getLogger(this.getClass()).info(index + ": Start to sleep 3s.");
                            Thread.sleep(Duration.ofSeconds(1).toMillis());
                            if (index / 2 != 0)
//                                throw new RuntimeException("Test error.");
                                System.err.println(index + ": End. | " + event.getContent());
                        }
                        catch (Exception ex) { throw new RuntimeException(ex); }
                    })
                    .build();

    private Function<Integer, EventSubscriber<TestStickyEvent>> testStickysubscriberFunc = index ->
            EventSubscriber.<TestStickyEvent>builder()
                    .subscriberID("TestStickyEventSubscriber: " + index)
                    .priority(index)
                    .eventConsumer(event -> {
                        try {
                            Loggers.getLogger(this.getClass()).info(index + ": Start to sleep 3s.");
                            Thread.sleep(Duration.ofSeconds(1).toMillis());
                            if (index / 2 != 0)
//                                throw new RuntimeException("Test error.");
                                System.err.println(index + ": End. | " + event.getContent());
                        }
                        catch (Exception ex) { throw new RuntimeException(ex); }
                    })
                    .build();
}

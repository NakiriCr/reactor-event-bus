package cn.gitlab.virtualcry.reactor.bus.test;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.EventBus;
import cn.gitlab.virtualcry.reactor.bus.selector.Selector;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static cn.gitlab.virtualcry.reactor.bus.selector.Selectors.$;

/**
 * Test for {@link EventBus}
 *
 * @see EventBus#on(Selector, Consumer)
 * @see EventBus#notify(Publisher, Object)
 * @see EventBus#notify(Object, Event)
 * @see EventBus#accept(Event)
 *
 * @author VirtualCry
 */
public class PublishAndSubscribeModeTest {
    private Logger                                  logger;
    private Semaphore                               semaphore;

    private EventBus                                bus;
    private String                                  notifyKey;


    @Before
    public void initialize() {
        this.logger = Loggers.getLogger(this.getClass());
        this.semaphore = new Semaphore(0);

        // create event bus.
        this.bus = EventBus.config().get();
        // set test key.
        this.notifyKey = "job.sink";

        // register.
        bus.on($(notifyKey), ev -> {
            logger.info("Received event. - {}", ev);
            // release.
            semaphore.release();
        });
    }


    @Test
    public void test() throws Exception {

        Flux<String> payloadFlux = Flux.fromArray(new String[] {"Hello", "World!"});
        Flux<Event> eventFlux = Flux
                .fromArray(new Event[]{ Event.wrap("Hello"), Event.wrap("World!") })
                .doOnNext(ev -> ev.setKey(notifyKey));

        // notify.
        bus.notify(payloadFlux, notifyKey);
        bus.notify(notifyKey, Event.wrap("Hello World!"));
        eventFlux.doOnNext(bus::accept).subscribe();

        // block.
        semaphore.acquire(5);
        // sleep.
        TimeUnit.SECONDS.sleep(1);
    }
}

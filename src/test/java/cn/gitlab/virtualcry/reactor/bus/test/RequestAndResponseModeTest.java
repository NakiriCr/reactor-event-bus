package cn.gitlab.virtualcry.reactor.bus.test;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.EventBus;
import cn.gitlab.virtualcry.reactor.bus.selector.Selector;
import org.junit.Before;
import org.junit.Test;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static cn.gitlab.virtualcry.reactor.bus.selector.Selectors.$;

/**
 * Test for {@link EventBus}
 *
 * @see EventBus#send(Object, Event)
 * @see EventBus#send(Object, Supplier)
 * @see EventBus#receive(Selector, Function)
 * @see EventBus#sendAndReceive(Object, Event, Consumer)
 * @see EventBus#sendAndReceive(Object, Supplier, Consumer)
 * @see EventBus#on(Selector, Consumer)
 *
 * @author VirtualCry
 */
public class RequestAndResponseModeTest {
    private Logger                                  logger;
    private Semaphore                               semaphore;

    private EventBus                                bus;
    private String                                  notifyKey;
    private String                                  replyKey;


    @Before
    public void initialize() {
        this.logger = Loggers.getLogger(this.getClass());
        this.semaphore = new Semaphore(0);

        // create event bus.
        this.bus = EventBus.config().get();
        // set test key.
        this.notifyKey = "job.sink";
        this.replyKey = "reply.sink";

        // register.
        bus.on($(replyKey), ev -> {
            logger.info("1. Reply it - {}", ev);
            // release.
            semaphore.release();
        });
        bus.receive($(notifyKey), ev -> {
            logger.info("Received event and ready to reply - {}", ev);
            return ev;
        });
    }


    @Test
    public void test() throws Exception {

        // send event.
        bus.send(notifyKey, Event.wrap("Hello World!", replyKey));

        // send event.
        bus.sendAndReceive(notifyKey,
                Event.wrap("Hello World!"),
                ev -> {
                    logger.info("2. Reply it - {}", ev);
                    // release.
                    semaphore.release();
                }
        );

        // block.
        semaphore.acquire(2);
        // sleep.
        TimeUnit.SECONDS.sleep(1);
    }
}

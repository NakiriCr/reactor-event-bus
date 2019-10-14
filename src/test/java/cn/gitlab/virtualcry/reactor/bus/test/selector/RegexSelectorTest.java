package cn.gitlab.virtualcry.reactor.bus.test.selector;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.EventBus;
import cn.gitlab.virtualcry.reactor.bus.selector.RegexSelector;
import org.junit.Before;
import org.junit.Test;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static cn.gitlab.virtualcry.reactor.bus.selector.Selectors.R;

/**
 * Test for {@link RegexSelector}
 *
 * @author VirtualCry
 */
public class RegexSelectorTest {
    private Logger                                  logger;
    private Semaphore                               semaphore;

    private EventBus                                bus;


    @Before
    public void initialize() {
        this.logger = Loggers.getLogger(this.getClass());
        this.semaphore = new Semaphore(0);

        // create event bus.
        this.bus = EventBus.config().get();
    }


    @Test
    public void test() throws Exception {

        // register.
        bus.on(R("^[1-9]\\d*$"), (Event<String> ev) -> {
            logger.info("Received event. - {}", ev.getData());
            // release.
            semaphore.release();
        });

        // notify
        bus.notify(23, Event.wrap("test"));

        // block.
        semaphore.acquire(1);
        // sleep.
        TimeUnit.SECONDS.sleep(1);
    }
}
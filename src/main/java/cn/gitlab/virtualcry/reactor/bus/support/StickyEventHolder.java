package cn.gitlab.virtualcry.reactor.bus.support;

import cn.gitlab.virtualcry.reactor.bus.StickyEvent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Somethings
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public final class StickyEventHolder extends
        ConcurrentHashMap<Class<? extends StickyEvent>, StickyEvent> {

    private static class StickyEventHolderInstance {
        private static final StickyEventHolder INSTANCE = new StickyEventHolder();
    }

    private StickyEventHolder() { }

    public static StickyEventHolder getSingleton() {  // singleton
        return StickyEventHolderInstance.INSTANCE;
    }
}

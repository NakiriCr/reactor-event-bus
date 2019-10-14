package cn.gitlab.virtualcry.reactor.bus.registry;

import cn.gitlab.virtualcry.reactor.bus.selector.ObjectSelector;
import cn.gitlab.virtualcry.reactor.bus.selector.Selector;
import lombok.Getter;

/**
 * @author VirtualCry
 * @since 3.2.2
 */
@Getter
public class CacheableRegistration<K ,V> implements Registration<K, V> {

    private final Selector<K>                           selector;
    private final V                                     object;

    private final Runnable                              onCancel;
    private volatile boolean                            cancelled;
    private volatile boolean                            cancelAfterUse;


    CacheableRegistration(Selector<K> selector, V object, Runnable onCancel) {
        this.selector = selector;
        this.object = object;
        this.onCancel = onCancel;
        this.cancelled = false;
        this.cancelAfterUse = false;
    }


    private static final Selector<Void> NO_MATCH = new ObjectSelector<Void, Void>(null) {
        @Override
        public boolean matches(Void key) {
            return false;
        }
    };


    @SuppressWarnings("unchecked")
    @Override
    public Selector<K> getSelector() {
        return (!cancelled ? selector : (Selector<K>) NO_MATCH);
    }

    @Override
    public Registration<K, V> cancelAfterUse() {
        this.cancelAfterUse = true;
        return this;
    }

    @Override
    public Registration<K, V> cancel() {
        if (!cancelled) {
            if (null != onCancel) {
                onCancel.run();
            }
            this.cancelled = true;
        }
        return this;
    }

    @Override
    public String toString() {
        return "CachableRegistration{" +
                "\n\tselector=" + selector +
                ",\n\tobject=" + object +
                ",\n\tonCancel=" + onCancel +
                ",\n\tcancelled=" + cancelled +
                ",\n\tcancelAfterUse=" + cancelAfterUse +
                "\n}";
    }
}

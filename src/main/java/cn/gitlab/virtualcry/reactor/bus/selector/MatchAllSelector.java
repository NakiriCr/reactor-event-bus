package cn.gitlab.virtualcry.reactor.bus.selector;

/**
 * Implementation of {@link Selector} that matches
 * all objects.
 *
 * @author Michael Klishin
 * @author VirtualCry
 * @since 3.2.2
 */
public class MatchAllSelector implements Selector {

    @Override
    public Object getObject() {
        return null;
    }

    @Override
    public boolean matches(Object key) {
        return true;
    }

    @Override
    public HeaderResolver getHeaderResolver() {
        return null;
    }
}

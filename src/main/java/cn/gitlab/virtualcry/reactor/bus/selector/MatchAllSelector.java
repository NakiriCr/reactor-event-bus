package cn.gitlab.virtualcry.reactor.bus.selector;

/**
 * Implementation of {@link Selector} that matches
 * all objects.
 *
 * @author Michael Klishin
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
}

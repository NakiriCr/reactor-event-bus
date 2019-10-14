package cn.gitlab.virtualcry.reactor.bus.filter;

import java.util.Collections;
import java.util.List;

/**
 * A {@link Filter} implementation that returns the first item.
 *
 * @author Stephane Maldini
 * @since 3.2.2
 */
public final class FirstFilter extends AbstractFilter {

	@Override
	public <T> List<T> doFilter(List<T> items, Object key) {
		if (items.isEmpty()) {
			return items;
		} else {
			return Collections.singletonList(items.get(0));
		}
	}
}

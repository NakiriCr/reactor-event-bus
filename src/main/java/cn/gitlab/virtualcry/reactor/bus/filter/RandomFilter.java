package cn.gitlab.virtualcry.reactor.bus.filter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A {@link Filter} implementation that returns a single, randomly selected item.
 *
 * @author Andy Wilkinson
 * @since 3.2.2
 */
public final class RandomFilter extends AbstractFilter {

	private final ThreadLocalRandom random = ThreadLocalRandom.current();

	@Override
	public <T> List<T> doFilter(List<T> items, Object key) {
		if (items.isEmpty()) {
			return items;
		} else {
			return Collections.singletonList(items.get(random.nextInt(items.size())));
		}
	}
}
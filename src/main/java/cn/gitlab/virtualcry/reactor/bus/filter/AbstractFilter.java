package cn.gitlab.virtualcry.reactor.bus.filter;

import java.util.List;
import java.util.Objects;

/**
 * @author VirtualCry
 * @since 3.2.2
 */
abstract class AbstractFilter implements Filter {

	@Override
	public final <T> List<T> filter(List<T> items, Object key) {
		Objects.requireNonNull(items, "'items' must not be null.");
		return doFilter(items, key);
	}

	protected abstract <T> List<T> doFilter(List<T> items, Object key);
}

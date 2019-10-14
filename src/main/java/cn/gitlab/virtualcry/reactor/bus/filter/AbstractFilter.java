package cn.gitlab.virtualcry.reactor.bus.filter;

import cn.gitlab.virtualcry.reactor.bus.support.Assert;

import java.util.List;

/**
 * @author VirtualCry
 * @since 3.2.2
 */
abstract class AbstractFilter implements Filter {

	@Override
	public final <T> List<T> filter(List<T> items, Object key) {
		Assert.notNull(items, "'items' must not be null.");
		return doFilter(items, key);
	}

	protected abstract <T> List<T> doFilter(List<T> items, Object key);
}

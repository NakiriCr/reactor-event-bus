package cn.gitlab.virtualcry.reactor.bus.filter;

import java.util.List;

/**
 * A {@link Filter} implementation that performs no filtering, returning the {@code items} as-is.
 *
 * @author Andy Wilkinson
 * @author Stephane Maldini
 * @since 3.2.2
 */
public final class PassThroughFilter extends AbstractFilter {

	@Override
	public <T> List<T> doFilter(List<T> items, Object key) {
		return items;
	}
}

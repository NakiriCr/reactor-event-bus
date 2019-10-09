package cn.gitlab.virtualcry.reactor.bus.filter;

import cn.gitlab.virtualcry.reactor.bus.registry.Registration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link Filter} implementation that performs no filtering, returning the {@code items} as-is.
 *
 * @author VirtualCry
 *
 */
@SuppressWarnings("unchecked")
public final class DeDuplicationFilter extends AbstractFilter {

	@Override
	public <T> List<T> doFilter(List<T> items, Object key) {
		return items.stream()
				.filter(item -> item instanceof Registration)
				.map(item -> ((Registration) item))
				.collect(Collectors.toMap(Registration::getObject, item -> item, (v1, v2) -> v1))
				.values().stream()
				.map(item -> (T) item)
				.collect(Collectors.toList());
	}
}

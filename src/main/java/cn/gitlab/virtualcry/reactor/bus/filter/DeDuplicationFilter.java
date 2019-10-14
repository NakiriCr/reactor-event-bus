package cn.gitlab.virtualcry.reactor.bus.filter;

import cn.gitlab.virtualcry.reactor.bus.registry.Registration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A {@link Filter} implementation that returns the {@code items} after de-duplication.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
@SuppressWarnings("unchecked")
public final class DeDuplicationFilter extends AbstractFilter {

	@Override
	public <T> List<T> doFilter(List<T> items, Object key) {
		return items.stream()
				.filter(item -> item instanceof Registration)
				.filter(distinctByKey(item -> ((Registration) item).getObject()))
				.collect(Collectors.toList());
	}

	private <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> cache = new HashMap<>();
		return t -> cache.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}

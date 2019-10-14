package cn.gitlab.virtualcry.reactor.bus.filter;

import java.util.List;

/**
 * A {@code Filter} is used to filter a list of items. The nature of the filtering is determined by the
 * implementation.
 *
 * @author Andy Wilkinson
 * @since 3.2.2
 */
public interface Filter {

	/**
	 * Filters the given {@code List} of {@code items}. The {@code key} may be used by an implementation to
	 * influence the filtering.
	 *
	 * @param items The items to filter. Must not be {@code null}.
	 * @param key The key
	 *
	 * @return The filtered items, never {@code null}.
	 *
	 * @throws IllegalArgumentException if {@code items} is null
	 */
	<T> List<T> filter(List<T> items, Object key);
}

package cn.gitlab.virtualcry.reactor.bus.selector;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

/**
 * Responsible for extracting any applicable headers from a key.
 *
 * @author Jon Brisbin
 * @author Stephane Maldini
 * @author VirtualCry
 * @since 3.2.2
 */
public interface HeaderResolver<T> {

	/**
	 * Resolve the headers that might be encoded in a key.
	 *
	 * @param key The key to match.
	 *
	 * @return Any applicable headers. Might be {@literal null}.
	 */
	@Nullable
	Map<String, T> resolve(Object key);

}

package cn.gitlab.virtualcry.reactor.bus.selector;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * Helper methods for creating {@link Selector}s.
 *
 * @author Andy Wilkinson
 * @author Jon Brisbin
 * @author Stephane Maldini
 *
 */
public abstract class Selectors {

	private static final AtomicInteger HASH_CODES = new AtomicInteger(Integer.MIN_VALUE);

	/**
	 * Creates an anonymous {@link Selector}.
	 *
	 * @return a new Selector
	 *
	 * @see ObjectSelector
	 */
	public static Selector anonymous() {
		Object obj = new AnonymousKey();
		return $(obj);
	}

	/**
	 * A short-hand alias for {@link Selectors#anonymous()}.
	 * <p/>
	 * Creates an anonymous {@link Selector}.
	 *
	 * @return a new Selector
	 *
	 * @see ObjectSelector
	 */
	public static Selector $() {
		return anonymous();
	}

	/**
	 * Creates a {@link Selector} based on the given object.
	 *
	 * @param obj
	 * 		The object to use for matching
	 *
	 * @return The new {@link ObjectSelector}.
	 *
	 * @see ObjectSelector
	 */
	public static <T> Selector<T> object(T obj) {
		return new ObjectSelector<>(obj);
	}

	/**
	 * A short-hand alias for {@link Selectors#object}.
	 * <p/>
	 * Creates a {@link Selector} based on the given object.
	 *
	 * @param obj
	 * 		The object to use for matching
	 *
	 * @return The new {@link ObjectSelector}.
	 *
	 * @see ObjectSelector
	 */
	public static <T> Selector<T> $(T obj) {
		return object(obj);
	}

	/**
	 * Creates a {@link Selector} based on the given string format and arguments.
	 *
	 * @param fmt
	 * 		The {@code String.format} style format specification
	 * @param args
	 * 		The format args
	 *
	 * @return The new {@link ObjectSelector}.
	 *
	 * @see ObjectSelector
	 * @see String#format(String, Object...)
	 */
	public static Selector $(String fmt, Object... args) {
		return object(String.format(fmt, args));
	}

	/**
	 * Creates a {@link Selector} based on the given regular expression.
	 *
	 * @param regex
	 * 		The regular expression to compile and use for matching
	 *
	 * @return The new {@link RegexSelector}.
	 *
	 * @see RegexSelector
	 */
	public static Selector regex(String regex) {
		return new RegexSelector(regex);
	}

	/**
	 * A short-hand alias for {@link Selectors#regex(String)}.
	 * <p/>
	 * Creates a {@link Selector} based on the given regular expression.
	 *
	 * @param regex
	 * 		The regular expression to compile and use for matching
	 *
	 * @return The new {@link RegexSelector}.
	 *
	 * @see RegexSelector
	 */
	public static Selector R(String regex) {
		return new RegexSelector(regex);
	}

	/**
	 * Creates a {@link Selector} based on the given class type that matches objects whose type is
	 * assignable according to {@link Class#isAssignableFrom(Class)}.
	 *
	 * @param supertype
	 * 		The supertype to use for matching
	 *
	 * @return The new {@link ClassSelector}.
	 *
	 * @see ClassSelector
	 */
	public static ClassSelector type(Class<?> supertype) {
		return new ClassSelector(supertype);
	}

	/**
	 * A short-hand alias for {@link Selectors#type(Class)}.
	 * <p/>
	 * Creates a {@link Selector} based on the given class type that matches objects whose type is
	 * assignable according to {@link Class#isAssignableFrom(Class)}.
	 *
	 * @param supertype
	 * 		The supertype to compare.
	 *
	 * @return The new {@link ClassSelector}.
	 *
	 * @see ClassSelector
	 */
	public static ClassSelector T(Class<?> supertype) {
		return type(supertype);
	}

	/**
	 * Creates a {@link Selector} based on the given {@link Predicate}.
	 *
	 * @param predicate
	 * 		The {@link Predicate} to delegate to when matching objects.
	 *
	 * @return PredicateSelector
	 *
	 * @see PredicateSelector
	 */
	public static Selector predicate(Predicate<Object> predicate) {
		return new PredicateSelector(predicate);
	}

    /**
     * Creates a {@link Selector} that matches
     * all objects.
     * @return The new {@link MatchAllSelector}
     *
     * @see MatchAllSelector
     */
    public static Selector matchAll() {
        return new MatchAllSelector();
    }

	public static class AnonymousKey {
		private final int hashCode = HASH_CODES.getAndIncrement() << 2;

		@Override
		public int hashCode() {
			return hashCode;
		}
	}
}

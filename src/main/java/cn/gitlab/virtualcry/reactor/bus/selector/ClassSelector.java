package cn.gitlab.virtualcry.reactor.bus.selector;

/**
 * Implementation of {@link Selector} that uses {@link Class#isAssignableFrom(Class)} to determine a match.
 *
 * @author Jon Brisbin
 * @author Andy Wilkinson
 * @author Stephane Maldini
 * @since 3.2.2
 */
public class ClassSelector extends ObjectSelector<Object, Class<?>> {

	/**
	 * Creates a new ClassSelector that will match keys that are the same as, or are a
	 * super type of the given {@code type}, i.e. the key is assignable according to
	 * {@link Class#isAssignableFrom(Class)}.
	 *
	 * @param type The type to match
	 */
	public ClassSelector(Class<?> type) {
		super(type);
	}

	/**
	 * Creates a {@code ClassSelector} based on the given class type that only matches if the
	 * key being matched is assignable according to {@link Class#isAssignableFrom(Class)}.
	 *
	 * @param supertype The supertype to compare.
	 *
	 * @return The new {@link Selector}.
	 */
	public static Selector typeSelector(Class<?> supertype) {
		return new ClassSelector(supertype);
	}

	@Override
	public boolean matches(Object key) {
		return (Class.class.isInstance(key) && getObject().isAssignableFrom((Class<?>) key)) ||
				getObject().isAssignableFrom(key.getClass());
	}
}

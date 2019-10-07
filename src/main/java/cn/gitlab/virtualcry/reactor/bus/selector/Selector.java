package cn.gitlab.virtualcry.reactor.bus.selector;

/**
 * A {@literal Selector} is a wrapper around an arbitrary object.
 *
 * @author Jon Brisbin
 * @author Stephane Maldini
 * @author Andy Wilkinson
 */
public interface Selector<T>  {


	/**
	 * Get the object being used for comparisons and equals checks.
	 *
	 * @return The internal object.
	 */
	Object getObject();

	/**
	 * Indicates whether this Selector matches the {@code key}.
	 *
	 * @param key The key to match
	 *
	 * @return {@code true} if there's a match, otherwise {@code false}.
	 */
	boolean matches(T key);
}

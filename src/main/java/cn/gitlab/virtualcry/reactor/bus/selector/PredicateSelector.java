package cn.gitlab.virtualcry.reactor.bus.selector;

import java.util.function.Predicate;

/**
 * Implementation of {@link Selector} that delegates the work of matching an object to the given {@link Predicate}.
 *
 * @author Jon Brisbin
 */
public class PredicateSelector extends ObjectSelector<Object, Predicate<Object>> {

	public PredicateSelector(Predicate<Object> object) {
		super(object);
	}

	/**
	 * Creates a {@link Selector} based on the given {@link Predicate}.
	 *
	 * @param predicate
	 * 		The {@link Predicate} to delegate to when matching objects.
	 *
	 * @return PredicateSelector
	 */
	public static PredicateSelector predicateSelector(Predicate<Object> predicate) {
		return new PredicateSelector(predicate);
	}

	@Override
	public boolean matches(Object key) {
		return getObject().test(key);
	}
}

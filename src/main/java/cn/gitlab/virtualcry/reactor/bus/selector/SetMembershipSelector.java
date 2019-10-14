package cn.gitlab.virtualcry.reactor.bus.selector;

import java.util.Set;

/**
 * Implementation of {@link Selector} that matches
 * objects on set membership.
 *
 * @author Michael Klishin
 * @author VirtualCry
 * @since 3.2.2
 */
public class SetMembershipSelector implements Selector {
	private final Set set;

	/**
	 * Create a {@link Selector} when the given regex pattern.
	 *
	 * @param set
	 * 		The {@link Set} that will be used for membership checks.
	 */
	public SetMembershipSelector(Set set) {
		this.set = set;
	}

	@Override
	public Object getObject() {
		return this.set;
	}

	@Override
	public boolean matches(Object key) {
		return this.set.contains(key);
	}

	@Override
	public HeaderResolver getHeaderResolver() {
		return null;
	}
}

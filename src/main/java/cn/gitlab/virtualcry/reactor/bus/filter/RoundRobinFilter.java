package cn.gitlab.virtualcry.reactor.bus.filter;

import cn.gitlab.virtualcry.reactor.bus.support.Assert;
import cn.gitlab.virtualcry.reactor.bus.support.loadBalance.LoadBalance;
import cn.gitlab.virtualcry.reactor.bus.support.loadBalance.LoadBalanceStrategy;
import cn.gitlab.virtualcry.reactor.bus.support.loadBalance.LoadBalances;

import java.util.Collections;
import java.util.List;

/**
 * A {@link Filter} implementation that returns a single item. The item is selected
 * using a round-robin algorithm based on the number of times the {@code key} has been
 * passed into the filter.
 *
 * @author Andy Wilkinson
 * @author VirtualCry
 * @since 3.2.2
 */
public final class RoundRobinFilter extends AbstractFilter {

	private final LoadBalance                       loadBalance;

	public RoundRobinFilter() {
		this.loadBalance = LoadBalances.create(LoadBalanceStrategy.ROUND_ROBIN);
	}


	@Override
	public <T> List<T> doFilter(List<T> items, Object key) {
		Assert.notNull(key, "'key' must not be null.");
		if (items.isEmpty())
			return items;
		else
			return Collections.singletonList(loadBalance.get(items, key));
	}
}

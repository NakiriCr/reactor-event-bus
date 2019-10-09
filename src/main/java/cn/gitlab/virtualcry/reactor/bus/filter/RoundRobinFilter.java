/*
 * Copyright (c) 2011-2014 Pivotal Software, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.gitlab.virtualcry.reactor.bus.filter;

import cn.gitlab.virtualcry.reactor.bus.util.loadBalance.LoadBalance;
import cn.gitlab.virtualcry.reactor.bus.util.loadBalance.LoadBalanceStrategy;
import cn.gitlab.virtualcry.reactor.bus.util.loadBalance.LoadBalanceUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A {@link Filter} implementation that returns a single item. The item is selected
 * using a round-robin algorithm based on the number of times the {@code key} has been
 * passed into the filter.
 *
 * @author VirtualCry
 *
 */
public final class RoundRobinFilter extends AbstractFilter {

	private final LoadBalance                       loadBalance;

	public RoundRobinFilter() {
		this.loadBalance = LoadBalanceUtils.createLoadBalance(LoadBalanceStrategy.ROUND_ROBIN);
	}


	@Override
	public <T> List<T> doFilter(List<T> items, Object key) {
		Objects.requireNonNull(key, "'key' must not be null");
		if (items.isEmpty())
			return items;
		else
			return Collections.singletonList(this.loadBalance.get(items, key));
	}
}

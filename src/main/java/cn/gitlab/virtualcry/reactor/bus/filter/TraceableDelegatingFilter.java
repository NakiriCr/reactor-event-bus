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

import org.checkerframework.checker.nullness.qual.NonNull;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Collections;
import java.util.List;

/**
 * @author VirtualCry
 */
public class TraceableDelegatingFilter implements Filter {

	private final Logger                                logger;
	private final Filter 								delegate;

	public TraceableDelegatingFilter(@NonNull Filter delegate) {
		this.logger = Loggers.getLogger(this.getClass());
		this.delegate = delegate;
	}

	@Override
	public <T> List<T> filter(List<T> items, Object key) {
		if(this.logger.isTraceEnabled()) {
			this.logger.trace("filtering {} using key {}", items, key);
		}
		List<T> list = delegate.filter(items, key);
		if(this.logger.isTraceEnabled()) {
			this.logger.trace("items {} matched key {}", (null == items ? Collections.emptyList() : items), key);
		}
		return list;
	}
}

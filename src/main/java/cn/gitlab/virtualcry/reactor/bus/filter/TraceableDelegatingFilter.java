package cn.gitlab.virtualcry.reactor.bus.filter;

import cn.gitlab.virtualcry.reactor.bus.support.Assert;
import org.checkerframework.checker.nullness.qual.NonNull;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Jon Brisbin
 * @author VirtualCry
 * @since 3.2.2
 */
public class TraceableDelegatingFilter implements Filter {

	private final Logger                                logger;
	private final Filter 								delegate;


	public TraceableDelegatingFilter(@NonNull Filter delegate) {
		this.logger = Loggers.getLogger(this.getClass());

		Assert.notNull(delegate, "Delegate Filter cannot be null.");
		this.delegate = delegate;
	}


	@Override
	public <T> List<T> filter(List<T> items, Object key) {
		if(logger.isTraceEnabled()) {
			logger.trace("filtering {} using key {}", items, key);
		}
		List<T> list = delegate.filter(items, key);
		if(logger.isTraceEnabled()) {
			logger.trace("items {} matched key {}", (null == items ? Collections.emptyList() : items), key);
		}
		return list;
	}
}

package cn.gitlab.virtualcry.reactor.bus.selector;

import java.util.Map;

/**
 * A {@link Selector} implementation based on a {@link UriPathTemplate}.
 *
 * @author Jon Brisbin
 * @author Andy Wilkinson
 * @author VirtualCry
 * @since 3.2.2
 *
 * @see UriPathTemplate
 */
public class UriPathSelector extends ObjectSelector<Object, UriPathTemplate> {

	private final HeaderResolver 						headerResolver;


	/**
	 * Create a selector from the given uri template string.
	 *
	 * @param uriPathTmpl The string to compile into a {@link UriPathTemplate}.
	 */
	public UriPathSelector(String uriPathTmpl) {
		super(new UriPathTemplate(uriPathTmpl));
		this.headerResolver = key -> {
			Map<String, Object> headers = getObject().match(key.toString());
			if (null != headers && !headers.isEmpty()) {
				return headers;
			}
			return null;
		};
	}

	/**
	 * Creates a {@link Selector} based on a URI template.
	 *
	 * @param uriTemplate The URI template to compile.
	 *
	 * @return The new {@link Selector}.
	 *
	 * @see UriPathTemplate
	 */
	public static Selector uriPathSelector(String uriTemplate) {
		return new UriPathSelector(uriTemplate);
	}

	@Override
	public boolean matches(Object path) {
		return String.class.isAssignableFrom(path.getClass()) && getObject().matches((String)path);
	}

	@Override
	public HeaderResolver getHeaderResolver() {
		return headerResolver;
	}

}

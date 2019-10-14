package cn.gitlab.virtualcry.reactor.bus.selector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link Selector} implementation based on the given regular expression. Parses it into a {@link Pattern} for
 * efficient matching against keys.
 * <p/>
 * An example of creating a regex Selector would be:
 * <p/>
 * <code>Selectors.R("event([0-9]+)")</code>
 * <p/>
 * This would match keys like:
 * <p/>
 * <code>"event1"</code>, <code>"event23"</code>, or <code>"event9"</code>
 *
 * @author Jon Brisbin
 * @author Andy Wilkinson
 * @author VirtualCry
 * @since 3.2.2
 */
public class RegexSelector extends ObjectSelector<Object, Pattern> {

	private final HeaderResolver 						headerResolver;


	/**
	 * Create a {@link Selector} when the given regex pattern.
	 *
	 * @param pattern
	 * 		The regex String that will be compiled into a {@link Pattern}.
	 */
	public RegexSelector(String pattern) {
		super(Pattern.compile(pattern));
		this.headerResolver = key -> {
			Matcher m = getObject().matcher(key.toString());
			if (m.matches())
				return Stream.iterate(0, i -> i + 1)
						.limit(m.groupCount())
						.collect(Collectors.toMap(i -> "group" + i, m::group));
			else
				return null;
		};
	}

	/**
	 * Creates a {@link Selector} based on the given regular expression.
	 *
	 * @param regex
	 * 		The regular expression to compile.
	 *
	 * @return The new {@link Selector}.
	 */
	public static Selector regexSelector(String regex) {
		return new RegexSelector(regex);
	}

	@Override
	public boolean matches(Object key) {
		return getObject().matcher(String.valueOf(key)).matches();
	}

	@Override
	public HeaderResolver getHeaderResolver() {
		return headerResolver;
	}
}

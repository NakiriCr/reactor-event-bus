package cn.gitlab.virtualcry.reactor.bus;

import cn.gitlab.virtualcry.reactor.bus.support.Assert;
import lombok.Getter;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.Serializable;
import java.util.*;

/**
 * Wrapper for an object that needs to be processed by {@link java.util.function.Consumer}s.
 *
 * @param <T>
 *     The type of the wrapped object
 *
 * @author Jon Brisbin
 * @author Stephane Maldini
 * @author Andy Wilkinson
 * @author VirtualCry
 */
@Getter
public class Event<T> implements Serializable {

    private final UUID                              id
            = UUID.randomUUID();
    private final Date                              creationDate
            = new Date();
    private final Headers                           headers;
    private final T                                 data;

    private volatile Object                         key;
    private volatile Object                         replyTo;


    /**
     * Creates a new Event based on the type T of {@literal data}
     *
     * @param klass
     *     The Class
     */
    public Event(Class<T> klass) {
        this.key = klass;
        this.headers = new Headers();
        this.data = null;
    }


    /**
     * Creates a new Event with the given {@code headers} and {@code data}.
     *
     * @param headers
     *     The headers
     * @param data
     *     The data
     */
    public Event(Headers headers, T data) {
        this.key = data == null ? null : data.getClass();
        this.headers = headers == null ? new Headers() : headers;
        this.data = data;
    }


    /**
     * Creates a new Event with the given {@code data}. The event will have empty headers.
     *
     * @param data
     *     The data
     */
    public Event(T data) {
        this.headers = new Headers();
        this.data = data;
    }


    /**
     * Wrap the given object with an {@link Event}.
     *
     * @param obj
     *     The object to wrap.
     *
     * @return The new {@link Event}.
     */
    public static <T> Event<T> wrap(T obj) {
        return new Event<>(obj);
    }


    /**
     * Wrap the given object with an {@link Event} and set the {@link Event#getReplyTo() replyTo} to the given {@code
     * replyToKey}.
     *
     * @param obj
     *     The object to wrap.
     * @param replyToKey
     *     The key to use as a {@literal replyTo}.
     * @param <T>
     *     The type of the given object.
     *
     * @return The new {@link Event}.
     */
    public static <T> Event<T> wrap(T obj, Object replyToKey) {
        return new Event<>(obj).setReplyTo(replyToKey);
    }


    /**
     * Set the {@code key} that interested parties should send replies to.
     *
     * @param replyTo
     *     The key to use to notify sender of replies.
     *
     * @return {@literal this}
     */
    public Event<T> setReplyTo(Object replyTo) {
        Assert.notNull(replyTo, "ReplyTo cannot be null.");
        this.replyTo = replyTo;
        return this;
    }


    /**
     * Set the key this event is being notified with.
     *
     * @param key
     *     The key used to notify consumers of this event.
     *
     * @return {@literal this}
     */
    public Event<T> setKey(Object key) {
        this.key = key;
        return this;
    }


    /**
     * Create a copy of this event, reusing same data and replyTo
     *
     * @return {@literal event copy}
     */
    public Event<T> copy() {
        return copy(data);
    }

    /**
     * Create a copy of this event, reusing same replyTo
     *
     * @return {@literal event copy}
     */
    public <E> Event<E> copy(E data) {
        if (null != replyTo) {
            return new Event<>( data).setReplyTo(replyTo);
        } else {
            return new Event<>(data);
        }
    }
    

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", headers=" + headers +
                ", replyTo=" + replyTo +
                ", key=" + key +
                ", data=" + data +
                '}';
    }


    /**
     * Headers are a Map-like structure of name-value pairs. Header names are case-insensitive, as determined by {@link
     * String#CASE_INSENSITIVE_ORDER}. A header can be removed by setting its value to {@code null}.
     */
    public static class Headers implements Serializable, Iterable<Tuple2<String, Object>> {

        /**
         * The name of the origin header
         *
         * @see #setOrigin(String)
         * @see #setOrigin(UUID)
         * @see #getOrigin()
         */
        public static final String ORIGIN = "x-reactor-origin";

        private final Object monitor = UUID.randomUUID();
        private final Map<String, Object> headers;

        private Headers(boolean sealed, Map<String, Object> headers) {
            Map<String, Object> copy = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
            copyHeaders(headers, copy);
            if (sealed) {
                this.headers = Collections.unmodifiableMap(copy);
            } else {
                this.headers = copy;
            }
        }

        /**
         * Creates a new Headers instance by copying the contents of the given {@code headers} Map. Note that, as the map is
         * copied, subsequent changes to its contents will have no effect upon the Headers.
         *
         * @param headers
         *     The map to copy.
         */
        public Headers(Map<String, Object> headers) {
            this(false, headers);
        }

        /**
         * Create an empty Headers
         */
        public Headers() {
            this(false, null);
        }

        /**
         * Sets all of the headers represented by entries in the given {@code headers} Map. Any entry with a null value will
         * cause the header matching the entry's name to be removed.
         *
         * @param headers
         *     The map of headers to set.
         *
         * @return {@code this}
         */
        public Headers setAll(Map<String, Object> headers) {
            if (null == headers || headers.isEmpty()) {
                return this;
            } else {
                synchronized (this.monitor) {
                    copyHeaders(headers, this.headers);
                }
            }
            return this;
        }

        /**
         * Set the header value. If {@code value} is {@code null} the header with the given {@code name} will be removed.
         *
         * @param name
         *     The name of the header.
         * @param value
         *     The header's value.
         *
         * @return {@code this}
         */
        public <V> Headers set(String name, V value) {
            synchronized (this.monitor) {
                setHeader(name, value, headers);
            }
            return this;
        }

        /**
         * Set the origin header. The origin is simply a unique id to indicate to consumers where it should send replies. If
         * {@code id} is {@code null} the origin header will be removed.
         *
         * @param id
         *     The id of the origin component.
         *
         * @return {@code this}
         */
        public Headers setOrigin(UUID id) {
            String idString = id == null ? null : id.toString();
            return setOrigin(idString);
        }

        /**
         * Get the origin header
         *
         * @return The origin header, may be {@code null}.
         */
        public String getOrigin() {
            synchronized (this.monitor) {
                return (String) headers.get(ORIGIN);
            }
        }

        /**
         * Set the origin header. The origin is simply a unique id to indicate to consumers where it should send replies. If
         * {@code id} is {@code null} this origin header will be removed.
         *
         * @param id
         *     The id of the origin component.
         *
         * @return {@code this}
         */
        public Headers setOrigin(String id) {
            synchronized (this.monitor) {
                setHeader(ORIGIN, id, headers);
            }
            return this;
        }

        /**
         * Get the value for the given header.
         *
         * @param name
         *     The header name.
         *
         * @return The value of the header, or {@code null} if none exists.
         */
        @SuppressWarnings("unchecked")
        public <V> V get(String name) {
            synchronized (monitor) {
                return (V) headers.get(name);
            }
        }

        /**
         * Determine whether the headers contain a value for the given name.
         *
         * @param name
         *     The header name.
         *
         * @return {@code true} if a value exists, {@code false} otherwise.
         */
        public boolean contains(String name) {
            synchronized (monitor) {
                return headers.containsKey(name);
            }
        }

        /**
         * Get these headers as an unmodifiable {@link Map}.
         *
         * @return The unmodifiable header map
         */
        public Map<String, Object> asMap() {
            synchronized (monitor) {
                return Collections.unmodifiableMap(headers);
            }
        }

        /**
         * Get the headers as a read-only version
         *
         * @return A read-only version of the headers.
         */
        public Headers readOnly() {
            synchronized (monitor) {
                return new Headers(true, headers);
            }
        }

        /**
         * Returns an unmodifiable Iterator over a copy of this Headers' contents.
         */
        @Override
        public Iterator<Tuple2<String, Object>> iterator() {
            synchronized (this.monitor) {
                List<Tuple2<String, Object>> headers = new ArrayList<Tuple2<String, Object>>(this.headers.size());
                for (Map.Entry<String, Object> header : this.headers.entrySet()) {
                    headers.add(Tuples.of(header.getKey(), header.getValue()));
                }
                return Collections.unmodifiableList(headers).iterator();
            }
        }

        @Override
        public String toString() {
            return headers.toString();
        }

        private void copyHeaders(Map<String, Object> source, Map<String, Object> target) {
            if (source != null) {
                for (Map.Entry<String, Object> entry : source.entrySet()) {
                    setHeader(entry.getKey(), entry.getValue(), target);
                }
            }
        }

        private void setHeader(String name, Object value, Map<String, Object> target) {
            if (value == null) {
                target.remove(name);
            } else {
                target.put(name, value);
            }
        }
    }
}

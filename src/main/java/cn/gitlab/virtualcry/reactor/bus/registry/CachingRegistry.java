package cn.gitlab.virtualcry.reactor.bus.registry;

import cn.gitlab.virtualcry.reactor.bus.selector.Selector;
import com.github.benmanes.caffeine.cache.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Implementation of {@link Registry} that uses {@link Cache} to cache
 * registration.
 *
 * @author VirtualCry
 * @since 3.2.2
 */
public class CachingRegistry<K, V> implements Registry<K, V> {

    private final Cache<String,
            Registration<K, ? extends V>>               cacheL1;
    private final Cache<Object,
            List<Registration<K, ? extends V>>>         cacheL2;

    private final boolean                               useL2Cache;
    private final Consumer<K>                           onNotFound;

    private final Consumer<Registration<K,
            ? extends V>>                               onRegister;
    private final Consumer<Registration<K,
            ? extends V>>                               onUnregister;


    CachingRegistry(boolean useL2Cache,
                    Consumer<K> onNotFound,
                    Consumer<Registration<K, ? extends V>> onRegister,
                    Consumer<Registration<K, ? extends V>> onUnregister) {
        this.cacheL1 = Caffeine.newBuilder()
                .writer(new L1RegistrationCacheWriter())
                .removalListener(new L1RegistrationCacheRemovalListener())
                .build();
        this.cacheL2 = Caffeine.newBuilder()
                .maximumSize(1000)
                .softValues()
                .build();
        this.useL2Cache = useL2Cache;
        this.onNotFound = onNotFound;
        this.onRegister = onRegister;
        this.onUnregister = onUnregister;
    }


    @Override
    public Registration<K, V> register(Selector<K> sel, V obj) {
        String key = UUID.randomUUID().toString();
        Registration<K, V> registration = new CacheableRegistration<>(sel, obj);
        this.cacheL1.put(key, registration);
        return registration;
    }

    @Override
    public boolean unregister(K key) {
        final AtomicBoolean modified = new AtomicBoolean(false);
        this.cacheL1.asMap().entrySet().stream()
                .filter(entry -> entry.getValue().getSelector().matches(key))
                .forEach(entry -> {
                    cacheL1.invalidate(entry.getKey());
                    modified.compareAndSet(false, true);
                });
        return modified.get();
    }

    @Override
    public List<Registration<K, ? extends V>> select(K key) {
        List<Registration<K, ? extends V>> selectedRegs = this.cacheL2.getIfPresent(key);
        selectedRegs = selectedRegs == null ? new ArrayList<>() : selectedRegs;
        if (selectedRegs.isEmpty() && this.onNotFound != null)
            this.onNotFound.accept(key);
        return selectedRegs;
    }

    @Override
    public void clear() {
        this.cacheL1.invalidateAll();
    }

    @Override @NonNull
    public Iterator<Registration<K, ? extends V>> iterator() {
        return this.cacheL1.asMap().values().iterator();
    }


    private class L1RegistrationCacheWriter implements
            CacheWriter<String, Registration<K, ? extends V>> {

        @Override
        public void write(@NonNull String key,
                          @NonNull Registration<K, ? extends V> registration) {
            Selector<K> selector = registration.getSelector();
            List<Registration<K, ? extends V>> registrations;
            if (null == (registrations = CachingRegistry.this.cacheL2.getIfPresent(selector.getObject()))) {
                registrations = new ArrayList<>();
                CachingRegistry.this.cacheL2.put(selector.getObject(), registrations);  // sync to validate in L2 cache.
            }
            registrations.add(registration);
            CachingRegistry.this.onRegister.accept(registration);
        }

        @Override
        public void delete(@NonNull String key,
                           @Nullable Registration<K, ? extends V> registration,
                           @NonNull RemovalCause cause) {
            if (registration == null)
                return;
            Selector<K> selector = registration.getSelector();
            CachingRegistry.this.cacheL2.invalidate(selector.getObject());  // sync to invalidate in L2 cache.
        }
    }

    private class L1RegistrationCacheRemovalListener implements
            RemovalListener<String, Registration<K, ? extends V>> {

        @Override
        public void onRemoval(@Nullable String key,
                              @Nullable Registration<K, ? extends V> registration,
                              @NonNull RemovalCause cause) {
            CachingRegistry.this.onUnregister.accept(registration);
        }
    }
}

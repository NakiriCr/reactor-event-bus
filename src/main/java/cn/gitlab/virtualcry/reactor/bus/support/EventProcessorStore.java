package cn.gitlab.virtualcry.reactor.bus.support;

import cn.gitlab.virtualcry.reactor.bus.Event;
import cn.gitlab.virtualcry.reactor.bus.processor.EventProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author VirtualCry
 */
public class EventProcessorStore {

    private final Map<Class<? extends Event>,
            EventProcessor<? extends Event>>    cacheHolder = new ConcurrentHashMap<>();


    @SuppressWarnings("unchecked")
    public <T extends Event> EventProcessor<T> computeIfAbsent(
            Class<T> eventType,
            Function<? super Class<? extends Event>, ? extends EventProcessor<? extends Event>> mappingFunction) {
        return (EventProcessor<T>) cacheHolder.computeIfAbsent(eventType, mappingFunction);
    }


    public Map<Class<? extends Event>, EventProcessor<? extends Event>> getCache() {
        return cacheHolder;
    }

}

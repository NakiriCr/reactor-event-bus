package cn.gitlab.virtualcry.reactor.bus.spec.registry;

import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.support.PayloadConsumer;
import lombok.Builder;

/**
 * Somethings
 *
 * @author VirtualCry
 */
@Builder
public class CachingRegistryComponentSpec implements RegistryComponentSpec {

    private Registry<Object, PayloadConsumer<?>> registry;

    @Override
    public Registry<Object, PayloadConsumer<?>> create() {
        return registry;
    }
}

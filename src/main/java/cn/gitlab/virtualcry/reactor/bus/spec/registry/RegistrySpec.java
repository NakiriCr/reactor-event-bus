package cn.gitlab.virtualcry.reactor.bus.spec.registry;

/**
 * Somethings
 *
 * @author VirtualCry
 */
public interface RegistrySpec {

    static CachingRegistryComponentSpec.CachingRegistryComponentSpecBuilder cachingRegistry() {
        return CachingRegistryComponentSpec.builder();
    }
}

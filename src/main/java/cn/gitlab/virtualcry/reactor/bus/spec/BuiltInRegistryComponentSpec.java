package cn.gitlab.virtualcry.reactor.bus.spec;

import cn.gitlab.virtualcry.reactor.bus.registry.Registries;
import cn.gitlab.virtualcry.reactor.bus.registry.Registry;
import cn.gitlab.virtualcry.reactor.bus.spec.registry.RegistryComponentSpec;
import cn.gitlab.virtualcry.reactor.bus.spec.registry.RegistrySpec;
import reactor.util.Logger;
import reactor.util.Loggers;

/**
 * Somethings
 *
 * @author VirtualCry
 */
public class BuiltInRegistryComponentSpec {

    public static final RegistryComponentSpec CACHING_REGISTRY;


    static {
        CACHING_REGISTRY = RegistrySpec.cachingRegistry()
                .registry(Registries.create(
                        registration -> {
                            final Logger logger = Loggers.getLogger(Registry.class);
                            if (logger.isDebugEnabled())
                                logger.debug("Registered { notify: {}, consumer: {} }.",
                                        registration.getSelector().getObject(),
                                        registration.getObject().getId());
                        },
                        registration -> {
                            final Logger logger = Loggers.getLogger(Registry.class);
                            if (logger.isDebugEnabled())
                                logger.debug("Unregistered { notify: {}, consumer: {} }.",
                                        registration.getSelector().getObject(),
                                        registration.getObject().getId());
                        }
                ))
                .build();
    }
}

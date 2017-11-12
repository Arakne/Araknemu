package fr.quatrevieux.araknemu.core.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of configuration system
 */
final public class DefaultConfiguration implements Configuration {
    final private Map<Class<? extends ConfigurationModule>, ConfigurationModule> modules = new HashMap<>();

    final private Driver driver;

    public DefaultConfiguration(Driver driver) {
        this.driver = driver;
    }

    @Override
    public <M extends ConfigurationModule> M module(Class<M> moduleClass) {
        if (modules.containsKey(moduleClass)) {
            return (M) modules.get(moduleClass);
        }

        M module;

        try {
            module = moduleClass.newInstance();
        } catch (Exception e) {
            throw new Error("Cannot load configuration ", e);
        }

        module.setPool(
            driver.has(module.name())
                ? driver.pool(module.name())
                : new EmptyPool()
        );

        modules.put(moduleClass, module);

        return module;
    }
}

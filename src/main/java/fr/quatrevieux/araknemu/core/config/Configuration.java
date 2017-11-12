package fr.quatrevieux.araknemu.core.config;

/**
 * Base configuration
 */
public interface Configuration {
    /**
     * Get the configuration module from its class
     * @param moduleClass
     * @param <M>
     * @return
     */
    public <M extends ConfigurationModule> M module(Class<M> moduleClass);
}

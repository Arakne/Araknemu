package fr.quatrevieux.araknemu.core.config;

/**
 * Configuration for a module
 */
public interface ConfigurationModule {
    /**
     * Set the configuration pool to the module
     * @param pool
     */
    void setPool(Pool pool);

    /**
     * Get the module name
     * @return
     */
    public String name();
}

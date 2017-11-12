package fr.quatrevieux.araknemu.core.config;

/**
 *
 */
public interface Driver {
    /**
     * Check if the configuration has the key
     * @param key Configuration item to check
     */
    public boolean has(String key);

    /**
     * Get the value of the item
     * @param key The config item key
     * @return The raw value
     */
    public Object get(String key);

    /**
     * Get a pool of config values
     * @param key The pool key
     * @return The pool of values
     */
    public Pool pool(String key);
}

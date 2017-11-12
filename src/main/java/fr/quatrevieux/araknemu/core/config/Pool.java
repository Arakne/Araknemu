package fr.quatrevieux.araknemu.core.config;

/**
 * Pool of configuration items
 */
public interface Pool {
    /**
     * Check if the pool has the key
     * @param key Configuration item to check
     */
    public boolean has(String key);

    /**
     * Get the value of the item
     * @param key The config item key
     * @return Configuration value
     */
    public String get(String key);
}

package fr.quatrevieux.araknemu.core;

/**
 * Base interface for service
 */
public interface Service {
    /**
     * Start the service
     */
    public void boot() throws BootException;

    /**
     * Stop the service
     */
    public void shutdown();
}

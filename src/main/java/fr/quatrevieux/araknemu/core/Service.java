package fr.quatrevieux.araknemu.core;

/**
 * Base interface for service
 *
 * @todo boot exception
 */
public interface Service {
    /**
     * Start the service
     */
    public void boot();

    /**
     * Stop the service
     */
    public void shutdown();
}

package fr.quatrevieux.araknemu.network.adapter;

/**
 * Base interface for handle server
 */
public interface Server {
    /**
     * Start the server
     */
    public void start() throws Exception;

    /**
     * Stop the server
     */
    public void stop() throws Exception;
}

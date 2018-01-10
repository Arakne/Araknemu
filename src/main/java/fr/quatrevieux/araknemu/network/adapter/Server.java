package fr.quatrevieux.araknemu.network.adapter;

import java.io.IOException;

/**
 * Base interface for handle server
 */
public interface Server {
    /**
     * Start the server
     */
    public void start() throws IOException;

    /**
     * Stop the server
     */
    public void stop() throws IOException;
}

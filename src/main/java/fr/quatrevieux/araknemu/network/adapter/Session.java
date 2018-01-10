package fr.quatrevieux.araknemu.network.adapter;

/**
 * Base interface for sessions
 */
public interface Session {
    /**
     * Get the low level IO channel
     */
    public Channel channel();

    /**
     * Write packet to channel
     *
     * @param packet Packet to write
     */
    public void write(Object packet);

    /**
     * Close the session
     */
    public void close();

    /**
     * Check if the session is valid
     */
    public boolean isAlive();
}

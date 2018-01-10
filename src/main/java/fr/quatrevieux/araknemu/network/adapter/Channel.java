package fr.quatrevieux.araknemu.network.adapter;

/**
 * Base interface for handle low level IO on socket
 */
public interface Channel {
    /**
     * Get the channel id
     */
    public long id();

    /**
     * Write message to the channel
     *
     * @param message Message to send
     */
    public void write(Object message);

    /**
     * Close the channel
     */
    public void close();

    /**
     * Check if the channel is alive
     */
    public boolean isAlive();
}

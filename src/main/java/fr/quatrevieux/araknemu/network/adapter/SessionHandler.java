package fr.quatrevieux.araknemu.network.adapter;

/**
 * Base handler for session
 */
public interface SessionHandler<S extends Session> {
    /**
     * The session is opened
     */
    public void opened(S session) throws Exception;

    /**
     * The session is closed
     */
    public void closed(S session) throws Exception;

    /**
     * A message is received from the session
     *
     * @param session The session
     * @param packet The received packet
     */
    public void received(S session, String packet) throws Exception;

    /**
     * A packet is sent to the session
     *
     * @param session The receiver
     * @param packet The sent packet
     */
    public void sent(S session, Object packet) throws Exception;

    /**
     * An error occurs during handling the session
     *
     * @param session The session in error
     * @param cause The cause
     */
    public void exception(S session, Throwable cause);

    /**
     * Create the session from the channel
     */
    public S create(Channel channel);
}

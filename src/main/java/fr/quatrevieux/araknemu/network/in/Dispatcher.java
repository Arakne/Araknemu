package fr.quatrevieux.araknemu.network.in;

/**
 * Dispatch incoming packet to related packet
 * @param <S> The session type
 */
public interface Dispatcher<S> {
    /**
     * Dispatch the packet and handle
     * @param session The current session
     * @param packet The packet to dispatch
     */
    public void dispatch(S session, Packet packet) throws HandlerNotFoundException;
}

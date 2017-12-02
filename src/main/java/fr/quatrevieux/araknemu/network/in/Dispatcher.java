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
     *
     * @throws HandlerNotFoundException When the handler cannot be found for the given packet
     * @throws Exception When error occurs during handle the packet
     */
    public void dispatch(S session, Packet packet) throws Exception;
}

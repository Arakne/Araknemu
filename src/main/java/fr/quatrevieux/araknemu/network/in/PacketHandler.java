package fr.quatrevieux.araknemu.network.in;

/**
 * Handle a given packet type
 * @param <S> Session type
 * @param <P> Packet type to handle
 */
public interface PacketHandler<S, P extends Packet> {
    /**
     * Handle the incoming packet
     * @param packet
     */
    public void handle(S session, P packet);

    /**
     * Get the handled packet class
     * @return
     */
    public Class<P> packet();
}

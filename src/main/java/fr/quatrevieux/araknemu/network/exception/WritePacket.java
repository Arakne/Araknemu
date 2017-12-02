package fr.quatrevieux.araknemu.network.exception;

/**
 * An error which result to an error packet
 */
public interface WritePacket {
    /**
     * Packet to send
     */
    public Object packet();
}

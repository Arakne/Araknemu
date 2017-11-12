package fr.quatrevieux.araknemu.network.in;

/**
 * Exception raised when a packet cannot be identified
 */
public class UndefinedPacketException extends ParsePacketException {
    public UndefinedPacketException(String packet) {
        super(packet, "Undefined packet");
    }
}

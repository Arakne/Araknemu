package fr.quatrevieux.araknemu.network.in;

/**
 * Exception raised when cannot parse a packet
 */
public class ParsePacketException extends RuntimeException {
    final private String packet;

    public ParsePacketException(String packet, String message, Throwable cause) {
        super(message, cause);

        this.packet = packet;
    }

    public ParsePacketException(String packet, String message) {
        this(packet, message, null);
    }

    /**
     * Get the packet
     */
    public String packet() {
        return packet;
    }
}

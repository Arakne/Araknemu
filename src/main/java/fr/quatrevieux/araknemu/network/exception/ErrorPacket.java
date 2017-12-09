package fr.quatrevieux.araknemu.network.exception;

/**
 * Simple error which result of an error response
 */
public class ErrorPacket extends Exception implements WritePacket {
    private Object packet;

    public ErrorPacket(Object packet) {
        this.packet = packet;
    }

    public ErrorPacket(Object packet, Throwable cause) {
        super(cause);
        this.packet = packet;
    }

    @Override
    public Object packet() {
        return packet;
    }
}

package fr.quatrevieux.araknemu.network.exception;

/**
 * Write error packet and close the session
 */
public class CloseWithPacket extends Exception implements CloseSession, WritePacket {
    private Object packet;

    public CloseWithPacket(Object packet) {
        this.packet = packet;
    }

    @Override
    public Object packet() {
        return packet;
    }
}

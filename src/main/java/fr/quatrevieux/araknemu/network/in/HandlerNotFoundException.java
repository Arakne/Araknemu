package fr.quatrevieux.araknemu.network.in;

/**
 * Exception raised when cannot found valid handler
 */
public class HandlerNotFoundException extends RuntimeException {
    public HandlerNotFoundException(Packet packet) {
        super("Cannot found handler for packet " + packet.getClass().getSimpleName());
    }
}

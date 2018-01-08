package fr.quatrevieux.araknemu.game.world.util;

/**
 * Interface for object which can send packets
 */
public interface Sender {
    /**
     * Send the packet
     */
    public void send(Object packet);
}

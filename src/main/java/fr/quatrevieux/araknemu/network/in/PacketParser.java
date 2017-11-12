package fr.quatrevieux.araknemu.network.in;

/**
 * Parse an incoming packet
 */
public interface PacketParser {
    /**
     * Parse an get packet from the incoming data
     *
     * @param input Incoming raw data
     * @return The packet object
     *
     * @throws UndefinedPacketException When cannot identify the packet
     * @throws ParsePacketException When cannot parse the packet
     */
    public Packet parse(String input) throws ParsePacketException;
}

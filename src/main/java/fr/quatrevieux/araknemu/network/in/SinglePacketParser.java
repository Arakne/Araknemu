package fr.quatrevieux.araknemu.network.in;

/**
 * Parser for one packet
 */
public interface SinglePacketParser<P extends Packet> extends PacketParser {
    @Override
    public P parse(String input) throws ParsePacketException;

    /**
     * Get the packet identification code
     * This code is the 2 or 3 first chars of the incoming packet
     */
    public String code();
}

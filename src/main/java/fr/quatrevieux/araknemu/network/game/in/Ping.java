package fr.quatrevieux.araknemu.network.game.in;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Ping request received
 */
final public class Ping implements Packet {
    final static public class Parser implements SinglePacketParser<Ping> {
        @Override
        public Ping parse(String input) throws ParsePacketException {
            return new Ping();
        }

        @Override
        public String code() {
            return "ping";
        }
    }
}

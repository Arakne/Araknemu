package fr.quatrevieux.araknemu.network.in._testpackets;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

public class PacketWithParser implements Packet {
    static public class Parser implements SinglePacketParser<PacketWithParser> {
        @Override
        public PacketWithParser parse(String input) throws ParsePacketException {
            return new PacketWithParser();
        }

        @Override
        public String code() {
            return null;
        }
    }
}

package fr.quatrevieux.araknemu.network.in._testpackets;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

public class OtherPacket implements Packet {
    static public class Parser implements SinglePacketParser<OtherPacket> {
        @Override
        public OtherPacket parse(String input) throws ParsePacketException {
            return new OtherPacket();
        }

        @Override
        public String code() {
            return null;
        }
    }
}

package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Ask for characters count per severs
 */
final public class AskServerList implements Packet {
    static public class Parser implements SinglePacketParser<AskServerList> {
        @Override
        public AskServerList parse(String input) throws ParsePacketException {
            return new AskServerList();
        }

        @Override
        public String code() {
            return "Ax";
        }
    }
}

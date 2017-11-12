package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Ask the position on the login queue
 *
 * @todo Implements queue position
 */
final public class AskQueuePosition implements Packet {
    final static public class Parser implements SinglePacketParser<AskQueuePosition> {
        @Override
        public AskQueuePosition parse(String input) throws ParsePacketException {
            return new AskQueuePosition();
        }

        @Override
        public String code() {
            return "Af";
        }
    }
}

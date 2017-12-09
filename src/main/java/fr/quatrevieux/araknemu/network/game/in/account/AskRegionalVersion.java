package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;

/**
 * Ask for regional version
 */
final public class AskRegionalVersion implements Packet {
    final static public class Parser implements SinglePacketParser<AskRegionalVersion> {
        @Override
        public AskRegionalVersion parse(String input) throws ParsePacketException {
            return new AskRegionalVersion();
        }

        @Override
        public String code() {
            return "AV";
        }
    }
}
